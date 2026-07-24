# packages/shared-libraries/generated-kotlin/

Build output only. Compiled Kotlin data classes, generated from
`../schemas/*.json` by `../generate.js`. Published to Maven as
`org.example.ohs.generated`.

**Do not hand-edit files here** — they are overwritten every time
`generate.js` runs.

## What belongs here

- `build.gradle.kts` — Gradle module config (hand-maintained)
- `src/main/kotlin/generated/` — generated `.kt` data classes (auto-created
  by `generate.js`, doesn't exist until you run it)

## Sample: what appears after running `generate.js`

```
generated-kotlin/
├── build.gradle.kts
└── src/
    └── main/
        └── kotlin/
            └── generated/
                └── PatientProfile.kt
```

```kotlin
// AUTO-GENERATED from schemas/patient-profile.json — do not edit by hand.
package generated

data class PatientProfile(
    val id: String,
    val name: String,
    val birthDate: String? = null
)
```

Consumed by `apps/mobile-kotlin` and `apps/core-platform/service-fhir-java`
via a Gradle project dependency:
```kotlin
implementation(project(":packages:shared-libraries:generated-kotlin"))
```
