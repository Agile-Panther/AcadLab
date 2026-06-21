// @lovable.dev/vite-tanstack-config already includes the following — do NOT add them manually
// or the app will break with duplicate plugins:
//   - tanstackStart, viteReact, tailwindcss, tsConfigPaths, nitro (build-only using cloudflare as a default target),
//     componentTagger (dev-only), VITE_* env injection, @ path alias, React/TanStack dedupe,
//     error logger plugins, and sandbox detection (port/host/strictPort).
// You can pass additional config via defineConfig({ vite: { ... }, etc... }) if needed.
import { defineConfig } from "@lovable.dev/vite-tanstack-config";

export default defineConfig({
  tanstackStart: {
    // Redirect TanStack Start's bundled server entry to src/server.ts (our SSR error wrapper).
    // nitro/vite builds from this
    server: { entry: "server" },
  },
  // Proxy every /backend/* call to the Spring Boot API. The target comes from
  // VITE_API_TARGET (http://backend:8080 in docker-compose) and falls back to
  // localhost:8080 for plain local dev. No path rewrite: the controllers are
  // already mapped under "backend/..." (e.g. backend/solicitacoes).
  vite: {
    server: {
      proxy: {
        "/backend": {
          target: process.env.VITE_API_TARGET ?? "http://localhost:8080",
          changeOrigin: true,
        },
      },
    },
  },
});
