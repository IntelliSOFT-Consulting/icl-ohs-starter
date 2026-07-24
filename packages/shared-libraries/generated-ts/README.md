# packages/shared-libraries/generated-ts/

Build output only. Compiled TypeScript models, generated from
`../schemas/*.json` by `../generate.js`. Published to NPM as `@ohs/generated-ts`.

**Do not hand-edit files here** — they are overwritten every time
`generate.js` runs.

## What belongs here

- `package.json` — package manifest (hand-maintained)
- `src/` — generated `.ts` interfaces (auto-created by `generate.js`,
  doesn't exist until you run it)

## Sample: what appears after running `generate.js`

```
generated-ts/
├── package.json
└── src/
    └── PatientProfile.ts
```

```typescript
// AUTO-GENERATED from schemas/patient-profile.json — do not edit by hand.
export interface PatientProfile {
  id: string;
  name: string;
  birthDate?: string;
}
```

Consumed by `apps/web-react` and `apps/core-platform/gateway-node` via:
```ts
import { PatientProfile } from "@ohs/generated-ts";
```
