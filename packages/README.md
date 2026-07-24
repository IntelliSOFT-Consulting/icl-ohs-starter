# packages/

Cross-project code shared by multiple apps. Currently holds the
schema-to-code data layer; add other shared packages here as needed
(e.g. a shared UI kit, shared validation logic).

## What belongs here

- `shared-libraries/` — the FHIR JSON Schema source of truth and its
  generated TypeScript/Kotlin outputs
- Any future package consumed by more than one app in `apps/`

## Sample: what's already in this folder

```
packages/
└── shared-libraries/
    ├── schemas/            # source of truth: FHIR JSON Schemas
    ├── generated-ts/       # compiled TS package (consumed by web/gateway)
    ├── generated-kotlin/   # compiled Kotlin package (consumed by mobile/service)
    ├── generate.js         # the compiler
    └── package.json
```

## Rule of thumb

If code is used by more than one directory under `apps/`, it belongs in
`packages/`, not duplicated inside each app.
