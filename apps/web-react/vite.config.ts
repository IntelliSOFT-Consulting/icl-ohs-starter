import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// Reads the shared root .env (see /.env.example) instead of a local one —
// envDir points three levels up to the repo root.
export default defineConfig({
  plugins: [react()],
  envDir: "../../",
});
