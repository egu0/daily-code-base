<template>
  <div :class="['collapsible-card', 'role-' + role]">
    <div class="card-header" @click="toggle">
      <span :class="['expand-arrow', { open: isOpen }]">&#9654;</span>
      <span class="role-tag">{{ role }}</span>
      <span class="card-preview">{{ preview }}</span>
    </div>
    <div v-if="isOpen" class="card-body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";

const props = defineProps<{
  role: string;
  preview: string;
  defaultOpen?: boolean;
}>();

const emit = defineEmits<{
  opened: [];
}>();

const isOpen = ref(props.defaultOpen ?? false);
const hasEmittedOpen = ref(false);

function toggle() {
  isOpen.value = !isOpen.value;
  if (isOpen.value && !hasEmittedOpen.value) {
    hasEmittedOpen.value = true;
    emit("opened");
  }
}
</script>

<style scoped>
.collapsible-card {
  background: #16213e;
  border-radius: 6px;
  margin-bottom: 8px;
  overflow: hidden;
  border: 1px solid #1a1a3e;
}

.card-header {
  padding: 10px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  transition: background 0.15s;
}

.card-header:hover {
  background: #1a2744;
}

.expand-arrow {
  font-size: 12px;
  color: #8892b0;
  transition: transform 0.2s;
}

.expand-arrow.open {
  transform: rotate(90deg);
}

.role-tag {
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 3px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-preview {
  font-size: 13px;
  color: #8892b0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
}

.card-body {
  padding: 12px 16px;
  border-top: 1px solid #1a1a3e;
}

/* Role colors */
.role-system .role-tag {
  background: #1e3a5f;
  color: #64b5f6;
}
.role-user .role-tag {
  background: #1b4332;
  color: #81c784;
}
.role-developer .role-tag {
  background: #4e3535;
  color: rgb(239, 105, 105);
}
.role-assistant .role-tag {
  background: #3c1f5e;
  color: #ce93d8;
}
.role-tool .role-tag {
  background: #4a3a1f;
  color: #ffb74d;
}
.role-tools .role-tag {
  background: #727272;
  color: #dbdbdb;
}
.role-meta .role-tag {
  background: #727272;
  color: #dbdbdb;
}
.role-thinking .role-tag {
  background: #3c1f5e;
  color: #ce93d8;
}
.role-output .role-tag {
  background: #1b4332;
  color: #81c784;
}
.role-tool-callings .role-tag {
  background: #4a3a1f;
  color: #ffb74d;
}

.role-system .card-header {
  border-left: 3px solid #64b5f6;
}
.role-user .card-header {
  border-left: 3px solid #81c784;
}
.role-developer .card-header {
  border-left: 3px solid #fff;
}
.role-assistant .card-header {
  border-left: 3px solid #ce93d8;
}
.role-tool .card-header {
  border-left: 3px solid #ffb74d;
}
.role-tools .card-header {
  border-left: 3px solid #727272;
}
.role-meta .card-header {
  border-left: 3px solid #727272;
}
.role-thinking .card-header {
  border-left: 3px solid #ce93d8;
}
.role-output .card-header {
  border-left: 3px solid #81c784;
}
.role-tool-callings .card-header {
  border-left: 3px solid #ffb74d;
}
</style>
