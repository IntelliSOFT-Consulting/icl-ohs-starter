# docs/

Architecture decisions and onboarding documentation for the repo — the
"why" behind the structure, not API reference docs (those belong closer to
the code they describe).

## What belongs here

- `architecture.md` — rationale for the polyglot monorepo approach
- (add) ADRs (Architecture Decision Records) as the project evolves
- (add) onboarding guides for new engineers joining any of the four teams

## Sample: what's already in this folder

`architecture.md` covers:
- Core architectural drivers (schema flux, team autonomy, external consumption)
- Infra/deployment alignment rationale
- Comparison matrix vs. multi-repo and submodules
- Exit strategy if the monorepo needs to be split later

## Adding a new doc

Use plain Markdown, one file per topic. Link new docs from the root
`README.md` if they're relevant to onboarding.
