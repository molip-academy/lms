# front-react

Vite 기반 React web frontend module이다.

## Stack

Vite + React 19 + TypeScript / Tailwind v4 + shadcn/ui (`zinc`) / React Router / TanStack Query /
react-hook-form + zod / Pretendard dynamic subset

## 구조

```
src/
├── components/ui/     shadcn/ui
├── components/        layout, protected-route, comment-section, like-button
├── hooks/             use-auth, use-posts, use-comments, use-theme
├── lib/               api, types, schemas, form, format
└── pages/             login, signup, me, post-list, post-detail, post-form
worker/index.ts        /api/* 를 Railway 로 proxy (ADR 0001)
```

## 규칙

- 인증 cookie 가 `HttpOnly` 라 **JS 는 token 을 읽을 수 없다.**
  로그인 여부는 앱 부팅 시 `GET /api/v1/members/me` 의 200/401 로만 판정한다 (`useMe`).
- `src/lib/schemas.ts` 의 zod 제약은 back 의 `@Valid` 와 1:1 이다. 한쪽만 바꾸지 않는다.
- 화면에서 수정/삭제 버튼을 숨기는 것은 UX 다. 실제 차단은 back 의 403 이다.
- 추천은 toggle 이 아니라 POST/DELETE 다. 그래야 낙관적 업데이트의 rollback 방향이 확정된다.

## Scripts

```bash
npm run dev        # Vite dev server (/api 는 localhost:8080 으로 proxy)
npm run build      # tsc -b && vite build
npm run cf:dev     # Worker 까지 포함해 로컬에서 실행
npm run cf:deploy  # build 후 Cloudflare 배포
```
