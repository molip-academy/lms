import { Link, useNavigate, useParams } from "react-router"
import { Eye } from "lucide-react"
import { toast } from "sonner"
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
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { Skeleton } from "@/components/ui/skeleton"
import { CommentSection } from "@/components/comment-section"
import { LikeButton } from "@/components/like-button"
import { useMe } from "@/hooks/use-auth"
import { useDeletePost, usePost, useTogglePostLike } from "@/hooks/use-posts"
import { formatDateTime } from "@/lib/format"
import { loadErrorMessage } from "@/lib/load-error"

export function PostDetailPage() {
  const { id: rawId } = useParams<{ id: string }>()
  const id = Number(rawId)
  const navigate = useNavigate()
  const { isLoggedIn } = useMe()

  const { data: post, isPending, isError, error } = usePost(id)
  const toggleLike = useTogglePostLike(id)
  const remove = useDeletePost()

  if (isPending) {
    return (
      <div className="space-y-4">
        <Skeleton className="h-10 w-2/3" />
        <Skeleton className="h-64 w-full" />
      </div>
    )
  }

  if (isError || !post) {
    return (
      <div className="space-y-4 py-12 text-center">
        <p className="text-muted-foreground">
          {loadErrorMessage(error, "글을 찾을 수 없습니다.")}
        </p>
        <Button variant="outline" asChild>
          <Link to="/">목록으로</Link>
        </Button>
      </div>
    )
  }

  const handleDelete = async () => {
    try {
      await remove.mutateAsync(id)
      toast.success("글을 삭제했습니다.")
      navigate("/", { replace: true })
    } catch {
      toast.error("글을 삭제하지 못했습니다.")
    }
  }

  return (
    <article className="space-y-6">
      <Card>
        <CardHeader className="space-y-3">
          <h1 className="text-2xl font-semibold tracking-tight">{post.title}</h1>
          <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-sm text-muted-foreground">
            <span>{post.author.nickname}</span>
            <span>{formatDateTime(post.createDate)}</span>
            <span className="flex items-center gap-1">
              <Eye className="size-3.5" />
              {post.viewCount}
            </span>
          </div>
        </CardHeader>

        <CardContent className="space-y-6">
          <p className="whitespace-pre-wrap leading-relaxed">{post.content}</p>

          <Separator />

          <div className="flex flex-wrap items-center justify-between gap-2">
            <LikeButton
              liked={post.liked}
              count={post.likeCount}
              onToggle={() => toggleLike.mutate(post.liked)}
              disabled={!isLoggedIn}
              size="default"
            />

            <div className="flex gap-2">
              <Button variant="outline" asChild>
                <Link to="/">목록</Link>
              </Button>

              {/* 서버가 내려준 권한 플래그로 그린다. 실제 차단은 back 의 403 이다. */}
              {post.canModify && (
                <Button variant="outline" asChild>
                  <Link to={`/posts/${post.id}/edit`}>수정</Link>
                </Button>
              )}

              {post.canDelete && (
                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <Button variant="destructive">삭제</Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>글을 삭제할까요?</AlertDialogTitle>
                      <AlertDialogDescription>
                        글에 달린 댓글도 함께 삭제되며 복구할 수 없습니다.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>취소</AlertDialogCancel>
                      <AlertDialogAction onClick={handleDelete}>삭제</AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      <CommentSection postId={id} />
    </article>
  )
}
