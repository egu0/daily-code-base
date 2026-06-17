<template>
  <div class="sidebar">
    <div class="sidebar-title">Requests</div>
    <div class="request-list">
      <div
        v-for="(group, index) in groupedRequests"
        :key="group.date"
        class="request-group"
      >
        <div class="group-header" @click="toggleGroup(index)">
          <span :class="['expand-arrow', { open: expandedIndex === index }]"
            >&#9654;</span
          >
          <span class="group-date">{{ group.date }}</span>
          <span class="group-count">{{ group.items.length }}</span>
        </div>
        <div v-if="expandedIndex === index" class="group-items">
          <div
            v-for="req in group.items"
            :key="req.name"
            :class="['request-item', { active: selectedName === req.name }]"
            @click="$emit('select', req.name)"
          >
            <div class="name">{{ formatTime(req.name) }}</div>
            <div class="model">{{ req.model }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { fetchRequests, type RequestItem } from "../api";

defineProps<{
  selectedName: string | null;
}>();

defineEmits<{
  select: [name: string];
}>();

interface GroupedRequests {
  date: string;
  items: RequestItem[];
}

const requests = ref<RequestItem[]>([]);
const expandedIndex = ref<number | null>(null);

const groupedRequests = computed<GroupedRequests[]>(() => {
  const groups: Map<string, RequestItem[]> = new Map();

  requests.value.forEach((req) => {
    const date = req.name.split("_")[0];
    if (!groups.has(date)) {
      groups.set(date, []);
    }
    groups.get(date)!.push(req);
  });

  // Sort by date descending, items by time ascending (earlier first)
  return Array.from(groups.entries())
    .sort((a, b) => b[0].localeCompare(a[0]))
    .map(([date, items]) => ({
      date,
      items: items.sort((a, b) => a.name.localeCompare(b.name)),
    }));
});

function toggleGroup(index: number) {
  expandedIndex.value = expandedIndex.value === index ? null : index;
}

function formatTime(name: string): string {
  const parts = name.split("_");
  return parts.length > 1 ? parts[1] : name;
}

onMounted(async () => {
  const { data } = await fetchRequests();
  requests.value = data;
});
</script>

<style scoped>
.sidebar {
  width: 280px;
  min-width: 280px;
  background: #16213e;
  border-right: 1px solid #0f3460;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
}

.sidebar-title {
  padding: 12px 16px;
  font-size: 13px;
  color: #8892b0;
  text-transform: uppercase;
  letter-spacing: 1px;
  border-bottom: 1px solid #0f3460;
}

.request-list {
  overflow-y: auto;
}

.request-item {
  padding: 12px 16px;
  cursor: pointer;
  border-bottom: 1px solid #1a1a3e;
  transition: background 0.15s;
}

.request-item:hover {
  background: #1a2744;
}

.request-item.active {
  background: #0f3460;
  border-left: 3px solid #e94560;
}

.request-item .name {
  font-size: 14px;
  font-weight: 500;
  color: #e0e0e0;
}

.request-item .model {
  font-size: 12px;
  color: #8892b0;
  margin-top: 4px;
}

.request-group {
  border-bottom: 1px solid #0f3460;
}

.group-header {
  padding: 10px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #1a2744;
  transition: background 0.15s;
}

.group-header:hover {
  background: #233354;
}

.expand-arrow {
  font-size: 10px;
  color: #8892b0;
  transition: transform 0.2s;
}

.expand-arrow.open {
  transform: rotate(90deg);
}

.group-date {
  font-size: 13px;
  font-weight: 600;
  color: #e0e0e0;
}

.group-count {
  font-size: 11px;
  color: #8892b0;
  margin-left: auto;
  padding: 2px 8px;
  background: #16213e;
  border-radius: 10px;
}

.group-items {
  background: #0f1a2e;
}
</style>
