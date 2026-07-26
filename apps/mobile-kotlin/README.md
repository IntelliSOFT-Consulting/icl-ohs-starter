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

## API endpoints and constants (icl-client-reference-app)

`icl-client-reference-app` talks to a provider auth API. Its API base URL, endpoint paths, and
related constants are centralized in one file:

```
icl-client-reference-app/src/commonMain/kotlin/icl/ohs/reference/config/ApiConstants.kt
```

Update that file — not individual screens — when the base URL, a login/reset-password/profile
endpoint path, or a legal-page URL changes.