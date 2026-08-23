import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { ApiError, NetworkError, api } from "@/lib/api"
import type { Member } from "@/lib/types"

export const ME_QUERY_KEY = ["me"] as const

/**
 * 인증 cookie 가 HttpOnly 라 JS 는 token 을 읽을 수 없다.
 * 로그인 여부는 이 query 의 성공/401 로만 판정된다.
 */
export function useMe() {
  const query = useQuery<Member | null>({
    queryKey: ME_QUERY_KEY,
    queryFn: async () => {
      try {
        return await api.me()
      } catch (error) {
        // 401 은 "비로그인" 이라는 정상적인 답이므로 에러로 다루지 않는다.
        if (error instanceof ApiError && error.status === 401) return null
        throw error
      }
    },
    staleTime: 1000 * 60 * 5,
    retry: false,
  })

  return {
    ...query,
    member: query.data ?? null,
    isLoggedIn: query.data != null,
    /**
     * 로그인 여부를 **판정할 수 없는** 상태다. "비로그인" 과 반드시 구분해야 한다.
     *
     * token 이 HttpOnly cookie 라 JS 는 서버에 물어보는 것 말고 로그인 여부를 알 방법이 없다.
     * 연결이 끊기면 그 유일한 수단이 막히므로, 이때 비로그인으로 처리하면
     * 로그인해 둔 사람에게 로그아웃된 것처럼 보인다.
     */
    isAuthUnknown: query.error instanceof NetworkError,
  }
}

export function useLogin() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: api.login,
    onSuccess: (member) => {
      queryClient.setQueryData(ME_QUERY_KEY, member)
    },
  })
}

export function useSignup() {
  return useMutation({ mutationFn: api.signup })
}

export function useLogout() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: api.logout,
    onSuccess: () => {
      queryClient.setQueryData(ME_QUERY_KEY, null)
      // 추천 여부(liked)와 수정/삭제 가능 여부가 로그인 주체에 따라 달라지므로 전부 다시 받는다.
      queryClient.invalidateQueries()
    },
  })
}
