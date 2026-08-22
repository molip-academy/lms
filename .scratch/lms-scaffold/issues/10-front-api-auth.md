# front: API client + 인증 상태 + 보호 라우트

Status: done
Blocked by: 09

- fetch wrapper: 항상 `credentials: 'include'`, 에러 응답의 `ProblemDetail`을 파싱해 throw
- zod schema를 back의 `@Valid` 제약과 1:1로 맞춘다 (spec.md 검증 규칙 표)
- 인증 상태: 앱 부팅 시 `GET /api/v1/members/me` 1회. 200이면 로그인, 401이면 비로그인.
  TanStack Query에 캐시하고 로그인/로그아웃 시 무효화. cookie가 `HttpOnly`라 JS가 token을 읽을 수 없다.
- `<ProtectedRoute>` wrapper: me 로딩 중 spinner, 401이면 `/login`으로 redirect하며
  `location.state`에 원래 목적지를 담는다. 로그인 성공 시 그리로 복귀.
- 비로그인 전용 라우트(`/login`, `/signup`)는 이미 로그인 상태면 `/`로.
- 레이아웃: 헤더(로고, 다크모드 toggle, 로그인/로그아웃/내정보)
