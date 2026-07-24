# Architectural Justification: Polyglot Monorepo for OHS

## Summary

This repo uses a **polyglot monorepo** (Node/Nx-style workspaces + Gradle
multi-project build) instead of multi-repo or Git submodules, because the
org has specialized teams (Mobile, Web, Backend, DevOps) that need
independent release cycles *and* a single source of truth for health data
schemas that change frequently early in a project's life.

## Core drivers

1. **Schema flux vs. type safety** — a schema change in
   `packages/shared-libraries/schemas/` compiles immediately into TS and
   Kotlin, verifiable against web, mobile, and backend in one commit —
   instead of a schema PR + three follow-up version-bump PRs.

2. **Team autonomy preserved via path filtering** — GitHub Actions
   workflows in `.github/workflows/` trigger only on their team's paths, and
   `.github/CODEOWNERS` enforces review gatekeeping per directory.

3. **Clean external consumption** — schemas compile to versioned NPM
   (`generated-ts/`) and Maven (`generated-kotlin/`) packages, published on
   merge to `main`, so external consumers never touch internal source.

4. **Infra co-located with app code** — Terraform and Ansible live in
   `infra/` next to `apps/`, so infra changes land in the same branch/PR as
   the application changes that require them.

## Comparison

| Driver | Multi-repo | Submodules | Monorepo (this template) |
|---|---|---|---|
| Schema sync | Manual bumps | Detached-HEAD prone | Atomic, single commit |
| Team autonomy | High | Medium | High (path filters + CODEOWNERS) |
| External consumption | High overhead | Exposes raw source | Automated package publish |
| Infra alignment | Disconnected | Fragmented | Unified |
| Onboarding | Clone 5+ repos | Painful submodule sync | Single clone |

## Exit strategy

Each directory under `apps/` and `infra/` is self-contained enough to be
extracted into its own repo later with minimal effort, if team/project
scale eventually makes the monorepo unwieldy.
