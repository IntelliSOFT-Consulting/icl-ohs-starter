# infra/ansible/group_vars/

Variables applied across host groups referenced in `site.yml` and
`inventory/`. Environment variables configuration (dev, staging, prod).

## What belongs here

- `all.yml` — variables shared by every host
- (add) `<env>.yml` — environment-specific overrides, if needed

## Sample: what's already in this folder

`all.yml`:
```yaml
timezone: "UTC"
docker_version: "26.1"
```

## Adding environment-specific overrides

```yaml
# group_vars/production.yml
docker_version: "26.1"
extra_hardening: true
```

Ansible automatically loads `group_vars/<group-name>.yml` for any group
defined in your inventory file.
