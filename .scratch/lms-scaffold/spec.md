# LMS Scaffold: 인증 + 커뮤니티 게시판 + 배포 파이프라인

## 목표

MOLIP Academy LMS의 첫 배포 가능한 뼈대를 만든다. LMS 본체(학습 workflow)는 아직 정해지지 않았으므로,
이번 scope는 **LMS에 딸린 커뮤니티 게시판**과 그것을 인터넷에 띄우는 CI/CD pipeline이다.

용어는 [CONTEXT.md](../../CONTEXT.md)를 따른다. `Member`는 인증 주체이며 학습상의 신분이 아니다.

## 확정된 결정

### back (`back/`)

- Kotlin + Spring Boot 4.1.1, JDK 25, Gradle Kotlin DSL. **Lombok 미사용** (Kotlin이 대체).
- root package `com.back`, main class `com.back.BackApplication` (`@EnableJpaAuditing`).
- 모든 entity는 `BaseEntity`를 상속해 작성일/수정일을 갖는다.
- OSIV **off**. `@Transactional`은 **controller action method** 레벨에 붙인다.
- profile 3종:
  - `dev`: H2 file DB (`./db_dev.mv.db`), `ddl-auto: update`, h2-console **on**
  - `test`: H2 in-memory, `ddl-auto: create`
  - `prod`: H2 file DB (container 내 절대경로), `ddl-auto: update`, h2-console **off**, show-sql **off**
- 의존성: DEV-TOOLS, SPRING-DATA-JPA, VALIDATION, SPRING-SECURITY, H2
- 인증: **JWT를 `HttpOnly; Secure; SameSite=Lax` cookie에** 담는다. access token 단독, 7일 만료.
  refresh token 없음 (ADR 0002 참조). password는 BCrypt.
- API prefix `/api/v1`. 성공 응답은 **순수 DTO**, 에러는 **`ProblemDetail` (RFC 9457)**.
- 목록은 page 20건, 최신순. `Page`를 그대로 직렬화하지 않고 얇은 DTO로 감싼다.

### front (`front-react/`)

- Vite + React + TypeScript, shadcn/ui (Tailwind v4, base color `zinc`), 다크모드 toggle.
- React Router v7 + TanStack Query + react-hook-form + zod.
- Pretendard **dynamic subset**을 jsDelivr CDN에서 로드.
- 로그인 여부는 앱 부팅 시 `GET /api/v1/members/me` 호출로 판정 (cookie가 `HttpOnly`라 JS가 token을 읽을 수 없음).

### infra

- front는 **Cloudflare Workers + Static Assets** (`wrangler deploy`).
- Worker의 `fetch` handler가 `/api/*`를 Railway로 proxy해 **same-origin**을 만든다 (ADR 0001).
- back은 **Railway(싱가폴)**, multi-stage **Dockerfile** (`eclipse-temurin:25`).
- GitHub Actions workflow 2개 + `paths` filter. test를 배포 gate로 건다.
  `main` push 시 배포, PR에서는 test/build만.

## Domain model

```
Member  id, createDate, modifyDate, username(uk), password, nickname(uk), role(USER|ADMIN)
Post    id, createDate, modifyDate, title, content, author->Member, viewCount, likeCount
Comment id, createDate, modifyDate, content, author->Member, post->Post, likeCount
PostLike    id, createDate, modifyDate, member->Member, post->Post        uk(member, post)
CommentLike id, createDate, modifyDate, member->Member, comment->Comment  uk(member, comment)
```

`likeCount`는 비정규화 counter이며 추천/취소 시 같은 transaction 안에서 증감한다.
`viewCount`는 상세 조회 요청마다 +1 (중복 제거 없음).

## 검증 규칙

| 필드 | 규칙 |
| --- | --- |
| `username` | 4~20자, 영문 소문자 + 숫자, 유일 |
| `password` | 8자 이상 |
| `nickname` | 2~20자, 유일 |
| `Post.title` | 1~200자 |
| `Post.content` | 1자 이상 |
| `Comment.content` | 1~1000자 |

back의 `@Valid` 제약과 front의 zod schema를 1:1로 맞춘다.

## API

```
POST   /api/v1/members              회원가입
GET    /api/v1/members/me           내 정보
POST   /api/v1/auth/login
POST   /api/v1/auth/logout
GET    /api/v1/posts?page=&size=    목록 (20건, 최신순)
POST   /api/v1/posts
GET    /api/v1/posts/{id}           조회수 +1
PUT    /api/v1/posts/{id}           작성자 본인만
DELETE /api/v1/posts/{id}           작성자 본인 또는 ADMIN
GET    /api/v1/posts/{id}/comments
POST   /api/v1/posts/{id}/comments
PUT    /api/v1/comments/{id}        작성자 본인만
DELETE /api/v1/comments/{id}        작성자 본인 또는 ADMIN
POST   /api/v1/posts/{id}/likes     추천
DELETE /api/v1/posts/{id}/likes     추천 취소
POST   /api/v1/comments/{id}/likes
DELETE /api/v1/comments/{id}/likes
```

## Sample data (`com.back.global.initData.BaseInitData`)

`baseInitDataApplicationRunner` bean, `@Transactional`. 회원이 1명이라도 있으면 중단.

- 회원 5명: `admin`(ADMIN, 닉네임 `관리자`) + `user1`~`user4`(USER, 닉네임 `유저1`~`유저4`)
- password 전부 `password123` (8자 제약 충족)
- 글 5개: `user1`~`user4`에 분산 (한 명이 2개)
- 댓글 5개: 한 글에 3개 + 다른 글에 2개
- `PostLike`를 글마다 0~3개씩 다르게

## 화면

| 경로 | 화면 | 접근 |
| --- | --- | --- |
| `/` | 글 목록 | 공개 |
| `/posts/:id` | 글 상세 + 댓글 | 공개 |
| `/posts/new` | 글 작성 | 보호 |
| `/posts/:id/edit` | 글 수정 | 보호 + 작성자 |
| `/login` | 로그인 | 비로그인 전용 |
| `/signup` | 회원가입 | 비로그인 전용 |
| `/me` | 내 정보 | 보호 |

보호는 `<ProtectedRoute>` wrapper로 처리하고, 튕긴 뒤 로그인하면 원래 가려던 곳으로 복귀한다.
작성자 확인은 front에서 숨기고 **back이 403으로 최종 차단**한다.

## 테스트

MockMvc 실제 호출 + `@Transactional` rollback. **mocking 하지 않는다.**
각 기능마다 성공 1개 + 권한/검증 실패 1~2개. 이 테스트가 배포 gate다.
