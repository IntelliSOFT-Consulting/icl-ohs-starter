# apps/

Top-level deployable applications, one directory per specialized team.
Each subdirectory is a self-contained workspace: it can be built, tested,
and deployed independently of the others, and could be extracted into its
own repository later with minimal effort.

## What belongs here

- One directory per team-owned application (`mobile-kotlin`, `web-react`,
  `core-platform`, and any future apps)
- No shared business logic — cross-app code belongs in `packages/`
- Each app directory owns its own build config (`package.json`,
  `build.gradle.kts`, `Dockerfile`, etc.)

## Sample: what's already in this folder

```
apps/
├── mobile-kotlin/       # Android team workspace
│   ├── build.gradle.kts
│   └── src/
├── web-react/           # Web team workspace
│   ├── package.json
│   └── src/
└── core-platform/       # Backend team workspace (multiple services)
    ├── gateway-node/
    ├── service-fhir-java/
    └── docker-compose.local.yml
```

## Adding a new app

1. Create `apps/<new-app>/`
2. Add its build manifest and a `src/` entrypoint
3. Add a matching `.github/workflows/cd-<new-app>.yml` with a `paths:` filter
4. Add an ownership line in `.github/CODEOWNERS`
