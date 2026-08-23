# syntax=docker/dockerfile:1

# ---- build ----
# Railway 의 자동 빌더가 JDK 25 를 지원하지 않을 수 있어 toolchain 을 직접 못박는다.
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# wrapper 와 build script 를 먼저 복사해 의존성 layer 를 캐시한다.
COPY gradlew ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies --quiet || true

COPY src ./src
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
