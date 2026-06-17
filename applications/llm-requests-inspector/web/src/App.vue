<template>
  <div class="app">
    <div class="header">
      <span @click="go_index">LLM API Logger</span>
    </div>
    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

function go_index() {
  router.push("/");
}
</script>

<style scoped>
.app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: #16213e;
  padding: 16px 24px;
  border-bottom: 1px solid #0f3460;
  font-size: 24px;
  font-weight: 600;
  color: #e94560;
}

.header span {
  cursor: pointer;
}

.container {
  display: flex;
  flex: 1;
  overflow: hidden;
}
</style>
