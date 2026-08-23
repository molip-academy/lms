# syntax=docker/dockerfile:1
#
# 빌드 컨텍스트는 **repo 루트**다. Railway 의 GitHub 연동이 monorepo 루트를 컨텍스트로 넘기므로
# COPY 경로에 back/ 접두사가 붙는다. 로컬에서 직접 빌드할 때도 루트에서 실행해야 한다:
#   docker build -f back/Dockerfile .

# ---- build ----
# Railway 의 자동 빌더가 JDK 25 를 지원하지 않을 수 있어 toolchain 을 직접 못박는다.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# wrapper 와 build script 를 먼저 복사해 의존성 layer 를 캐시한다.
COPY back/gradlew ./
COPY back/gradle ./gradle
COPY back/settings.gradle.kts back/build.gradle.kts ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies --quiet || true

COPY back/src ./src
# 이미지 빌드 단계에서는 test 를 돌리지 않는다. test gate 는 GitHub Actions 가 담당한다.
RUN ./gradlew --no-daemon clean bootJar -x test

# ---- run ----
FROM eclipse-temurin:25-jre
WORKDIR /app

# ADR 0002: volume 없는 H2 file DB. 이 디렉터리는 재배포마다 사라진다.
RUN mkdir -p /app/data

COPY --from=build /workspace/build/libs/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"
EXPOSE 8080

# Railway 가 PORT 를 주입한다. application.yml 의 ${PORT:8080} 이 이를 받는다.
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]
