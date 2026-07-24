# apps/web-react/src/

Application source: entrypoint, root component, and feature components.

## What belongs here

- `main.tsx` — React root render, mounts `<App />`
- `App.tsx` — top-level layout/shell
- `components/` — feature components, one file per component

## Sample: what's already in this folder

`main.tsx`:
```tsx
import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";

ReactDOM.createRoot(document.getElementById("root")!).render(<App />);
```

`components/` is currently empty — add feature components here, e.g.:

```tsx
// components/RiskAnalysisWidget.tsx
export function RiskAnalysisWidget({ score }: { score: number }) {
  return <div>Risk score: {score}</div>;
}
```
