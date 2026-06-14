<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue'
import type { HardwareItem, HardwareType } from '@/types/evaluation'
import { useEvaluatorStore } from '@/stores/evaluator'

const props = defineProps<{
  modelValue: number | null
  label: string
  icon: string
  hwType: HardwareType
}>()

const emit = defineEmits<{
  'update:modelValue': [value: number | null]
}>()

const store = useEvaluatorStore()

/* ── 下拉状态 ── */
const isOpen = ref(false)
const searchText = ref('')
const highlightedIndex = ref(-1)

/* ── DOM 引用 ── */
const containerRef = ref<HTMLElement | null>(null)
const inputRef = ref<HTMLInputElement | null>(null)
const listRef = ref<HTMLElement | null>(null)

/* ── 硬件列表 ── */
const items = computed(() => store.getHardwareByType(props.hwType))

/* ── 过滤后的列表 ── */
const filteredItems = computed(() => {
  if (!searchText.value.trim()) return items.value
  const q = searchText.value.toLowerCase()
  return items.value.filter(
    (item) =>
      item.name.toLowerCase().includes(q) ||
      item.brand.toLowerCase().includes(q) ||
      item.specs.toLowerCase().includes(q) ||
      String(item.id).includes(q),
  )
})

/* ── 当前选中的硬件 ── */
const selectedItem = computed(() =>
  props.modelValue ? items.value.find((i) => i.id === props.modelValue) ?? null : null,
)

/* ── 键盘导航 ── */
function scrollToHighlighted() {
  if (!listRef.value || highlightedIndex.value < 0) return
  const el = listRef.value.children[highlightedIndex.value] as HTMLElement | undefined
  el?.scrollIntoView({ block: 'nearest' })
}

function moveHighlight(delta: number) {
  const max = filteredItems.value.length - 1
  if (max < 0) {
    highlightedIndex.value = -1
    return
  }
  highlightedIndex.value = Math.max(0, Math.min(max, highlightedIndex.value + delta))
  scrollToHighlighted()
}

function onKeyDown(e: KeyboardEvent) {
  switch (e.key) {
    case 'ArrowDown':
      e.preventDefault()
      if (!isOpen.value) open()
      else moveHighlight(1)
      break
    case 'ArrowUp':
      e.preventDefault()
      if (isOpen.value) moveHighlight(-1)
      break
    case 'Enter':
      e.preventDefault()
      if (isOpen.value && highlightedIndex.value >= 0) {
        select(filteredItems.value[highlightedIndex.value])
      } else {
        open()
      }
      break
    case 'Escape':
      if (isOpen.value) {
        e.stopPropagation()
        close()
      }
      break
  }
}

/* ── 操作 ── */
function open() {
  isOpen.value = true
  highlightedIndex.value = -1
  searchText.value = ''
  // 如果已有选中项，高亮它
  if (selectedItem.value && filteredItems.value.length > 0) {
    const idx = filteredItems.value.findIndex((i) => i.id === selectedItem.value!.id)
    if (idx >= 0) highlightedIndex.value = idx
  }
}

function close() {
  isOpen.value = false
  highlightedIndex.value = -1
}

function toggle() {
  isOpen.value ? close() : open()
}

function select(item: HardwareItem) {
  emit('update:modelValue', item.id)
  close()
  inputRef.value?.blur()
}

function clear() {
  emit('update:modelValue', null)
  searchText.value = ''
}

/* ── 点击外部关闭 ── */
function onClickOutside(e: MouseEvent) {
  if (containerRef.value && !containerRef.value.contains(e.target as Node)) {
    close()
  }
}

onMounted(() => document.addEventListener('click', onClickOutside))
onUnmounted(() => document.removeEventListener('click', onClickOutside))

// 当 modelValue 被外部清空时，同步清空搜索
watch(
  () => props.modelValue,
  (val) => {
    if (val === null) searchText.value = ''
  },
)
</script>

