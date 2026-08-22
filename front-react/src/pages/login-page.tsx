import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { Link, useLocation, useNavigate } from "react-router"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { useLogin } from "@/hooks/use-auth"
import { applyApiError } from "@/lib/form"
import { loginSchema, type LoginInput } from "@/lib/schemas"

export function LoginPage() {
  const login = useLogin()
  const navigate = useNavigate()
  const location = useLocation()

  // ProtectedRoute 가 담아둔 원래 목적지. 없으면 목록으로 보낸다.
  const from = (location.state as { from?: { pathname: string } } | null)?.from?.pathname ?? "/"

  const form = useForm<LoginInput>({
    resolver: zodResolver(loginSchema),
    defaultValues: { username: "", password: "" },
  })

  const onSubmit = async (values: LoginInput) => {
    try {
      await login.mutateAsync(values)
      navigate(from, { replace: true })
    } catch (error) {
      applyApiError(error, form.setError)
    }
  }

  return (
    <Card className="mx-auto max-w-sm">
      <CardHeader>
        <CardTitle>로그인</CardTitle>
        <CardDescription>MOLIP Academy 계정으로 로그인합니다.</CardDescription>
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
                    <Input type="password" autoComplete="current-password" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {form.formState.errors.root && (
              <p className="text-sm text-destructive">{form.formState.errors.root.message}</p>
            )}

            <Button type="submit" className="w-full" disabled={form.formState.isSubmitting}>
              로그인
            </Button>

            <p className="text-center text-sm text-muted-foreground">
              계정이 없으신가요?{" "}
              <Link to="/signup" className="underline underline-offset-4">
                회원가입
              </Link>
            </p>
          </form>
        </Form>
      </CardContent>
    </Card>
  )
}
