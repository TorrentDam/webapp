import { defineConfig } from "vite";
import path from "node:path";

export default defineConfig(({ mode }) => {
  const task = mode === "development" ? "fastLinkJS" : "fullLinkJS";
  return {
    base: "./",
    build: {
      outDir: "target/gh-pages",
      chunkSizeWarningLimit: 5000,
      rollupOptions: {
        input: path.resolve("index.html"),
      },
    },
    resolve: {
      alias: {
        "scalajs:main.js": path.resolve(`out/app/${task}.dest/main.js`),
      },
    },
  };
});