<template>
  <div
    ref="containerRef"
    :class="[
      'hw-select',
      `hw-select--${hwType}`,
      { 'hw-select--open': isOpen, 'hw-select--filled': modelValue !== null },
    ]"
    @keydown="onKeyDown"
  >
    <!-- 左侧色条 -->
    <span class="hw-select__accent" aria-hidden="true"></span>

    <!-- 顶部：图标 + 标签 -->
    <div class="hw-select__top">
      <span class="hw-select__icon">{{ icon }}</span>
      <span class="hw-select__label">{{ label }}</span>
      <span v-if="modelValue !== null" class="hw-select__dot"></span>
    </div>

    <!-- 触发器 -->
    <button
      type="button"
      class="hw-select__trigger"
      :aria-label="`选择${label}`"
      :aria-expanded="isOpen"
      @click="toggle"
    >
      <template v-if="selectedItem">
        <div class="hw-select__selected">
          <span class="hw-select__name">{{ selectedItem.name }}</span>
          <span class="hw-select__brand">{{ selectedItem.brand }}</span>
        </div>
      </template>
      <template v-else>
        <span class="hw-select__placeholder">{{ store.hardwareLoading ? '加载中…' : '点击选择硬件…' }}</span>
      </template>

      <div class="hw-select__actions">
        <button
          v-if="modelValue !== null"
          type="button"
          class="hw-select__clear"
          aria-label="清除选择"
          @click.stop="clear"
        >
          ✕
        </button>
        <span :class="['hw-select__arrow', { 'hw-select__arrow--open': isOpen }]">▾</span>
      </div>
    </button>

    <!-- 下拉面板 -->
    <Transition name="dropdown">
      <div v-if="isOpen" class="hw-select__dropdown">
        <!-- 搜索框 -->
        <div class="hw-select__search">
          <span class="hw-select__search-icon">🔍</span>
          <input
            ref="inputRef"
            v-model="searchText"
            type="text"
            class="hw-select__search-input"
            placeholder="搜索名称、品牌或规格…"
            @keydown.stop="onKeyDown"
          />
        </div>

        <!-- 列表 -->
        <div v-if="filteredItems.length > 0" ref="listRef" class="hw-select__list">
          <button
            v-for="(item, idx) in filteredItems"
            :key="item.id"
            type="button"
            :class="[
              'hw-select__option',
              {
                'hw-select__option--highlighted': idx === highlightedIndex,
                'hw-select__option--selected': item.id === modelValue,
              },
            ]"
            @click="select(item)"
            @mouseenter="highlightedIndex = idx"
          >
            <div class="hw-select__option-main">
              <span class="hw-select__option-name">{{ item.name }}</span>
              <span class="hw-select__option-brand">{{ item.brand }}</span>
            </div>
            <span v-if="item.specs" class="hw-select__option-specs">{{ item.specs }}</span>
            <span v-if="item.id === modelValue" class="hw-select__option-check">✓</span>
          </button>
        </div>

        <!-- 空状态 -->
        <div v-else class="hw-select__empty">
          <span v-if="store.hardwareLoading">加载中…</span>
          <span v-else>无匹配硬件</span>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
/* ═════════════════════════════════════════
   Hardware Selector — 下拉搜索选择器
   ═════════════════════════════════════════ */

.hw-select {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: var(--space-xs);
  padding: var(--space-md) var(--space-md) var(--space-md) calc(var(--space-md) + 3px);
  background: var(--bg-field);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  transition: border-color 0.25s, box-shadow 0.25s, background 0.25s;
  cursor: pointer;
  overflow: visible;
}

.hw-select--open {
  z-index: 50;
}

/* ── 左侧色条 ── */
.hw-select__accent {
  position: absolute;
  left: 0;
  top: 8px;
  bottom: 8px;
  width: 3px;
  border-radius: 0 3px 3px 0;
  background: var(--border-subtle);
  transition: background 0.3s, box-shadow 0.3s;
}

