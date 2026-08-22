# back: Gradle/Spring Boot scaffolding + profile 3종 + BaseEntity

Status: done

Kotlin + Spring Boot 4.1.1, JDK 25, Gradle Kotlin DSL로 `back/`을 세운다.

- Gradle Wrapper 포함 (로컬에 gradle 없음).
- plugin: `kotlin("jvm")`, `kotlin("plugin.spring")`, `kotlin("plugin.jpa")`, `org.springframework.boot`, `io.spring.dependency-management`
- 의존성: devtools, spring-boot-starter-data-jpa, validation, security, web, h2, jjwt
- `com.back.BackApplication` + `@EnableJpaAuditing`
- `com.back.global.jpa.entity.BaseEntity`: `id`, `createDate`, `modifyDate` (`@CreatedDate`/`@LastModifiedDate`, `@EntityListeners(AuditingEntityListener::class)`)
- `application.yml` / `application-dev.yml` / `application-test.yml` / `application-prod.yml`
- OSIV off (`spring.jpa.open-in-view: false`)
- `.gitignore`에 `db_dev.mv.db`, `build/`, `.gradle/`

`./gradlew build`가 통과하면 완료.
