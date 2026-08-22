import { BrowserRouter, Route, Routes } from "react-router"
import { Layout } from "@/components/layout"
import { GuestRoute, ProtectedRoute } from "@/components/protected-route"
import { LoginPage } from "@/pages/login-page"
import { MePage } from "@/pages/me-page"
import { PostDetailPage } from "@/pages/post-detail-page"
import { PostFormPage } from "@/pages/post-form-page"
import { PostListPage } from "@/pages/post-list-page"
import { SignupPage } from "@/pages/signup-page"

export function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route index element={<PostListPage />} />
          <Route path="posts/:id" element={<PostDetailPage />} />

          <Route
            path="posts/new"
            element={
              <ProtectedRoute>
                <PostFormPage mode="create" />
              </ProtectedRoute>
            }
          />
          <Route
            path="posts/:id/edit"
            element={
              <ProtectedRoute>
                <PostFormPage mode="edit" />
              </ProtectedRoute>
            }
          />
          <Route
            path="me"
            element={
              <ProtectedRoute>
                <MePage />
              </ProtectedRoute>
            }
          />

          <Route
            path="login"
            element={
              <GuestRoute>
                <LoginPage />
              </GuestRoute>
            }
          />
          <Route
            path="signup"
            element={
              <GuestRoute>
                <SignupPage />
              </GuestRoute>
            }
          />

          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

function NotFound() {
  return <p className="py-16 text-center text-muted-foreground">페이지를 찾을 수 없습니다.</p>
}
