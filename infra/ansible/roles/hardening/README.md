# infra/ansible/roles/hardening/

HIPAA / GDPR server hardening compliance baseline, applied to every host.

## What belongs here

- `tasks/main.yml` — the hardening steps (automatic updates, firewall,
  SSH restrictions, etc.)

## Sample: what's already in this folder

```yaml
- name: Ensure automatic security updates are enabled
  ansible.builtin.debug:
    msg: "TODO: configure unattended-upgrades / dnf-automatic"

- name: Ensure firewall is enabled and default-deny
  ansible.builtin.debug:
    msg: "TODO: configure ufw/firewalld default deny inbound"

- name: Disable password-based SSH auth
  ansible.builtin.debug:
    msg: "TODO: enforce PasswordAuthentication no in sshd_config"
```

## Instantiating for a real project

Replace each `debug` placeholder with a real task, e.g.:
```yaml
- name: Disable password-based SSH auth
  ansible.builtin.lineinfile:
    path: /etc/ssh/sshd_config
    regexp: '^PasswordAuthentication'
    line: 'PasswordAuthentication no'
  notify: restart sshd
```
