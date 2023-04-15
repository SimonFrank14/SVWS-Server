import { defineConfig } from "vitest/config";
import { resolve } from 'path'

export default defineConfig({
  test: {
    globals: true,
    root: "src",
    reporters: ["default", "junit"],
    outputFile: "../../../build/coverage/svws-core/junit.xml",
    coverage: {
      reportsDirectory: "../../../build/coverage/svws-core",
    },
  },
  resolve: {
	alias: [{ find: "~", replacement: resolve(__dirname, '../../../src/core/ts/src') }],
  },
});