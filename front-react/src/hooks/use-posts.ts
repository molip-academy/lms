import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api } from "@/lib/api"
import type { LikeResult, PageResult, Post, PostListItem } from "@/lib/types"

export const postKeys = {
  all: ["posts"] as const,
  list: (page: number) => ["posts", "list", page] as const,
  detail: (id: number) => ["posts", "detail", id] as const,
}

export function usePosts(page: number) {
  return useQuery({
    queryKey: postKeys.list(page),
    queryFn: () => api.posts(page),
  })
}

export function usePost(id: number) {
  return useQuery({
    queryKey: postKeys.detail(id),
    queryFn: () => api.post(id),
  })
}

export function useWritePost() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: api.writePost,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: postKeys.all }),
  })
}

export function useModifyPost(id: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (input: { title: string; content: string }) => api.modifyPost(id, input),
    onSuccess: (post) => {
      queryClient.setQueryData(postKeys.detail(id), post)
      queryClient.invalidateQueries({ queryKey: postKeys.all })
    },
  })
}

export function useDeletePost() {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: api.deletePost,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: postKeys.all }),
  })
}

/**
 * 추천은 즉시 화면에 반영하고, 실패하면 되돌린다.
 *
 * back 이 toggle 이 아니라 POST/DELETE 로 나뉘어 있어서 되돌릴 방향이 항상 확정적이다.
 * toggle 이었다면 실패한 요청이 서버 상태를 바꿨는지 알 수 없어 rollback 이 성립하지 않는다.
 */
export function useTogglePostLike(id: number) {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (liked: boolean) => (liked ? api.unlikePost(id) : api.likePost(id)),

    onMutate: async (liked: boolean) => {
      await queryClient.cancelQueries({ queryKey: postKeys.all })

      const previousDetail = queryClient.getQueryData<Post>(postKeys.detail(id))
      const previousLists = queryClient.getQueriesData<PageResult<PostListItem>>({
        queryKey: ["posts", "list"],
      })

      const delta = liked ? -1 : 1

      queryClient.setQueryData<Post>(postKeys.detail(id), (old) =>
        old ? { ...old, liked: !liked, likeCount: old.likeCount + delta } : old,
      )

      for (const [key] of previousLists) {
        queryClient.setQueryData<PageResult<PostListItem>>(key, (old) =>
          old
            ? {
                ...old,
                content: old.content.map((item) =>
                  item.id === id
                    ? { ...item, liked: !liked, likeCount: item.likeCount + delta }
                    : item,
                ),
              }
            : old,
        )
      }

      return { previousDetail, previousLists }
    },

    onError: (_error, _liked, context) => {
      if (context?.previousDetail) {
        queryClient.setQueryData(postKeys.detail(id), context.previousDetail)
      }
      for (const [key, data] of context?.previousLists ?? []) {
        queryClient.setQueryData(key, data)
      }
    },

    // 서버가 준 값으로 확정한다. 낙관적 계산이 어긋났더라도 여기서 맞춰진다.
    onSuccess: (result: LikeResult) => {
      queryClient.setQueryData<Post>(postKeys.detail(id), (old) =>
        old ? { ...old, liked: result.liked, likeCount: result.likeCount } : old,
      )
    },
  })
}
