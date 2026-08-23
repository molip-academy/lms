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
 * 요청이 back 까지 닿지도 못한 경우다. `fetch` 는 이럴 때 TypeError 를 던지는데,
 * 그대로 두면 응답이 온 실패(ApiError)와 구분할 수 없어 호출부가 전부 오판한다.
 *
 * PWA 로 설치하면 offline 에서 앱을 여는 일이 실제로 생기므로 별도 타입으로 세운다.
 */
export class NetworkError extends Error {
  constructor() {
    super("네트워크에 연결할 수 없습니다. 연결 상태를 확인해주세요.")
    this.name = "NetworkError"
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
  let response: Response
  try {
    response = await fetch(`/api/v1${path}`, {
      credentials: "include",
      headers: init?.body ? { "Content-Type": "application/json" } : undefined,
      ...init,
    })
  } catch {
    // fetch 가 던지는 건 연결 실패뿐이다. HTTP 에러는 아래에서 status 로 걸러진다.
    throw new NetworkError()
  }

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
