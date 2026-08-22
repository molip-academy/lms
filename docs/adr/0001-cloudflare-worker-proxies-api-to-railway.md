# Cloudflare Worker가 `/api/*`를 Railway로 proxy한다

front는 Cloudflare Workers(Static Assets), back은 Railway(싱가폴)에 각각 배포되어 원래는 origin이 다르다. Worker의 `fetch` handler가 `/api/*` 요청을 Railway로 그대로 넘기도록 해서 브라우저 입장에서 **front와 API를 같은 origin**으로 만들었다. 이 선택 하나가 인증 구조 전체를 결정한다 — 인증 쿠키가 third-party가 아닌 first-party가 되므로, Safari ITP나 브라우저의 third-party cookie 차단에 걸리지 않고 `HttpOnly` cookie를 쓸 수 있다.

## Consequences

- **proxy를 걷어내면 인증이 통째로 깨진다.** `HttpOnly` cookie 방식(ADR 없음, `back` 설정 참조)은 same-origin을 전제로 한다. 나중에 front를 다른 방식으로 배포하려면 인증 방식부터 다시 설계해야 한다.
- Worker는 `Cookie`와 `Set-Cookie` header를 변형 없이 통과시켜야 한다. 여기에 손대면 로그인이 조용히 실패한다.
- custom domain을 보유하지 않아 `*.workers.dev`로 서비스한다. proxy 덕분에 custom domain 없이도 인증이 동작하므로, domain 확보는 인증과 무관한 별개 문제다.
- Railway URL(`*.up.railway.app`)은 공개된 채로 둔다. proxy를 우회한 직접 호출이 가능하지만 그 경로는 cross-origin이라 cookie가 붙지 않아 익명 접근만 된다. 대신 **`h2-console`은 `prod` profile에서 반드시 꺼야 한다** — 열려 있으면 인증 없이 DB 전체가 노출된다.
