# infra/ansible/roles/

Reusable, composable configuration units, applied to hosts from `site.yml`.

## What belongs here

One directory per role, each with a `tasks/main.yml` (and optionally
`handlers/`, `templates/`, `defaults/`).

## Sample: what's already in this folder

```
roles/
├── hardening/           # HIPAA/GDPR server hardening baselines
│   └── tasks/main.yml
└── docker-runtime/       # Prepares nodes with Docker runtime + networking
    └── tasks/main.yml
```

## Adding a role

```
roles/<new-role>/tasks/main.yml
```
```yaml
---
- name: Do the thing
  ansible.builtin.debug:
    msg: "role task here"
```

Then reference it in `site.yml`:
```yaml
- hosts: all
  roles:
    - <new-role>
```
