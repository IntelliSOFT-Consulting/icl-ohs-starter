# apps/web-react/

React web application workspace (admin/clinician dashboards). Owned by the
Web team.

## What belongs here

- `package.json` — dependencies, including `@ohs/generated-ts` (built from
  `packages/shared-libraries/schemas`)
- `vite.config.ts` — sets `envDir` to the repo root so Vite reads the
  shared `.env` instead of a local one
- `src/` — components, pages, hooks, app entrypoint

## Environment variables

This app reads from the **shared root `.env`** (see `/.env.example`).
`vite.config.ts` points `envDir` at the repo root:
```ts
export default defineConfig({
  envDir: "../../",
});
```
Vite only exposes vars prefixed `VITE_` to client code — relevant ones:
`VITE_API_BASE_URL`, `VITE_APP_NAME`, `VITE_ENABLE_RISK_ANALYSIS`.

## Sample: what's already in this folder

```
web-react/
├── package.json
├── vite.config.ts
└── src/
    ├── App.tsx
    ├── main.tsx
    └── components/
```

`package.json` (excerpt):
```json
{
  "name": "@ohs/web-react",
  "dependencies": {
    "@ohs/generated-ts": "workspace:*",
    "react": "^18.3.0",
    "react-dom": "^18.3.0"
  }
}
```

`src/App.tsx`:
```tsx
import React from "react";

export default function App() {
  return <div>OHS Web Dashboard - starter</div>;
}
```

## Instantiating for a real project

- Replace `App.tsx` with your real dashboard shell.
- Add feature components under `src/components/` (e.g.
  `RiskAnalysisWidget.tsx` for a maternal-health project).
- Types from `@ohs/generated-ts` become available once schemas are added
  and `node packages/shared-libraries/generate.js` has been run.
