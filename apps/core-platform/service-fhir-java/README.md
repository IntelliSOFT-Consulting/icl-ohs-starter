# apps/core-platform/service-fhir-java/

Java service built on HAPI FHIR, holding custom business logic for
converting/storing FHIR resources.

## What belongs here

- `build.gradle.kts` — dependencies, including HAPI FHIR and the generated
  Kotlin models from `packages/shared-libraries/generated-kotlin`
- `Dockerfile` — Gradle build stage + slim JRE runtime stage
- `src/main/java` or `src/main/kotlin` — service code (add as the app grows)

## Environment variables

This service reads from the **shared root `.env`** (see `/.env.example`).
- In Docker Compose, it's injected via `env_file: [../../.env]`.
- For a local (non-Docker) Gradle run, export it into your shell first:
  ```bash
  export $(grep -v '^#' ../../../.env | xargs)
  ./gradlew bootRun
  ```
Relevant vars: `FHIR_SERVICE_PORT`, `SPRING_DATASOURCE_URL`,
`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`,
`FHIR_SERVER_BASE_URL`.

## Sample: what's already in this folder

`build.gradle.kts` (excerpt):
```kotlin
dependencies {
    implementation(project(":packages:shared-libraries:generated-kotlin"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:7.4.0")
}
```

`Dockerfile` (excerpt):
```dockerfile
FROM gradle:8.9-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon -x test

FROM eclipse-temurin:17-jre
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Instantiating for a real project

- Add `src/main/kotlin` (or `java`) with your Spring Boot controllers and
  FHIR resource converters.
- Wire the datasource to the `fhir-db` Postgres instance defined in
  `docker-compose.local.yml` / `infra/terraform/modules/fhir-db`.
