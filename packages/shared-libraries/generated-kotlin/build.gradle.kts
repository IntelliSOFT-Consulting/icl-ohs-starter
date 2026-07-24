// Auto-generated Kotlin models compiled from packages/shared-libraries/schemas.
// Do not edit generated sources by hand — edit the JSON Schemas instead and
// re-run `node ../generate.js`.

plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "org.example.ohs.generated"
version = "0.1.0"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
