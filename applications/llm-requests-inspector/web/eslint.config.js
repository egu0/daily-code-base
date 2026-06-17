import pluginVue from "eslint-plugin-vue";
import vueParser from "vue-eslint-parser";
import tsParser from "@typescript-eslint/parser";

export default [
  {
    plugins: {
      vue: pluginVue,
    },
    files: ["**/*.vue"],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        parser: tsParser,
      },
    },
    rules: {
      ...pluginVue.configs.recommended.rules,
      "vue/multi-word-component-names": "off",
      "vue/no-v-html": "warn",
    },
  },
  {
    files: ["**/*.ts"],
    plugins: {
      "@typescript-eslint": pluginVue,
    },
    languageOptions: {
      parser: tsParser,
    },
    rules: {
      "@typescript-eslint/no-unused-vars": ["error", { ignorePattern: "^_" }],
    },
  },
  {
    ignores: ["node_modules", "dist", "*.min.js"],
  },
];
