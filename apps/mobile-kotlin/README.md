# apps/mobile-kotlin/

Android application workspace. Owned by the Mobile team.

## What belongs here

- `build.gradle.kts` — module build config, depends on
  `packages/shared-libraries/generated-kotlin` for FHIR data models
- `src/main/kotlin/...` — application code (activities, view models, UI)
- `src/test/...` — unit tests (add as the app grows)

## Sample: what's already in this folder

```
mobile-kotlin/
├── build.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── org/
                └── example/
                    └── mobile/
                        └── MainActivity.kt
```

`build.gradle.kts` (excerpt):
```kotlin
dependencies {
    implementation(project(":packages:shared-libraries:generated-kotlin"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
}
```

`MainActivity.kt`:
```kotlin
package org.example.mobile

import androidx.appcompat.app.AppCompatActivity

// TODO: replace with real screens (e.g. Patient Registration, Vitals capture)
class MainActivity : AppCompatActivity()
```

## Instantiating for a real project

- Rename the `org.example.mobile` package to your project's namespace in
  both `build.gradle.kts` (`namespace`, `applicationId`) and the source tree.
- Replace `MainActivity` with real screens once schemas are populated in
  `packages/shared-libraries/schemas/`.

## Configure the API base URL (icl-client-reference-app)

`icl-client-reference-app` talks to a provider auth API. All of its API endpoints and related
constants are centralized in one file:

```
icl-client-reference-app/src/commonMain/kotlin/icl/ohs/refernce/config/ApiConstants.kt
```

Update that file — not individual screens — when a login/reset-password/profile endpoint path or
a legal-page URL changes.

The API **base URL** is the exception: it is not a literal in that file, and it is not committed
to source control at all. It's read from `local.properties`, which is already gitignored, so each
developer (and each CI environment) can point at their own backend without touching a tracked
file.

Setup steps for a fresh clone:

1. Open the project in Android Studio once so `local.properties` exists (it auto-generates it with
   `sdk.dir` set).
2. Copy the line from `local.properties.template` into your `local.properties` and fill in the
   value:

   ```properties
   apiBaseUrl=https://your-auth-server.example.com/auth
   ```

3. Sync/build. The `:icl-client-reference-app` Gradle build reads `apiBaseUrl` and generates
   `icl.ohs.reference.config.LocalConfig`, which `ApiConstants.BASE_AUTH_URL` exposes to every
   Kotlin Multiplatform target (Android, iOS, JVM, JS, Wasm).

For CI or other automation, set the `API_BASE_URL` environment variable instead — it takes
precedence over `local.properties`. If neither is set, the build still succeeds (so Gradle sync
and unrelated tasks like `clean`/`tasks` never break) — Gradle logs a warning, and sign-in shows a
"configure the auth base URL" message at runtime until it's set, the same way release builds
degrade to unsigned when signing config is missing.