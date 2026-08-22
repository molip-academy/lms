import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Link, useNavigate } from "react-router"
import { toast } from "sonner"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { useSignup } from "@/hooks/use-auth"
import { applyApiError } from "@/lib/form"
import { signupSchema, type SignupInput } from "@/lib/schemas"

export function SignupPage() {
  const signup = useSignup()
  const navigate = useNavigate()

  const form = useForm<SignupInput>({
    resolver: zodResolver(signupSchema),
    defaultValues: { username: "", password: "", nickname: "" },
  })

  const onSubmit = async (values: SignupInput) => {
    try {
      await signup.mutateAsync(values)
      toast.success("회원가입이 완료되었습니다. 로그인해주세요.")
      navigate("/login", { replace: true })
    } catch (error) {
      applyApiError(error, form.setError)
    }
  }

  return (
    <Card className="mx-auto max-w-sm">
      <CardHeader>
        <CardTitle>회원가입</CardTitle>
        <CardDescription>아이디와 닉네임은 다른 사람과 겹칠 수 없습니다.</CardDescription>
      </CardHeader>
      <CardContent>
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="username"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>아이디</FormLabel>
                  <FormControl>
                    <Input autoComplete="username" {...field} />
                  </FormControl>
                  <FormDescription>영문 소문자와 숫자 4~20자</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="password"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>비밀번호</FormLabel>
                  <FormControl>
                    <Input type="password" autoComplete="new-password" {...field} />
                  </FormControl>
                  <FormDescription>8자 이상</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="nickname"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>닉네임</FormLabel>
                  <FormControl>
                    <Input {...field} />
                  </FormControl>
                  <FormDescription>게시판에 표시되는 이름입니다. 2~20자</FormDescription>
                  <FormMessage />
                </FormItem>
              )}
            />

            {form.formState.errors.root && (
              <p className="text-sm text-destructive">{form.formState.errors.root.message}</p>
            )}

            <Button type="submit" className="w-full" disabled={form.formState.isSubmitting}>
              회원가입
            </Button>

            <p className="text-center text-sm text-muted-foreground">
              이미 계정이 있으신가요?{" "}
              <Link to="/login" className="underline underline-offset-4">
                로그인
              </Link>
            </p>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
