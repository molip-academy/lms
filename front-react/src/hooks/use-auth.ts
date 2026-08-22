import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { ApiError, api } from "@/lib/api"
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
