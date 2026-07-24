# infra/ansible/

Server configuration management and application bootstrapping, for hosts
that aren't fully managed by Terraform-provisioned managed services (e.g.
self-managed VMs).

## What belongs here

- `site.yml` — main entrypoint playbook
- `group_vars/` — variables shared across host groups
- `inventory/` — per-environment host lists
- `roles/` — reusable, composable configuration units

## Sample: what's already in this folder

```
ansible/
├── site.yml
├── group_vars/
│   └── all.yml
├── inventory/
│   ├── staging.ini
│   └── production.ini
└── roles/
    ├── hardening/
    └── docker-runtime/
```

`site.yml`:
```yaml
- name: Baseline server hardening (HIPAA / GDPR)
  hosts: all
  become: true
  roles:
    - hardening

- name: Prepare Docker runtime
  hosts: all
  become: true
  roles:
    - docker-runtime
```

Run against staging:
```bash
ansible-playbook -i inventory/staging.ini site.yml
```
