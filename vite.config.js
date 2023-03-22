import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
  build: {
    outDir: "target/gh-pages",
    chunkSizeWarningLimit: 5000,
  },
  plugins: [scalaJSPlugin()],
});
