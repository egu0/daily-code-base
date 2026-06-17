/** @type {import('stylelint').Config} */
export default {
  extends: "stylelint-config-standard",
  rules: {
    "no-empty-source": null,
  },
  ignoreFiles: ["**/dist/**", "**/node_modules/**", "**/*.vue"],
};
