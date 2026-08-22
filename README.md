# MOLIP Academy LMS

| 환경 | URL |
| --- | --- |
| web | https://molip-academy-lms.jangka512.workers.dev |
| api | https://back-production-cbf2.up.railway.app (Railway, Southeast Asia) |

`/api/*` 는 Cloudflare Worker 가 Railway 로 넘기므로, front 에서는 same-origin 으로 보인다.

## Repository structure

| Directory | Stack | Target |
| --- | --- | --- |
| `back/` | 코프링 (Kotlin + Spring Boot 4.1.1, JDK 25) | backend |
| `front-react/` | Vite + React + Cloudflare Workers | web |
| `front-kmp/` | KMP | Android, iOS (미착수) |
| `infra/` | Terraform | infrastructure (미착수) |

용어는 [CONTEXT.md](./CONTEXT.md)를, 되돌리기 어려운 결정은 [docs/adr/](./docs/adr/)을 참고한다.

## 로컬 실행

두 개를 같이 띄운다. front 의 dev proxy 가 `/api` 를 back 으로 넘긴다.

```bash
cd back && ./gradlew bootRun
```

```bash
cd front-react && npm install && npm run dev
```

- front: http://localhost:5173
- back: http://localhost:8080
- h2-console: http://localhost:8080/h2-console (dev profile 에서만 열린다)

### Sample 계정

`BaseInitData` 가 만든다. password 는 전부 `password123` 이다.

| username | role | nickname |
| --- | --- | --- |
| `admin` | ADMIN | 관리자 |
| `user1` ~ `user4` | USER | 유저1 ~ 유저4 |

## 테스트

```bash
cd back && ./gradlew test
```

MockMvc 로 실제 요청을 보내고 `@Transactional` 로 rollback 한다. mocking 하지 않는다.
이 test 가 GitHub Actions 의 배포 gate 다.

## 배포

`main` 에 push 하면 변경된 쪽만 배포된다 (`paths` filter).

- `back/**` → test 통과 후 Railway (싱가폴), multi-stage Dockerfile
- `front-react/**` → build 후 Cloudflare Workers (Static Assets)

Cloudflare Worker 가 `/api/*` 를 Railway 로 proxy 해서 front 와 API 를 같은 origin 으로 만든다.
인증 cookie 가 first-party 가 되는 것이 이 구조의 목적이다. 자세한 내용은
[ADR 0001](./docs/adr/0001-cloudflare-worker-proxies-api-to-railway.md) 을 참고한다.

### 필요한 설정

배포 전에 사람이 직접 해야 한다.

**GitHub repository secrets**

| 이름 | 용도 |
| --- | --- |
| `RAILWAY_TOKEN` | Railway 배포 |
| `CLOUDFLARE_API_TOKEN` | Cloudflare Workers 배포 |
| `CLOUDFLARE_ACCOUNT_ID` | Cloudflare 계정 식별 |

**Railway 환경변수**

| 이름 | 값 |
| --- | --- |
| `JWT_SECRET` | 32자 이상의 임의 문자열 |
| `SPRING_PROFILES_ACTIVE` | `prod` (Dockerfile 기본값) |

**`front-react/wrangler.jsonc`**

`vars.BACKEND_URL` 은 이미 실제 Railway URL 로 설정되어 있다. 비밀이 아니므로 평문으로 둔다.

> **주의**: `back` 서비스는 반드시 **replica 1개**로 유지한다.
> H2 file DB 는 컨테이너 로컬 파일이므로 replica 가 2개 이상이면 각자 다른 DB 를 갖게 되어
> 로그인한 회원이 요청마다 사라지는 것처럼 보인다.
> `railway service scale --service back southeast-asia=1` 로 확인한다.

> **운영 DB 주의**: 운영도 volume 없는 H2 file DB 라서 **재배포마다 데이터가 전부 사라지고**
> sample data 가 다시 깔린다. 의도된 선택이며 이유는
> [ADR 0002](./docs/adr/0002-production-uses-ephemeral-h2-file-db.md) 에 있다.
