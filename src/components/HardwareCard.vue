<script setup lang="ts">
import type { HardwareItem, HardwareType } from '@/types/evaluation'
import { TYPE_LABEL_MAP } from '@/types/evaluation'

defineProps<{
  item: HardwareItem
}>()

function getTypeClass(type: string): string {
  return `type-bar--${type.toLowerCase()}`
}

function getTypeLabel(type: string): string {
  return (TYPE_LABEL_MAP as Record<string, string>)[type] ?? type
}
</script>

<template>
  <div class="hw-card">
    <div class="type-bar" :class="getTypeClass(item.type)"></div>
    <div class="hw-body">
      <div class="hw-header">
        <h3 class="hw-name">{{ item.name }}</h3>
        <span class="hw-type-badge">{{ getTypeLabel(item.type) }}</span>
      </div>
      <div class="hw-meta">
        <span class="hw-brand">{{ item.brand }}</span>
        <span v-if="item.tdp" class="hw-tdp">{{ item.tdp }}W</span>
      </div>
      <p v-if="item.specs" class="hw-specs">{{ item.specs }}</p>
    </div>
  </div>
</template>

<style scoped>
.hw-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  overflow: hidden;
  display: flex;
  transition: transform 0.2s var(--ease-out-expo), box-shadow 0.2s var(--ease-out-expo);
}

.hw-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-elevated);
}

/* ── 左侧类型色条 ── */
.type-bar {
  width: 4px;
  flex-shrink: 0;
}

.type-bar--cpu           { background: #0066ff; }
.type-bar--motherboard   { background: #00a86b; }
.type-bar--memory        { background: #e67700; }
.type-bar--gpu           { background: #7c3aed; }
.type-bar--psu           { background: #e03131; }

/* ── 内容 ── */
.hw-body {
  padding: var(--space-md);
  flex: 1;
  min-width: 0;
}

.hw-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: var(--space-sm);
  margin-bottom: var(--space-xs);
}

.hw-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hw-type-badge {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: var(--radius-full);
  background: var(--bg-field);
  font-size: 11px;
  font-weight: 500;
  color: var(--text-muted);
}

.hw-meta {
  display: flex;
  gap: var(--space-sm);
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: var(--space-xs);
}

.hw-brand {
  font-weight: 500;
}

.hw-tdp {
  font-family: var(--font-mono);
  color: var(--text-secondary);
}

.hw-specs {
  font-size: 12px;
  color: var(--text-muted);
  font-family: var(--font-mono);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
