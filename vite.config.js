import { defineConfig } from "vite";
import path from "node:path";

export default defineConfig(({ mode }) => {
  const task = mode === "development" ? "fastLinkJS" : "fullLinkJS";
  return {
    build: {
      outDir: "target/gh-pages",
      chunkSizeWarningLimit: 5000,
      rollupOptions: {
        external: ["/config.js"],
      },
    },
    resolve: {
      alias: {
        "scalajs:main.js": path.resolve(`out/app/${task}.dest/main.js`),
      },
    },
  };
});
