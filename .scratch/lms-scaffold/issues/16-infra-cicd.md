# infra: GitHub Actions workflow 2개

Status: done
Blocked by: 15

`paths` filter로 back/front를 독립 배포한다.

- `.github/workflows/back.yml` — `back/**` 변경 시.
  PR: `./gradlew test` (JDK 25 setup). `main` push: test 통과 후 Railway CLI로 배포.
- `.github/workflows/front.yml` — `front-react/**` 변경 시.
  PR: `npm ci && npm run build`. `main` push: build 후 `cloudflare/wrangler-action`으로 배포.

**test를 배포 gate로 건다.** test 실패 시 배포하지 않는다.

필요한 secret: `RAILWAY_TOKEN`, `CLOUDFLARE_API_TOKEN`, `CLOUDFLARE_ACCOUNT_ID`.
발급은 사람이 해야 하므로 완료 후 안내한다.
