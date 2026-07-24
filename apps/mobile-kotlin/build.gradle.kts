plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "org.example.mobile" // TODO: rename to your project's namespace
    compileSdk = 34

    defaultConfig {
        applicationId = "org.example.mobile"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Consumes the generated Kotlin models compiled from
    // packages/shared-libraries/schemas/*.json
    implementation(project(":packages:shared-libraries:generated-kotlin"))

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    testImplementation("junit:junit:4.13.2")
}
