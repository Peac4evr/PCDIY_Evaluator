<script setup lang="ts">
import type { DiagnosisItem } from '@/types/evaluation'
import { computed, ref } from 'vue'

const props = defineProps<{ item: DiagnosisItem; index: number }>()

const expanded = ref(false)

const statusIcon = computed(() => {
  switch (props.item.status) {
    case 'PASSED': return '✓'
    case 'WARN':   return '⚠'
    case 'FAILED': return '✕'
    default:       return '—'
  }
})

const statusLabel = computed(() => {
  switch (props.item.status) {
    case 'PASSED': return '通过'
    case 'WARN':   return '警告'
    case 'FAILED': return '故障'
    default:       return '跳过'
  }
})

// 从消息中提取【...】标记的关键信息用于高亮展示
const highlights = computed(() => {
  const matches = props.item.message.match(/【(.+?)】/g)
  return matches ? matches.map((m) => m.replace(/【|】/g, '')) : []
})

// 将消息按【...】分割，用于渲染高亮片段
const messageParts = computed(() => {
  return props.item.message.split(/(【.+?】)/g)
})
</script>

<template>
  <div
    :class="['diag-card', `diag-card--${item.status.toLowerCase()}`]"
    :style="{ '--stagger-index': index }"
  >
    <!-- 左侧状态指示区 -->
    <div :class="['diag-card__indicator', `indicator--${item.status.toLowerCase()}`]">
      <span class="diag-card__icon">{{ statusIcon }}</span>
      <span class="diag-card__index">{{ String(index + 1).padStart(2, '0') }}</span>
    </div>

    <!-- 主体内容 -->
    <div class="diag-card__body">
      <div class="diag-card__header">
        <h4 class="diag-card__rule">{{ item.ruleName }}</h4>
        <span :class="['diag-card__badge', `badge--${item.status.toLowerCase()}`]">
          {{ statusLabel }}
        </span>
      </div>

      <!-- 关联硬件标签 -->
      <div v-if="item.relatedParts && item.relatedParts.length > 0" class="diag-card__parts">
        <span class="diag-card__parts-label">涉及硬件：</span>
        <span
          v-for="(part, i) in item.relatedParts"
          :key="i"
          :class="['diag-card__part-chip', `part-chip--${item.status.toLowerCase()}`]"
        >
          {{ part }}
        </span>
      </div>

      <!-- 消息（支持【高亮】） -->
      <div class="diag-card__message">
        <template v-for="(part, i) in messageParts" :key="i">
          <mark v-if="part.startsWith('【')" class="diag-card__highlight">
            {{ part.replace(/【|】/g, '') }}
          </mark>
          <span v-else>{{ part }}</span>
        </template>
      </div>

      <!-- 展开查看更多详情（仅失败项） -->
      <button
        v-if="item.status === 'FAILED'"
        class="diag-card__expand"
        @click="expanded = !expanded"
      >
        <span>{{ expanded ? '收起详情' : '查看不兼容详情' }}</span>
        <span :class="['diag-card__expand-arrow', { 'diag-card__expand-arrow--open': expanded }]">▾</span>
      </button>

      <Transition name="expand">
        <div v-if="expanded && item.status === 'FAILED'" class="diag-card__detail">
          <div class="diag-card__detail-box">
            <span class="diag-card__detail-icon">🔧</span>
            <div>
              <p class="diag-card__detail-title">故障原因分析</p>
              <p class="diag-card__detail-text">
                该诊断项被标记为
                <strong>严重不兼容</strong>，属于一票否决级别的硬件冲突。
                装机方案将无法通过验证，请根据上方高亮提示调整硬件选型。
              </p>
            </div>
          </div>
          <div class="diag-card__detail-box">
            <span class="diag-card__detail-icon">💡</span>
            <div>
              <p class="diag-card__detail-title">解决建议</p>
              <p class="diag-card__detail-text">
                请更换不兼容的配件，确保 CPU 插槽与主板匹配、内存代数一致、
                电源额定功率充足。可使用上方的下拉选择器重新挑选兼容配件。
              </p>
            </div>
          </div>
        </div>
      </Transition>
    </div>
  </div>
</template>

<style scoped>
/* ═════════════════════════════════════════
   Diagnosis Card — 增强版
   ═════════════════════════════════════════ */

.diag-card {
  display: flex;
  gap: var(--space-md);
  padding: var(--space-lg);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-subtle);
  background: var(--bg-elevated);
  opacity: 0;
  animation: card-enter 0.5s var(--ease-out-expo) forwards;
  animation-delay: calc(var(--stagger-index) * 70ms + 100ms);
  transition: border-color 0.25s, box-shadow 0.25s, transform 0.25s;
}

@keyframes card-enter {
  from { opacity: 0; transform: translateX(-16px); }
  to   { opacity: 1; transform: translateX(0); }
}

.diag-card:hover {
  border-color: var(--border-default);
  transform: translateX(3px);
}

/* ── 状态变体 ── */
.diag-card--passed {
  border-left: 3px solid var(--status-passed);
}

.diag-card--warn {
  border-left: 3px solid var(--status-warn);
  background: linear-gradient(135deg, var(--bg-elevated) 70%, var(--status-warn-bg));
}

.diag-card--failed {
  border-left: 3px solid var(--status-failed);
  box-shadow: 0 0 0 1px rgba(255, 61, 90, 0.12), 0 4px 16px rgba(255, 61, 90, 0.06);
  background: linear-gradient(135deg, var(--bg-elevated) 70%, var(--status-failed-bg));
}

