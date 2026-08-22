import { useEffect } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { useNavigate, useParams } from "react-router"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { Skeleton } from "@/components/ui/skeleton"
import { Textarea } from "@/components/ui/textarea"
import { useModifyPost, usePost, useWritePost } from "@/hooks/use-posts"
import { applyApiError } from "@/lib/form"
import { postSchema, type PostInput } from "@/lib/schemas"

export function PostFormPage({ mode }: { mode: "create" | "edit" }) {
  const params = useParams<{ id: string }>()
  const id = Number(params.id)
  const navigate = useNavigate()

  const existing = usePost(mode === "edit" ? id : 0)
  const write = useWritePost()
  const modify = useModifyPost(id)

  const form = useForm<PostInput>({
    resolver: zodResolver(postSchema),
    defaultValues: { title: "", content: "" },
  })

  const { reset } = form
  useEffect(() => {
    if (mode === "edit" && existing.data) {
      reset({ title: existing.data.title, content: existing.data.content })
    }
  }, [mode, existing.data, reset])

  const onSubmit = async (values: PostInput) => {
    try {
      if (mode === "create") {
        const created = await write.mutateAsync(values)
        toast.success("글을 등록했습니다.")
        navigate(`/posts/${created.id}`, { replace: true })
      } else {
        await modify.mutateAsync(values)
        toast.success("글을 수정했습니다.")
        navigate(`/posts/${id}`, { replace: true })
      }
    } catch (error) {
      applyApiError(error, form.setError)
    }
  }

  if (mode === "edit" && existing.isPending) {
    return <Skeleton className="h-96 w-full" />
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>{mode === "create" ? "글쓰기" : "글 수정"}</CardTitle>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="title"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>제목</FormLabel>
                  <FormControl>
                    <Input placeholder="제목을 입력하세요" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="content"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>내용</FormLabel>
                  <FormControl>
                    <Textarea rows={14} placeholder="내용을 입력하세요" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {form.formState.errors.root && (
              <p className="text-sm text-destructive">{form.formState.errors.root.message}</p>
            )}

            <div className="flex justify-end gap-2">
              <Button type="button" variant="outline" onClick={() => navigate(-1)}>
                취소
              </Button>
              <Button type="submit" disabled={form.formState.isSubmitting}>
                {mode === "create" ? "등록" : "수정"}
              </Button>
            </div>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
