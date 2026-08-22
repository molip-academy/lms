# infra: Dockerfile + Railway 설정

Status: done
Blocked by: 14

- `back/Dockerfile`: multi-stage. build는 `eclipse-temurin:25-jdk`, run은 `eclipse-temurin:25-jre`.
  Gradle wrapper로 빌드하고 layer 캐시를 살린다.
- `SPRING_PROFILES_ACTIVE=prod`, `PORT` 환경변수 바인딩 (Railway가 주입)
- `prod` profile: H2 file DB를 container 내 절대경로에, h2-console **off**, show-sql **off**
- `JWT_SECRET`은 Railway 환경변수
- region 싱가폴

Railway의 GitHub 자동 연동은 **쓰지 않는다** — test gate를 우회하기 때문. Actions에서 CLI로 배포한다.
