import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { api } from "@/lib/api"
import type { Comment, LikeResult } from "@/lib/types"
import { postKeys } from "./use-posts"

export const commentKeys = {
  list: (postId: number) => ["comments", postId] as const,
}

export function useComments(postId: number) {
  return useQuery({
    queryKey: commentKeys.list(postId),
    queryFn: () => api.comments(postId),
  })
}

export function useWriteComment(postId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: (input: { content: string }) => api.writeComment(postId, input),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: commentKeys.list(postId) })
      // 목록의 댓글 수가 바뀐다.
      queryClient.invalidateQueries({ queryKey: postKeys.all })
    },
  })
}

export function useModifyComment(postId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: ({ id, content }: { id: number; content: string }) =>
      api.modifyComment(id, { content }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: commentKeys.list(postId) }),
  })
}

export function useDeleteComment(postId: number) {
  const queryClient = useQueryClient()
  return useMutation({
    mutationFn: api.deleteComment,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: commentKeys.list(postId) })
      queryClient.invalidateQueries({ queryKey: postKeys.all })
    },
  })
}

/** 글 추천과 같은 낙관적 업데이트 패턴을 쓴다. */
export function useToggleCommentLike(postId: number) {
  const queryClient = useQueryClient()
  const key = commentKeys.list(postId)

  return useMutation({
    mutationFn: ({ id, liked }: { id: number; liked: boolean }) =>
      liked ? api.unlikeComment(id) : api.likeComment(id),

    onMutate: async ({ id, liked }) => {
      await queryClient.cancelQueries({ queryKey: key })
      const previous = queryClient.getQueryData<Comment[]>(key)
      const delta = liked ? -1 : 1

      queryClient.setQueryData<Comment[]>(key, (old) =>
        old?.map((comment) =>
          comment.id === id
            ? { ...comment, liked: !liked, likeCount: comment.likeCount + delta }
            : comment,
        ),
      )

      return { previous }
    },

    onError: (_error, _variables, context) => {
      if (context?.previous) queryClient.setQueryData(key, context.previous)
    },

    onSuccess: (result: LikeResult, { id }) => {
      queryClient.setQueryData<Comment[]>(key, (old) =>
        old?.map((comment) =>
          comment.id === id
            ? { ...comment, liked: result.liked, likeCount: result.likeCount }
            : comment,
        ),
      )
    },
  })
}
