# apps/core-platform/

Backend services workspace. Owned by the Backend team. Contains multiple
microservices plus a local orchestration file to run them together.

## What belongs here

- One directory per backend service (`gateway-node`, `service-fhir-java`,
  and any future services)
- `docker-compose.local.yml` — spins up every service + local databases for
  local development

## Sample: what's already in this folder

```
core-platform/
├── docker-compose.local.yml
├── gateway-node/          # Node.js API Gateway / Auth Layer
│   ├── Dockerfile
│   ├── package.json
│   └── src/index.js
└── service-fhir-java/     # Java HAPI FHIR / business logic service
    ├── Dockerfile
    └── build.gradle.kts
```

`docker-compose.local.yml` (excerpt):
```yaml
services:
  fhir-db:
    image: postgres:16
    env_file: [../../.env]
  service-fhir-java:
    build: ./service-fhir-java
    depends_on: [fhir-db]
    env_file: [../../.env]
  gateway-node:
    build: ./gateway-node
    depends_on: [service-fhir-java]
    env_file: [../../.env]
```

Environment variables: copy the single root `.env.example` to `.env` at
the repo root — `docker compose` needs to be pointed at it explicitly
with `--env-file` since it lives outside this directory.

Run the whole backend locally:
```bash
# from the repo root
cp .env.example .env

cd apps/core-platform
docker compose --env-file ../../.env -f docker-compose.local.yml up
```

## Adding a new service

1. Create `apps/core-platform/<new-service>/` with its own `Dockerfile`
2. Add it to `docker-compose.local.yml`
3. Add a job for it in `.github/workflows/cd-core.yml`
