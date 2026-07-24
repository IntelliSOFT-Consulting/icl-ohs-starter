# apps/core-platform/gateway-node/

Node.js API Gateway / Auth Layer. Sits in front of `service-fhir-java` and
handles routing, auth (OAuth2/OpenID Connect), and health checks.

## What belongs here

- `package.json` — dependencies, including `@ohs/generated-ts`
- `Dockerfile` — multi-stage build (install deps, then slim runtime image)
- `src/` — Express (or equivalent) app code

## Environment variables

This service reads from the **shared root `.env`** (see `/.env.example`),
not a local file. `src/index.js` loads it explicitly:
```js
require("dotenv").config({ path: path.resolve(__dirname, "../../../.env") });
```
Relevant vars: `GATEWAY_PORT`, `FHIR_SERVICE_URL`, `OAUTH_ISSUER_URL`,
`OAUTH_CLIENT_ID`, `OAUTH_CLIENT_SECRET`, `JWT_SECRET`.

## Sample: what's already in this folder

`src/index.js`:
```js
const express = require("express");
const app = express();

app.get("/health", (_req, res) => res.status(200).send("ok"));

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`gateway-node listening on ${port}`));
```

`Dockerfile` (excerpt):
```dockerfile
FROM node:20-slim AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY . .

FROM node:20-slim
WORKDIR /app
COPY --from=build /app .
EXPOSE 3000
CMD ["node", "src/index.js"]
```

## Instantiating for a real project

- Add real routes/middleware for auth (OAuth2/OpenID Connect) and proxying
  to `service-fhir-java`.
- Import generated types from `@ohs/generated-ts` once schemas exist.
