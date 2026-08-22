# back

코프링 backend module이다. Kotlin + Spring Boot 4.1.1, JDK 25, Gradle Kotlin DSL.

## 구조

```
com.back
├── BackApplication.kt          @EnableJpaAuditing
├── domain/
│   ├── member/member/          Member, Role
│   ├── member/auth/            로그인 / 로그아웃
│   └── post/                   post, comment, postLike, commentLike
└── global/
    ├── jpa/entity/BaseEntity   작성일 / 수정일
    ├── security/               JWT, SecurityConfig
    ├── rq/Rq                   현재 로그인 주체와 인증 cookie
    ├── exception/              ServiceException, ProblemDetail 변환
    ├── dto/                    PageDto, LikeResultDto
    └── initData/BaseInitData   sample data
```

## 규칙

- **OSIV 는 꺼져 있고, `@Transactional` 은 controller action method 에 붙인다.**
  응답 직렬화까지 transaction 이 열려 있으므로 lazy loading 이 동작한다.
- 성공 응답은 순수 DTO, 에러는 RFC 9457 `ProblemDetail` 이다.
  front 는 HTTP status 로 성공/실패를 판별하므로 200 안에 실패를 담지 않는다.
- entity 에 `data class` 를 쓰지 않는다. `equals`/`hashCode` 가 Hibernate lazy proxy 와 충돌한다.
- 인증 JWT 는 `HttpOnly` cookie 에 담는다. 자세한 이유는 repo root 의 ADR 참고.

## Profile

| profile | DB | ddl-auto | h2-console |
| --- | --- | --- | --- |
| `dev` (기본) | file `./db_dev.mv.db` | update | on |
| `test` | H2 in-memory | create | off |
| `prod` | file `/app/data/db_prod` | update | **off** |
