# infra: Cloudflare Worker (Static Assets + /api proxy)

Status: done
Blocked by: 13

ADR 0001을 구현한다.

- `front-react/wrangler.jsonc`: Workers + Static Assets, SPA fallback(`not_found_handling: single-page-application`)
- Worker `fetch` handler: `/api/*` 요청을 `env.BACKEND_URL`로 proxy.
  `Cookie`/`Set-Cookie` header를 **변형 없이** 통과시킨다. 여기 손대면 로그인이 조용히 깨진다.
- `BACKEND_URL`은 `vars`에 평문 (비밀이 아님).
- custom domain 없음 → `*.workers.dev`
- `wrangler dev`로 로컬에서도 같은 경로 모양이 동작해야 한다.
