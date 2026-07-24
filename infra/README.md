# infra/

Platform / DevOps team workspace. Infrastructure-as-code lives next to
application code so infra changes land in the same branch/PR as the app
changes that require them.

## What belongs here

- `terraform/` — cloud resource provisioning (databases, compute clusters)
- `ansible/` — server configuration management and bootstrapping
- `docker/` — global Docker security/compliance baselines shared across
  all containers built in `apps/`

## Sample: what's already in this folder

```
infra/
├── docker/
│   └── security-policies/
├── terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── environments/
│   └── modules/
│       ├── fhir-db/
│       └── compute-cluster/
└── ansible/
    ├── site.yml
    ├── group_vars/
    ├── inventory/
    └── roles/
        ├── hardening/
        └── docker-runtime/
```

## Ownership

All changes here require DevOps team approval (`.github/CODEOWNERS`), and
only `infra/**` changes trigger `.github/workflows/cd-infra.yml`.
