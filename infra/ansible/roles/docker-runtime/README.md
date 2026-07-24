# infra/ansible/roles/docker-runtime/

Prepares nodes with Docker runtime and networking so they can run the
containers built from `apps/core-platform/*`.

## What belongs here

- `tasks/main.yml` — Docker install + service enablement + network setup

## Sample: what's already in this folder

```yaml
- name: Install Docker
  ansible.builtin.apt:
    name: docker.io
    state: present
    update_cache: true
  when: ansible_os_family == "Debian"

- name: Ensure Docker service is running
  ansible.builtin.service:
    name: docker
    state: started
    enabled: true
```

## Instantiating for a real project

Add the compose plugin and any overlay network setup:
```yaml
- name: Install docker-compose-plugin
  ansible.builtin.apt:
    name: docker-compose-plugin
    state: present
```
