# .github/workflows/

CI/CD pipeline definitions. Each workflow is scoped with `paths:` filters so
it only runs when files relevant to that team change — this is what gives
teams independent release cycles inside a single monorepo.

## What belongs here

One workflow file per deployable unit (or per cross-cutting pipeline like
schema codegen). Every workflow should:
1. Filter on the specific `apps/<team>/**` (or `infra/**`) path
2. Build + test
3. Deploy only on `main`, gated behind a build/test job

## Sample: `cd-web.yml` (already in this folder)

```yaml
name: CD - Web (React)

on:
  push:
    branches: [main]
    paths:
      - "apps/web-react/**"
      - "packages/shared-libraries/generated-ts/**"

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: apps/web-react
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: "20" }
      - run: npm ci
      - run: npm run build
      - run: npm test -- --ci

  deploy:
    needs: build-and-test
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      - run: echo "TODO: deploy build artifacts"
```

## Adding a new workflow

If a new deployable unit is added under `apps/` or `infra/`, copy the
closest existing workflow, update its `paths:` filter and job steps, and
add the corresponding entry to `.github/CODEOWNERS`.
