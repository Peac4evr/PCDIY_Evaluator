<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useEvaluatorStore } from '@/stores/evaluator'
import type { HardwareItem } from '@/types/evaluation'
import HardwareCard from '@/components/HardwareCard.vue'

const store = useEvaluatorStore()

/* ── 类型筛选 ── */
const FILTER_TABS = [
  { key: 'ALL', label: '全部' },
  { key: 'CPU', label: 'CPU' },
  { key: 'MOTHERBOARD', label: '主板' },
  { key: 'MEMORY', label: '内存' },
  { key: 'GPU', label: '显卡' },
  { key: 'PSU', label: '电源' },
] as const

const activeFilter = ref<string>('ALL')
const searchQuery = ref('')

/* ── 数据 ── */
const allHardware = computed<HardwareItem[]>(() => {
  const lists = store.hardwareLists
  const result: HardwareItem[] = []
  for (const type of Object.keys(lists)) {
    result.push(...lists[type])
  }
  return result
})

const filteredHardware = computed<HardwareItem[]>(() => {
  let items = allHardware.value

  // 类型筛选：直接按后端 type 字符串从 store 中取
  if (activeFilter.value !== 'ALL') {
    items = store.hardwareLists[activeFilter.value] ?? []
  }

  // 搜索
  const q = searchQuery.value.trim().toLowerCase()
  if (q) {
    items = items.filter(
      (h) =>
        h.name.toLowerCase().includes(q) ||
        h.brand.toLowerCase().includes(q) ||
        (h.specs && h.specs.toLowerCase().includes(q)),
    )
  }

  return items
})

/* ── 生命周期 ── */
onMounted(() => {
  store.fetchAllHardware()
})
</script>

<template>
  <div class="library-page">
    <!-- 页头 -->
    <header class="page-header">
      <h1 class="page-title">硬件库</h1>
      <p class="page-desc">浏览所有可用硬件，查看详细规格参数</p>
    </header>

    <!-- 搜索框 -->
    <div class="search-bar">
      <span class="search-icon" aria-hidden="true">🔍</span>
      <input
        v-model="searchQuery"
        type="text"
        class="search-input"
        placeholder="搜索硬件名称、品牌或规格…"
        aria-label="搜索硬件"
      />
    </div>

    <!-- 类型筛选标签 -->
    <div class="filter-tabs" role="tablist" aria-label="硬件类型筛选">
      <button
        v-for="tab in FILTER_TABS"
        :key="tab.key"
        class="filter-tab"
        :class="{ active: activeFilter === tab.key }"
        role="tab"
        :aria-selected="activeFilter === tab.key"
        @click="activeFilter = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <!-- 硬件卡片网格 -->
    <div v-if="filteredHardware.length > 0" class="hw-grid">
      <HardwareCard
        v-for="item in filteredHardware"
        :key="item.id"
        :item="item"
        :style="{ animationDelay: `${Math.min((filteredHardware.indexOf(item) % 12) * 40, 400)}ms` }"
        style="animation: fade-up 0.4s var(--ease-out-expo) both"
      />
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-state">
      <span class="empty-icon">📦</span>
      <p>没有找到匹配的硬件</p>
    </div>
  </div>
</template>

<style scoped>
.library-page {
  max-width: 960px;
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-lg) var(--space-3xl);
}

/* ── 页头 ── */
.page-header {
  text-align: center;
  margin-bottom: var(--space-xl);
  animation: fade-up 0.5s var(--ease-out-expo);
}

.page-title {
  font-size: 26px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.03em;
  margin-bottom: var(--space-sm);
}

.page-desc {
  font-size: 14px;
  color: var(--text-secondary);
}

/* ── 搜索 ── */
.search-bar {
  position: relative;
  max-width: 480px;
  margin: 0 auto var(--space-lg);
  animation: fade-up 0.5s var(--ease-out-expo) 0.05s both;
}

.search-icon {
  position: absolute;
  left: 14px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 15px;
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 10px 14px 10px 40px;
  border: 1px solid var(--border-default);
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-family: var(--font-sans);
  color: var(--text-primary);
  background: var(--bg-elevated);
  transition: border-color 0.2s var(--ease-out-expo), box-shadow 0.2s var(--ease-out-expo);
}

.search-input:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-dim);
}

.search-input::placeholder {
  color: var(--text-muted);
}

/* ── 筛选标签 ── */
.filter-tabs {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-bottom: var(--space-xl);
  flex-wrap: wrap;
  animation: fade-up 0.5s var(--ease-out-expo) 0.1s both;
}

.filter-tab {
  padding: 6px 16px;
  border-radius: var(--radius-full);
  border: 1px solid var(--border-default);
  background: var(--bg-elevated);
  font-size: 13px;
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s var(--ease-out-expo);
}

.filter-tab:hover {
  border-color: var(--border-strong);
  color: var(--text-primary);
}

.filter-tab.active {
  background: var(--accent);
  border-color: var(--accent);
  color: #ffffff;
}

/* ── 网格 ── */
.hw-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--space-md);
}

/* ── 空状态 ── */
.empty-state {
  text-align: center;
  padding: var(--space-3xl) var(--space-lg);
  color: var(--text-muted);
}

.empty-icon {
  font-size: 48px;
  display: block;
  margin-bottom: var(--space-md);
}
</style>
