# infra/ansible/inventory/

Host inventories, one file per environment, listing the actual servers
Ansible should target.

## What belongs here

- `staging.ini` — staging host list
- `production.ini` — production host list

## Sample: what's already in this folder

`staging.ini`:
```ini
[web]
# staging-web-1.example.com

[backend]
# staging-backend-1.example.com
```

## Instantiating for a real project

Uncomment and replace with real hostnames or IPs once servers exist:
```ini
[web]
staging-web-1.myproject.com

[backend]
staging-backend-1.myproject.com
staging-backend-2.myproject.com
```

Run against a specific environment:
```bash
ansible-playbook -i inventory/production.ini ../site.yml
```
