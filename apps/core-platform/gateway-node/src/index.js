// TODO: replace with real API Gateway / Auth Layer (OAuth2 / OpenID Connect)
// Loads the shared root .env (see /.env.example) — not a local file.
require("dotenv").config({ path: require("path").resolve(__dirname, "../../../.env") });
const express = require("express");
const app = express();

app.get("/health", (_req, res) => res.status(200).send("ok"));

const port = process.env.GATEWAY_PORT || process.env.PORT || 3000;
app.listen(port, () => console.log(`gateway-node listening on ${port}`));
