// Root multi-project build config, applied to all Gradle subprojects.

plugins {
    kotlin("jvm") version "2.0.0" apply false
    id("com.android.application") version "8.5.0" apply false
    id("org.springframework.boot") version "3.3.0" apply false
}

allprojects {
    repositories {
        mavenCentral()
        google()
    }
}
