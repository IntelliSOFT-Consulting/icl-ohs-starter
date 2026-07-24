# packages/shared-libraries/schemas/

The single source of truth for health data shapes. Intentionally left
empty in this template — populate it once you know your project's FHIR
profiles.

## What belongs here

One JSON Schema file per data type/profile. Keep them flat and simple;
`generate.js` currently supports primitive property types
(`string`, `integer`, `number`, `boolean`) — extend it if you need nested
objects, arrays, or `$ref` composition.

## Sample: what a real schema looks like

```json
// patient-profile.json
{
  "type": "object",
  "required": ["id", "name"],
  "properties": {
    "id": { "type": "string" },
    "name": { "type": "string" },
    "birthDate": { "type": "string" },
    "isActive": { "type": "boolean" }
  }
}
```

Running `node ../generate.js` from `shared-libraries/` turns this into:

```typescript
// generated-ts/src/PatientProfile.ts
export interface PatientProfile {
  id: string;
  name: string;
  birthDate?: string;
  isActive?: boolean;
}
```

```kotlin
// generated-kotlin/.../PatientProfile.kt
data class PatientProfile(
    val id: String,
    val name: String,
    val birthDate: String? = null,
    val isActive: Boolean? = null
)
```
