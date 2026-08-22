import type {
  Comment,
  LikeResult,
  Member,
  PageResult,
  Post,
  PostListItem,
} from "./types"

/**
 * back 의 에러 응답(RFC 9457 ProblemDetail)을 그대로 담는다.
 * `errors` 는 @Valid 실패 시의 필드별 메시지다.
 */
export class ApiError extends Error {
  status: number
  errors?: Record<string, string>

  constructor(status: number, message: string, errors?: Record<string, string>) {
    super(message)
    this.name = "ApiError"
    this.status = status
    this.errors = errors
  }
}

/**
 * 인증 token 은 HttpOnly cookie 라 JS 가 읽을 수 없다.
 * `credentials: "include"` 만 붙이면 브라우저가 알아서 실어 보낸다.
 *
 * 배포에서는 Cloudflare Worker 가 /api/* 를 Railway 로 넘겨 same-origin 이 되고,
 * 로컬에서는 Vite dev proxy 가 같은 역할을 한다 (ADR 0001).
 */
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`/api/v1${path}`, {
    credentials: "include",
    headers: init?.body ? { "Content-Type": "application/json" } : undefined,
    ...init,
  })

  if (response.status === 204) return undefined as T

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    throw new ApiError(
      response.status,
      data?.detail ?? "요청을 처리하지 못했습니다.",
      data?.errors,
    )
  }

  return data as T
}

const body = (value: unknown) => JSON.stringify(value)

export const api = {
  signup: (input: { username: string; password: string; nickname: string }) =>
    request<Member>("/members", { method: "POST", body: body(input) }),

  login: (input: { username: string; password: string }) =>
    request<Member>("/auth/login", { method: "POST", body: body(input) }),

  logout: () => request<void>("/auth/logout", { method: "POST" }),

  me: () => request<Member>("/members/me"),

  posts: (page: number, size = 20) =>
    request<PageResult<PostListItem>>(`/posts?page=${page}&size=${size}`),

  post: (id: number) => request<Post>(`/posts/${id}`),

  writePost: (input: { title: string; content: string }) =>
    request<Post>("/posts", { method: "POST", body: body(input) }),

  modifyPost: (id: number, input: { title: string; content: string }) =>
    request<Post>(`/posts/${id}`, { method: "PUT", body: body(input) }),

  deletePost: (id: number) => request<void>(`/posts/${id}`, { method: "DELETE" }),

  comments: (postId: number) => request<Comment[]>(`/posts/${postId}/comments`),

  writeComment: (postId: number, input: { content: string }) =>
    request<Comment>(`/posts/${postId}/comments`, { method: "POST", body: body(input) }),

  modifyComment: (id: number, input: { content: string }) =>
    request<Comment>(`/comments/${id}`, { method: "PUT", body: body(input) }),

  deleteComment: (id: number) => request<void>(`/comments/${id}`, { method: "DELETE" }),

  // 추천은 toggle 이 아니라 POST/DELETE 다. 재시도해도 상태가 뒤집히지 않는다.
  likePost: (id: number) => request<LikeResult>(`/posts/${id}/likes`, { method: "POST" }),
  unlikePost: (id: number) => request<LikeResult>(`/posts/${id}/likes`, { method: "DELETE" }),
  likeComment: (id: number) => request<LikeResult>(`/comments/${id}/likes`, { method: "POST" }),
  unlikeComment: (id: number) => request<LikeResult>(`/comments/${id}/likes`, { method: "DELETE" }),
}
