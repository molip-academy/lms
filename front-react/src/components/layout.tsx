import { Link, Outlet, useNavigate } from "react-router"
import { LogOut, Moon, PenLine, Sun, User } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { useLogout, useMe } from "@/hooks/use-auth"
import { useTheme } from "@/hooks/use-theme"

export function Layout() {
  const { member, isPending } = useMe()
  const logout = useLogout()
  const { theme, toggle } = useTheme()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout.mutateAsync()
    navigate("/")
  }

  return (
    <div className="min-h-screen bg-background">
      <header className="sticky top-0 z-10 border-b bg-background/80 backdrop-blur">
        <div className="mx-auto flex h-14 max-w-4xl items-center justify-between gap-4 px-4">
          <Link to="/" className="font-semibold tracking-tight">
            MOLIP Academy
          </Link>

          <nav className="flex items-center gap-1">
            <Button variant="ghost" size="icon" onClick={toggle} aria-label="테마 전환">
              {theme === "dark" ? <Sun className="size-4" /> : <Moon className="size-4" />}
            </Button>

            {isPending ? (
              <Skeleton className="h-8 w-24" />
            ) : member ? (
              <>
                <Button variant="ghost" size="sm" asChild>
                  <Link to="/posts/new">
                    <PenLine className="size-4" />
                    글쓰기
                  </Link>
                </Button>
                <Button variant="ghost" size="sm" asChild>
                  <Link to="/me">
                    <User className="size-4" />
                    {member.nickname}
                  </Link>
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={handleLogout}
                  disabled={logout.isPending}
                >
                  <LogOut className="size-4" />
                  로그아웃
                </Button>
              </>
            ) : (
              <>
                <Button variant="ghost" size="sm" asChild>
                  <Link to="/login">로그인</Link>
                </Button>
                <Button size="sm" asChild>
                  <Link to="/signup">회원가입</Link>
                </Button>
              </>
            )}
          </nav>
        </div>
      </header>

      <main className="mx-auto max-w-4xl px-4 py-8">
        <Outlet />
      </main>
    </div>
  )
}
