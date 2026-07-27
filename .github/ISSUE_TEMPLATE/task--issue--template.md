---
name: Task (issue) template
about: Describe granular task to be completed
title: ''
labels: ''
assignees: ''
type: Task

---

---
name: "🛠️ Execution Task"
description: A tightly scoped, isolated technical task.
title: "Task: [Component] - [Action]"
labels: ["type: task", "status: ready-for-dev"]
---

## 📍 Context
- **Parent Feature:** <!-- Link back to the main Feature epic issue in this repo, e.g. #42 -->
- **Target Component:** [e.g., Web (`apps/web-react`), Mobile (`apps/mobile-kotlin`), Core platform (`apps/core-platform`), Infrastructure (`infra`)]
---

## 💻 Technical Requirements
*Developer Note: Adhere strictly to the scope boundaries detailed below.*

### Reference Files / Context Scope
- `apps/web-react/path/to/target/file.tsx`
- `apps/mobile-kotlin/path/to/related/interface.kt`
- `apps/core-platform/path/to/service.ts`
- `infra/path/to/module.tf`

### Specific Instructions
1. [ ] Implement the following interface logic...
2. [ ] Ensure errors are caught and logged via the standard logger framework.
3. [ ] Avoid changing public API signatures unless explicitly required.

---

## ✅ Definition of Done (DoD)
- [ ] Code compiles without errors or warnings.
- [ ] Unit tests cover edge cases for this change.
- [ ] No regression introduced to existing components.
