<template>
  <div class="code-block-wrapper">
    <pre><code ref="codeEl" :class="'language-' + language">{{ code }}</code></pre>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import hljs from "highlight.js/lib/core";
import jsonLang from "highlight.js/lib/languages/json";
import markdownLang from "highlight.js/lib/languages/markdown";

hljs.registerLanguage("json", jsonLang);
hljs.registerLanguage("markdown", markdownLang);

const props = defineProps<{
  code: string;
  language: "json" | "markdown";
}>();

const codeEl = ref<HTMLElement>();

function highlight() {
  if (codeEl.value) {
    hljs.highlightElement(codeEl.value);
  }
}

onMounted(highlight);
watch(() => props.code, highlight);
</script>

<style scoped>
.code-block-wrapper {
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 8px;
}

.code-block-wrapper pre {
  margin: 0;
  padding: 12px 16px;
}

.code-block-wrapper code {
  font-family: "SF Mono", "Fira Code", Consolas, monospace;
  font-size: 13px;
  line-height: 1.5;
}
</style>
