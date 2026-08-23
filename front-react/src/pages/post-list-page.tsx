import { Link, useSearchParams } from "react-router"
import { Eye, MessageSquare, ThumbsUp } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination"
import { Skeleton } from "@/components/ui/skeleton"
import { loadErrorMessage } from "@/lib/load-error"
import { usePosts } from "@/hooks/use-posts"
import { formatDate } from "@/lib/format"

export function PostListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const page = Math.max(1, Number(searchParams.get("page") ?? 1))
  const { data, isPending, isError, error } = usePosts(page)

  if (isPending) {
    return (
      <div className="space-y-3">
        {Array.from({ length: 5 }).map((_, index) => (
          <Skeleton key={index} className="h-20 w-full" />
        ))}
      </div>
    )
  }

  if (isError) {
    return (
      <p className="text-center text-muted-foreground">
        {loadErrorMessage(error, "글 목록을 불러오지 못했습니다.")}
      </p>
    )
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold tracking-tight">자유게시판</h1>
        <p className="text-sm text-muted-foreground">전체 {data.totalElements}개의 글</p>
      </div>

      {data.content.length === 0 ? (
        <p className="py-12 text-center text-muted-foreground">아직 글이 없습니다.</p>
      ) : (
        <ul className="space-y-3">
          {data.content.map((post) => (
            <li key={post.id}>
              <Link to={`/posts/${post.id}`}>
                <Card className="transition-colors hover:bg-accent/50">
                  <CardContent className="space-y-2">
                    <h2 className="line-clamp-1 font-medium">{post.title}</h2>
                    <div className="flex flex-wrap items-center gap-x-3 gap-y-1 text-sm text-muted-foreground">
                      <span>{post.author.nickname}</span>
                      <span>{formatDate(post.createDate)}</span>
                      <span className="flex items-center gap-1">
                        <Eye className="size-3.5" />
                        {post.viewCount}
                      </span>
                      <span className="flex items-center gap-1">
                        <ThumbsUp className="size-3.5" />
                        {post.likeCount}
                      </span>
                      <span className="flex items-center gap-1">
                        <MessageSquare className="size-3.5" />
                        {post.commentCount}
                      </span>
                    </div>
                  </CardContent>
                </Card>
              </Link>
            </li>
          ))}
        </ul>
      )}

      {data.totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                href="#"
                aria-disabled={page <= 1}
                className={page <= 1 ? "pointer-events-none opacity-50" : undefined}
                onClick={(event) => {
                  event.preventDefault()
                  if (page > 1) setSearchParams({ page: String(page - 1) })
                }}
              />
            </PaginationItem>

            {Array.from({ length: data.totalPages }).map((_, index) => (
              <PaginationItem key={index}>
                <PaginationLink
                  href="#"
                  isActive={page === index + 1}
                  onClick={(event) => {
                    event.preventDefault()
                    setSearchParams({ page: String(index + 1) })
                  }}
                >
                  {index + 1}
                </PaginationLink>
              </PaginationItem>
            ))}

            <PaginationItem>
              <PaginationNext
                href="#"
                aria-disabled={page >= data.totalPages}
                className={page >= data.totalPages ? "pointer-events-none opacity-50" : undefined}
                onClick={(event) => {
                  event.preventDefault()
                  if (page < data.totalPages) setSearchParams({ page: String(page + 1) })
                }}
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  )
}
