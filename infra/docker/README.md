# infra/docker/

Global/production base Docker configurations shared across services —
distinct from the per-service `Dockerfile`s that live inside each app
directory under `apps/`.

## What belongs here

- `security-policies/` — compliance baselines (encryption, hardening
  checklists) that every container built in this repo should follow
- (add) shared base images, if multiple services should build `FROM` a
  common hardened base

## Sample: what's already in this folder

```
docker/
└── security-policies/
    └── README.md   # HIPAA/GDPR compliance checklist
```
