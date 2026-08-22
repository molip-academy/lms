import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormMessage,
} from "@/components/ui/form"
import { Separator } from "@/components/ui/separator"
import { Skeleton } from "@/components/ui/skeleton"
import { Textarea } from "@/components/ui/textarea"
import { LikeButton } from "@/components/like-button"
import { useMe } from "@/hooks/use-auth"
import {
  useComments,
  useDeleteComment,
  useModifyComment,
  useToggleCommentLike,
  useWriteComment,
} from "@/hooks/use-comments"
import { applyApiError } from "@/lib/form"
import { commentSchema, type CommentInput } from "@/lib/schemas"
import { formatDateTime } from "@/lib/format"
import type { Comment } from "@/lib/types"

export function CommentSection({ postId }: { postId: number }) {
  const { isLoggedIn } = useMe()
  const { data: comments, isPending } = useComments(postId)
  const [editingId, setEditingId] = useState<number | null>(null)

  const write = useWriteComment(postId)
  const modify = useModifyComment(postId)
  const remove = useDeleteComment(postId)
  const toggleLike = useToggleCommentLike(postId)

  const form = useForm<CommentInput>({
    resolver: zodResolver(commentSchema),
    defaultValues: { content: "" },
  })

  const onSubmit = async (values: CommentInput) => {
    try {
      await write.mutateAsync(values)
      form.reset({ content: "" })
    } catch (error) {
      applyApiError(error, form.setError)
    }
  }

  const handleDelete = async (id: number) => {
    try {
      await remove.mutateAsync(id)
      toast.success("댓글을 삭제했습니다.")
    } catch {
      toast.error("댓글을 삭제하지 못했습니다.")
    }
  }

  return (
    <section className="space-y-4">
      <h2 className="font-semibold">
        댓글 {comments ? `${comments.length}개` : ""}
      </h2>

      {isLoggedIn ? (
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-2">
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormControl>
                    <Textarea rows={3} placeholder="댓글을 입력하세요" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            {form.formState.errors.root && (
              <p className="text-sm text-destructive">{form.formState.errors.root.message}</p>
            )}
            <div className="flex justify-end">
              <Button type="submit" size="sm" disabled={form.formState.isSubmitting}>
                댓글 등록
              </Button>
            </div>
          </form>
        </Form>
      ) : (
        <p className="rounded-md border border-dashed p-4 text-center text-sm text-muted-foreground">
          댓글을 쓰려면 로그인이 필요합니다.
        </p>
      )}

      <Separator />

      {isPending ? (
        <div className="space-y-3">
          <Skeleton className="h-16 w-full" />
          <Skeleton className="h-16 w-full" />
        </div>
      ) : comments && comments.length > 0 ? (
        <ul className="space-y-4">
          {comments.map((comment) => (
            <li key={comment.id}>
              <CommentItem
                comment={comment}
                isEditing={editingId === comment.id}
                onStartEdit={() => setEditingId(comment.id)}
                onCancelEdit={() => setEditingId(null)}
                onSave={async (content) => {
                  try {
                    await modify.mutateAsync({ id: comment.id, content })
                    setEditingId(null)
                    toast.success("댓글을 수정했습니다.")
                  } catch {
                    toast.error("댓글을 수정하지 못했습니다.")
                  }
                }}
                onDelete={() => handleDelete(comment.id)}
                onToggleLike={() =>
                  toggleLike.mutate({ id: comment.id, liked: comment.liked })
                }
                canLike={isLoggedIn}
              />
            </li>
          ))}
        </ul>
      ) : (
        <p className="py-6 text-center text-sm text-muted-foreground">첫 댓글을 남겨보세요.</p>
      )}
    </section>
  )
}

function CommentItem({
  comment,
  isEditing,
  onStartEdit,
  onCancelEdit,
  onSave,
  onDelete,
  onToggleLike,
  canLike,
}: {
  comment: Comment
  isEditing: boolean
  onStartEdit: () => void
  onCancelEdit: () => void
  onSave: (content: string) => void
  onDelete: () => void
  onToggleLike: () => void
  canLike: boolean
}) {
  const [draft, setDraft] = useState(comment.content)

  return (
    <div className="space-y-2 rounded-md border p-4">
      <div className="flex items-center justify-between text-sm">
        <span className="font-medium">{comment.author.nickname}</span>
        <span className="text-muted-foreground">{formatDateTime(comment.createDate)}</span>
      </div>

      {isEditing ? (
        <div className="space-y-2">
          <Textarea rows={3} value={draft} onChange={(event) => setDraft(event.target.value)} />
          <div className="flex justify-end gap-2">
            <Button size="sm" variant="outline" onClick={onCancelEdit}>
              취소
            </Button>
            <Button size="sm" onClick={() => onSave(draft)} disabled={draft.trim().length === 0}>
              저장
            </Button>
          </div>
        </div>
      ) : (
        <p className="whitespace-pre-wrap text-sm">{comment.content}</p>
      )}

      <div className="flex items-center gap-2">
        <LikeButton
          liked={comment.liked}
          count={comment.likeCount}
          onToggle={onToggleLike}
          disabled={!canLike}
        />

        {/* 화면에서 숨기는 것은 UX 다. 실제 차단은 back 의 403 이다. */}
        {comment.canModify && !isEditing && (
          <Button size="sm" variant="ghost" onClick={onStartEdit}>
            수정
          </Button>
        )}

        {comment.canDelete && (
          <AlertDialog>
            <AlertDialogTrigger asChild>
              <Button size="sm" variant="ghost">
                삭제
              </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
              <AlertDialogHeader>
                <AlertDialogTitle>댓글을 삭제할까요?</AlertDialogTitle>
                <AlertDialogDescription>삭제한 댓글은 복구할 수 없습니다.</AlertDialogDescription>
              </AlertDialogHeader>
              <AlertDialogFooter>
                <AlertDialogCancel>취소</AlertDialogCancel>
                <AlertDialogAction onClick={onDelete}>삭제</AlertDialogAction>
              </AlertDialogFooter>
            </AlertDialogContent>
          </AlertDialog>
        )}
      </div>
    </div>
  )
}
