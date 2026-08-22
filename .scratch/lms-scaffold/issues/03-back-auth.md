# back: 인증 (JWT HttpOnly cookie)

Status: done
Blocked by: 02

- `com.back.global.security`: Spring Security 7 설정. `SecurityFilterChain` bean.
- JWT를 `HttpOnly; Secure; SameSite=Lax` cookie(`token`)에 담는다. access 단독, 7일 만료.
- `JwtAuthenticationFilter`: cookie에서 token을 읽어 `SecurityContext`에 인증을 채운다.
- secret은 `JWT_SECRET` 환경변수, dev/test는 기본값.
- BCrypt password encoder.
- endpoint: `POST /api/v1/members`(가입), `POST /api/v1/auth/login`, `POST /api/v1/auth/logout`(`Max-Age=0`), `GET /api/v1/members/me`
- CSRF는 비활성 (token cookie + SameSite=Lax + JSON API).
- 인증 실패는 401, 권한 부족은 403을 `ProblemDetail`로 반환.
- `@RestControllerAdvice`로 `@Valid` 실패를 필드별 메시지를 담은 `ProblemDetail`로 변환.

controller action method에 `@Transactional`을 붙인다.
