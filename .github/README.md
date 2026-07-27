# .github/

GitHub-native configuration: pull request routing, issue/PR templates, and CI/CD automation.
Everything here is read by GitHub itself, not by your application code.

## What belongs here

- `CODEOWNERS` — maps directories to the teams that must review changes there
- `PULL_REQUEST_TEMPLATE.md` — checklist applied to every new pull request
- `ISSUE_TEMPLATE/` — task, feature/epic, and bug report forms for new issues
- `workflows/` — GitHub Actions pipelines, one per team, path-filtered so a
  change in one team's directory doesn't trigger every other team's pipeline

## Sample: what's already in this folder

```
.github/
├── CODEOWNERS
├── PULL_REQUEST_TEMPLATE.md
├── ISSUE_TEMPLATE/
│   ├── task--issue--template.md
│   ├── feature--epic--template.md
│   └── bug_report.md
└── workflows/
    ├── cd-web.yml            # fires only on apps/web-react/** changes
    ├── cd-mobile.yml         # fires only on apps/mobile-kotlin/** changes
    ├── cd-core.yml           # fires only on apps/core-platform/** changes
    ├── cd-infra.yml          # fires only on infra/** changes
    └── pipeline-codegen.yml  # fires only on packages/shared-libraries/schemas/** changes
```

## Contribution flow

1. Prefer opening an issue first (task, feature/epic, or bug) for discussion and scoping.
2. Open a PR that links the issue with `Fixes #<n>` and completes the PR checklist
   (CI checks pass; docs updated when appropriate).

## When you instantiate this template

- Replace the placeholder handles in `CODEOWNERS` (`@org/mobile-team`, etc.)
  with your real GitHub team handles.
- Add secrets referenced by workflows (`NPM_TOKEN`, `MAVEN_USERNAME`,
  `MAVEN_PASSWORD`, cloud provider credentials) in repo/environment settings.