.hw-select--cpu           .hw-select__accent { background: #ff6b6b; }
.hw-select--motherboard   .hw-select__accent { background: #4ecdc4; }
.hw-select--memory        .hw-select__accent { background: #e6a817; }
.hw-select--gpu           .hw-select__accent { background: #a78bfa; }
.hw-select--psu           .hw-select__accent { background: #f97316; }

.hw-select--filled .hw-select__accent {
  box-shadow: 0 0 8px currentColor;
}

/* ── 状态 ── */
.hw-select:hover {
  border-color: var(--border-default);
  background: var(--bg-hover);
}

.hw-select--open {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px var(--accent-dim);
}

.hw-select--filled {
  border-color: var(--border-default);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

/* ── 顶部行 ── */
.hw-select__top {
  display: flex;
  align-items: center;
  gap: var(--space-xs);
}

.hw-select__icon {
  font-size: 1rem;
  line-height: 1;
  filter: grayscale(0.4);
  transition: filter 0.3s;
}

.hw-select--filled .hw-select__icon {
  filter: grayscale(0);
}

.hw-select__label {
  font-size: 0.72rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  flex: 1;
}

.hw-select__dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--accent);
  box-shadow: 0 0 6px var(--accent-glow);
  animation: glow-pulse 2s ease-in-out infinite;
}

/* ── 触发器按钮 ── */
.hw-select__trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-sm);
  background: transparent;
  border: none;
  border-bottom: 2px solid var(--border-default);
  padding: 4px 2px;
  font-family: var(--font-sans);
  font-size: 0.92rem;
  color: var(--text-primary);
  cursor: pointer;
  transition: border-color 0.25s;
  text-align: left;
  width: 100%;
}

.hw-select--open .hw-select__trigger,
.hw-select__trigger:hover {
  border-bottom-color: var(--accent);
}

.hw-select__selected {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  min-width: 0;
  flex: 1;
}

.hw-select__name {
  font-weight: 600;
  letter-spacing: -0.01em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hw-select__brand {
  font-size: 0.75rem;
  color: var(--text-muted);
  font-family: var(--font-mono);
  padding: 1px 6px;
  border-radius: var(--radius-sm);
  background: var(--bg-hover);
  border: 1px solid var(--border-subtle);
  flex-shrink: 0;
}

.hw-select__placeholder {
  color: var(--text-muted);
  font-size: 0.85rem;
}

.hw-select__actions {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-shrink: 0;
}

.hw-select__clear {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: var(--border-subtle);
  color: var(--text-muted);
  font-size: 0.6rem;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  padding: 0;
}

.hw-select__clear:hover {
  background: var(--status-failed);
  color: #fff;
}

.hw-select__arrow {
  font-size: 0.7rem;
  color: var(--text-muted);
  transition: transform 0.25s;
}

.hw-select__arrow--open {
  transform: rotate(180deg);
}

/* ═════════════════════════════════════════
   下拉面板
   ═════════════════════════════════════════ */

.hw-select__dropdown {
  position: absolute;
  left: 0;
  right: 0;
  top: calc(100% + 4px);
  z-index: 100;
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  border-radius: var(--radius-md);
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.12),
    0 4px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  max-height: 300px;
}

/* ── 搜索行 ── */
.hw-select__search {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-sm) var(--space-md);
  border-bottom: 1px solid var(--border-subtle);
  flex-shrink: 0;
}

.hw-select__search-icon {
  font-size: 0.75rem;
  flex-shrink: 0;
}

.hw-select__search-input {
  flex: 1;
  border: none;
  background: transparent;
  font-family: var(--font-sans);
  font-size: 0.82rem;
  color: var(--text-primary);
  padding: 4px 0;
  outline: none;
}

.hw-select__search-input::placeholder {
  color: var(--text-muted);
}

/* ── 列表 ── */
.hw-select__list {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-xs) 0;
}

.hw-select__option {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  width: 100%;
  padding: var(--space-sm) var(--space-md);
  border: none;
  background: transparent;
  font-family: var(--font-sans);
  text-align: left;
  cursor: pointer;
  transition: background 0.15s;
}

.hw-select__option:hover,
.hw-select__option--highlighted {
  background: var(--bg-hover);
}

.hw-select__option--selected {
  background: var(--accent-dim);
}

.hw-select__option-main {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex: 1;
  min-width: 0;
}

.hw-select__option-name {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.hw-select__option-brand {
  font-size: 0.68rem;
  color: var(--text-muted);
  padding: 1px 5px;
  border-radius: var(--radius-sm);
  background: var(--bg-field);
  flex-shrink: 0;
  font-family: var(--font-mono);
}

.hw-select__option-specs {
  font-size: 0.7rem;
  color: var(--text-muted);
  white-space: nowrap;
  flex-shrink: 0;
}

.hw-select__option-check {
  font-size: 0.8rem;
  color: var(--accent);
  font-weight: 700;
  flex-shrink: 0;
  width: 18px;
  text-align: center;
}

/* ── 空状态 ── */
.hw-select__empty {
  padding: var(--space-lg);
  text-align: center;
  font-size: 0.8rem;
  color: var(--text-muted);
}

/* ═════════════════════════════════════════
   下拉过渡动画
   ═════════════════════════════════════════ */
.dropdown-enter-active {
  transition: opacity 0.2s ease-out, transform 0.2s var(--ease-out-expo);
}

.dropdown-leave-active {
  transition: opacity 0.15s ease-in, transform 0.15s ease-in;
}

.dropdown-enter-from {
  opacity: 0;
  transform: translateY(-6px) scale(0.98);
}

.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px) scale(0.98);
}
</style>
