plugins {
    id("org.springframework.boot") version "3.3.0"
    kotlin("jvm")
}

group = "org.example.ohs"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    // Consumes the generated Kotlin/Java models compiled from
    // packages/shared-libraries/schemas/*.json
    implementation(project(":packages:shared-libraries:generated-kotlin"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("ca.uhn.hapi.fhir:hapi-fhir-structures-r4:7.4.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
