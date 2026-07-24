# .github/

GitHub-native configuration: pull request routing and CI/CD automation.
Everything here is read by GitHub itself, not by your application code.

## What belongs here

- `CODEOWNERS` — maps directories to the teams that must review changes there
- `workflows/` — GitHub Actions pipelines, one per team, path-filtered so a
  change in one team's directory doesn't trigger every other team's pipeline

## Sample: what's already in this folder

```
.github/
├── CODEOWNERS
└── workflows/
    ├── cd-web.yml            # fires only on apps/web-react/** changes
    ├── cd-mobile.yml         # fires only on apps/mobile-kotlin/** changes
    ├── cd-core.yml           # fires only on apps/core-platform/** changes
    ├── cd-infra.yml          # fires only on infra/** changes
    └── pipeline-codegen.yml  # fires only on packages/shared-libraries/schemas/** changes
```

## When you instantiate this template

- Replace the placeholder handles in `CODEOWNERS` (`@org/mobile-team`, etc.)
  with your real GitHub team handles.
- Add secrets referenced by workflows (`NPM_TOKEN`, `MAVEN_USERNAME`,
  `MAVEN_PASSWORD`, cloud provider credentials) in repo/environment settings.
