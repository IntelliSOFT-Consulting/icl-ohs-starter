# packages/shared-libraries/

The cross-project data layer. FHIR JSON Schemas are the single source of
truth here; TypeScript and Kotlin models are generated from them so every
app (`web-react`, `gateway-node`, `service-fhir-java`, `mobile-kotlin`)
consumes the same types.

## What belongs here

- `schemas/` — hand-written JSON Schemas (source of truth, never generated)
- `generate.js` — the compiler: JSON Schema → TypeScript + Kotlin
- `generated-ts/`, `generated-kotlin/` — build output, never hand-edited
- `package.json` — workspace manifest for this package

## Environment variables

For manual local publishing only (CI uses repo secrets — see
`.github/workflows/pipeline-codegen.yml`), source the **shared root
`.env`** first:
```bash
source ../../.env
npm publish --workspace generated-ts --access public
```
Relevant vars: `NPM_TOKEN`, `MAVEN_USERNAME`, `MAVEN_PASSWORD`.

## Sample: what's already in this folder

```
shared-libraries/
├── schemas/            # empty — add your JSON Schemas here
│   └── .gitkeep
├── generated-ts/
│   └── package.json
├── generated-kotlin/
│   └── build.gradle.kts
├── generate.js
└── package.json
```

## Workflow

1. Add or edit a schema in `schemas/`, e.g.:
   ```json
   // schemas/patient-profile.json
   {
     "type": "object",
     "required": ["id", "name"],
     "properties": {
       "id": { "type": "string" },
       "name": { "type": "string" },
       "birthDate": { "type": "string" }
     }
   }
   ```
2. Run the compiler:
   ```bash
   node generate.js
   ```
3. This writes `generated-ts/src/PatientProfile.ts` and
   `generated-kotlin/src/main/kotlin/generated/PatientProfile.kt`
4. `.github/workflows/pipeline-codegen.yml` runs this automatically on push
   and publishes versioned packages from `main`.

Never hand-edit files inside `generated-ts/` or `generated-kotlin/` — they
are overwritten on the next `generate.js` run.