.diag-card--skipped {
  border-left: 3px solid var(--status-skipped);
  opacity: 0.5;
}

/* ── 左侧指示区 ── */
.diag-card__indicator {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--space-xs);
  flex-shrink: 0;
  min-width: 32px;
}

.diag-card__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  font-size: 0.95rem;
  font-weight: 700;
}

.indicator--passed .diag-card__icon {
  color: var(--status-passed);
  background: var(--status-passed-bg);
}

.indicator--warn .diag-card__icon {
  color: var(--status-warn);
  background: var(--status-warn-bg);
}

.indicator--failed .diag-card__icon {
  color: var(--status-failed);
  background: var(--status-failed-bg);
  box-shadow: 0 0 8px var(--status-failed-bg);
}

.indicator--skipped .diag-card__icon {
  color: var(--status-skipped);
  background: var(--status-skipped-bg);
}

.diag-card__index {
  font-family: var(--font-mono);
  font-size: 0.6rem;
  font-weight: 600;
  color: var(--text-muted);
  letter-spacing: 0.04em;
}

/* ── 主体内容 ── */
.diag-card__body {
  flex: 1;
  min-width: 0;
}

.diag-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-sm);
  margin-bottom: var(--space-xs);
}

.diag-card__rule {
  font-size: 0.88rem;
  font-weight: 600;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}

/* ── 状态徽章 ── */
.diag-card__badge {
  font-size: 0.65rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  padding: 3px 10px;
  border-radius: var(--radius-full);
  flex-shrink: 0;
  border: 1px solid transparent;
}

.badge--passed {
  color: var(--status-passed);
  background: var(--status-passed-bg);
  border-color: rgba(0, 230, 118, 0.25);
}

.badge--warn {
  color: var(--status-warn);
  background: var(--status-warn-bg);
  border-color: rgba(255, 171, 0, 0.25);
}

.badge--failed {
  color: var(--status-failed);
  background: var(--status-failed-bg);
  border-color: rgba(255, 61, 90, 0.3);
}

.badge--skipped {
  color: var(--status-skipped);
  background: var(--status-skipped-bg);
}

/* ── 关联硬件标签 ── */
.diag-card__parts {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: var(--space-sm);
}

.diag-card__parts-label {
  font-size: 0.68rem;
  color: var(--text-muted);
  font-weight: 500;
}

.diag-card__part-chip {
  font-size: 0.7rem;
  font-weight: 500;
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-family: var(--font-sans);
}

.part-chip--failed {
  color: var(--status-failed);
  background: var(--status-failed-bg);
  border: 1px solid rgba(255, 61, 90, 0.2);
}

.part-chip--warn {
  color: var(--status-warn);
  background: var(--status-warn-bg);
  border: 1px solid rgba(255, 171, 0, 0.2);
}

.part-chip--passed {
  color: var(--status-passed);
  background: var(--status-passed-bg);
  border: 1px solid rgba(0, 230, 118, 0.2);
}

.part-chip--skipped {
  color: var(--status-skipped);
  background: var(--status-skipped-bg);
}

/* ── 消息 ── */
.diag-card__message {
  font-size: 0.84rem;
  color: var(--text-secondary);
  line-height: 1.7;
}

.diag-card__highlight {
  background: rgba(255, 171, 0, 0.18);
  color: var(--text-primary);
  font-weight: 600;
  padding: 1px 4px;
  border-radius: 3px;
  white-space: normal;
}

.diag-card--failed .diag-card__highlight {
  background: rgba(255, 61, 90, 0.12);
  color: var(--status-failed);
}

.diag-card--passed .diag-card__highlight {
  background: rgba(0, 230, 118, 0.12);
  color: var(--status-passed);
}

/* ── 展开按钮 ── */
.diag-card__expand {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: var(--space-sm);
  padding: 4px 12px;
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm);
  background: var(--bg-field);
  color: var(--text-secondary);
  font-size: 0.72rem;
  font-family: var(--font-sans);
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s;
}

.diag-card__expand:hover {
  border-color: var(--border-default);
  color: var(--text-primary);
}

.diag-card__expand-arrow {
  font-size: 0.65rem;
  transition: transform 0.25s;
}

.diag-card__expand-arrow--open {
  transform: rotate(180deg);
}

/* ── 详情区域 ── */
.diag-card__detail {
  margin-top: var(--space-md);
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

.diag-card__detail-box {
  display: flex;
  gap: var(--space-sm);
  padding: var(--space-md);
  border-radius: var(--radius-sm);
  background: var(--bg-field);
  border: 1px solid var(--border-subtle);
}

.diag-card__detail-icon {
  font-size: 1rem;
  flex-shrink: 0;
  margin-top: 1px;
}

.diag-card__detail-title {
  font-size: 0.76rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 2px;
}

.diag-card__detail-text {
  font-size: 0.76rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* ── 展开动画 ── */
.expand-enter-active {
  transition: all 0.3s var(--ease-out-expo);
}

.expand-leave-active {
  transition: all 0.2s ease-in;
}

.expand-enter-from {
  opacity: 0;
  max-height: 0;
}

.expand-enter-to {
  opacity: 1;
  max-height: 200px;
}

.expand-leave-from {
  opacity: 1;
  max-height: 200px;
}

.expand-leave-to {
  opacity: 0;
  max-height: 0;
}
</style>
