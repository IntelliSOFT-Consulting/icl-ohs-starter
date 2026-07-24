# Security Policies — Docker Baseline

Healthcare data-at-rest encryption & compliance profiles for containers
built from this repo.

TODO for each project instantiation:
- [ ] Define seccomp / AppArmor profiles for `gateway-node` and `service-fhir-java`
- [ ] Enforce non-root `USER` in all Dockerfiles
- [ ] Document data-at-rest encryption approach for volumes (e.g. `fhir-db-data`)
- [ ] Add vulnerability scanning step (Trivy/Grype) to `cd-core.yml` and `cd-infra.yml`
- [ ] Record HIPAA/GDPR control mapping for auditors
