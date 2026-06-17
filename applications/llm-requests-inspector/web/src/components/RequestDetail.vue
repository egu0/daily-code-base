<template>
  <div class="detail">
    <div v-if="!detail" class="empty-state">
      Select a request to view details
    </div>
    <template v-else>
      <!-- Usage Section -->
      <div class="section">
        <div class="section-title">Usage</div>
        <div class="meta-info">
          <div class="meta-item">
            Model:
            <span class="meta-value">{{ detail.model || "unknown" }}</span>
          </div>
          <template
            v-if="detail.usage && detail.usage.prompt_tokens !== undefined"
          >
            <div class="meta-item">
              Input:
              <span class="meta-value"
                >{{ detail.usage.prompt_tokens.toLocaleString() }} tokens</span
              >
            </div>
            <div class="meta-item">
              Output:
              <span class="meta-value"
                >{{
                  detail.usage.completion_tokens.toLocaleString()
                }}
                tokens</span
              >
            </div>
            <div class="meta-item">
              Cost:
              <span class="meta-value"
                >${{ detail.usage.cost.toFixed(6) }}</span
              >
            </div>
          </template>
        </div>
      </div>

      <!-- Request Section -->
      <div class="section">
        <div class="section-title">Request</div>
        <CollapsibleCard
          v-for="(msg, idx) in detail.messages"
          :key="`${props.reqId}-${idx}`"
          :role="msg.role"
          :preview="getMessagePreview(msg)"
        >
          <CodeBlock
            v-for="(block, bIdx) in msg.content"
            :key="bIdx"
            :code="block.data"
            :language="block.mode === 'markdown' ? 'markdown' : 'json'"
          />
        </CollapsibleCard>
      </div>

      <!-- Response Section -->
      <div class="section">
        <div class="section-title">Response</div>
        <CollapsibleCard
          v-for="(msg, idx) in detail.resp_messages"
          :key="idx"
          :role="msg.role"
          :preview="getMessagePreview(msg)"
        >
          <CodeBlock
            v-for="(block, bIdx) in msg.content"
            :key="bIdx"
            :code="block.data"
            :language="block.mode === 'markdown' ? 'markdown' : 'json'"
          />
        </CollapsibleCard>

        <div v-if="detail.resp_messages.length == 0" class="no-data">
          No response data available
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { fetchRequestDetail, type RequestDetail, type Message } from "../api";
import CollapsibleCard from "./CollapsibleCard.vue";
import CodeBlock from "./CodeBlock.vue";

const props = defineProps<{
  reqId: string | null;
}>();

const detail = ref<RequestDetail | null>(null);

function getMessagePreview(msg: Message): string {
  if (!msg.content || msg.content.length === 0) return "(empty)";
  const first = msg.content[0];
  const text = first.data || "";
  return text.length > 80 ? text.substring(0, 80) + "..." : text;
}

async function loadDetail(reqId: string) {
  const { data } = await fetchRequestDetail(reqId);
  detail.value = data;
}

watch(
  () => props.reqId,
  (newId) => {
    if (newId) loadDetail(newId);
    else detail.value = null;
  },
  { immediate: true },
);
</script>

<style scoped>
.detail {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #8892b0;
  font-size: 20px;
}

.section {
  margin-bottom: 24px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #e94560;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #0f3460;
}

.meta-info {
  background: #16213e;
  border-radius: 6px;
  padding: 12px 16px;
  font-size: 13px;
  color: #8892b0;
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.meta-value {
  color: #e0e0e0;
  font-weight: 500;
}

.no-data {
  color: #8892b0;
  font-size: 13px;
  padding: 8px 16px;
}

.loading {
  color: #8892b0;
  font-size: 13px;
}

.empty-hint {
  color: #8892b0;
  font-size: 13px;
}
</style>
