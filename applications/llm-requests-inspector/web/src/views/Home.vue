<template>
  <div class="home">
    <RequestList :selected-name="selectedReqId" @select="onSelect" />
    <RequestDetail :req-id="selectedReqId" />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import RequestList from "../components/RequestList.vue";
import RequestDetail from "../components/RequestDetail.vue";

const route = useRoute();
const router = useRouter();
const selectedReqId = ref<string | null>(null);

function onSelect(name: string) {
  selectedReqId.value = name;
  router.push(`/index/${name}`);
}

watch(
  () => route.params.reqId,
  (newId) => {
    if (typeof newId === "string" && newId) {
      selectedReqId.value = newId;
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.home {
  display: flex;
  flex: 1;
  overflow: hidden;
}
</style>
