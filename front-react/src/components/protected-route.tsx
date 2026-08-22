import { Navigate, useLocation } from "react-router"
import type { ReactNode } from "react"
import { useMe } from "@/hooks/use-auth"
import { Skeleton } from "@/components/ui/skeleton"

function LoadingScreen() {
  return (
    <div className="mx-auto max-w-3xl space-y-4 p-6">
      <Skeleton className="h-8 w-1/3" />
      <Skeleton className="h-4 w-full" />
      <Skeleton className="h-4 w-2/3" />
    </div>
  )
}

/**
 * 로그인이 필요한 화면을 감싼다.
 *
 * 튕겨낼 때 원래 목적지를 `location.state` 에 담아두고, 로그인 성공 후 그리로 돌려보낸다.
 * 이게 없으면 글쓰기 버튼을 눌렀다가 로그인한 뒤 목록으로 떨어진다.
 *
 * 화면에서 숨기는 것은 UX 이고, 실제 차단은 back 의 401/403 이다.
 */
export function ProtectedRoute({ children }: { children: ReactNode }) {
  const { isLoggedIn, isPending } = useMe()
  const location = useLocation()

  if (isPending) return <LoadingScreen />
  if (!isLoggedIn) return <Navigate to="/login" state={{ from: location }} replace />

  return <>{children}</>
}

/** 이미 로그인한 사람에게 로그인/회원가입 화면을 보여주지 않는다. */
export function GuestRoute({ children }: { children: ReactNode }) {
  const { isLoggedIn, isPending } = useMe()

  if (isPending) return <LoadingScreen />
  if (isLoggedIn) return <Navigate to="/" replace />

  return <>{children}</>
}
