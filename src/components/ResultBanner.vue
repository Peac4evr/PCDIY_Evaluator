<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  passed: boolean
  totalPower: number
  detailsCount: { passed: number; warn: number; failed: number }
}>()

const summary = computed(() => {
  if (props.passed) {
    return {
      icon: '✓',
      label: '装机方案校验通过',
      subtitle: '所有关键指标均符合兼容性与供电安全要求',
      tone: 'banner--passed' as const,
    }
  }
  return {
    icon: '✕',
    label: '存在严重不兼容问题',
    subtitle: '请根据下方诊断详情调整配件选择',
    tone: 'banner--failed' as const,
  }
})
</script>

<template>
  <div :class="['result-banner', summary.tone]">
    <div class="result-banner__body">
      <!-- 头部：图标 + 标题 -->
      <div class="result-banner__header">
        <div :class="['result-banner__icon-ring', summary.tone]">
          <span class="result-banner__icon">{{ summary.icon }}</span>
        </div>
        <div>
          <h2 class="result-banner__title">{{ summary.label }}</h2>
          <p class="result-banner__subtitle">{{ summary.subtitle }}</p>
        </div>
      </div>

      <!-- 统计面板 -->
      <div class="result-banner__stats">
        <!-- 功耗：主指标 -->
        <div class="stat stat--hero">
          <span class="stat__value stat__value--power">{{ totalPower }}</span>
          <span class="stat__unit">W</span>
          <span class="stat__label">预估峰值功耗</span>
        </div>

        <!-- 分隔线 -->
        <div class="stat-divider" aria-hidden="true"></div>

        <!-- 诊断计数 -->
        <div class="stat-group">
          <div class="stat stat--inline">
            <span class="stat__chip stat__chip--passed">{{ detailsCount.passed }}</span>
            <span class="stat__label">通过</span>
          </div>
          <div class="stat stat--inline">
            <span class="stat__chip stat__chip--warn">{{ detailsCount.warn }}</span>
            <span class="stat__label">警告</span>
          </div>
          <div class="stat stat--inline">
            <span class="stat__chip stat__chip--failed">{{ detailsCount.failed }}</span>
            <span class="stat__label">故障</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.result-banner {
  display: flex;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-elevated);
  animation: banner-reveal 0.6s var(--ease-out-expo);
  border: 1px solid var(--border-subtle);
}

@keyframes banner-reveal {
  from { opacity: 0; transform: translateY(-20px) scale(0.97); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}

/* ── 状态侧边条 ── */
.banner--passed {
  border-left: 4px solid var(--status-passed);
}

.banner--failed {
  border-left: 4px solid var(--status-failed);
}

/* ── 主体内容 ── */
.result-banner__body {
  flex: 1;
  padding: var(--space-lg) var(--space-xl);
  background: var(--bg-elevated);
}

/* ── 头部 ── */
.result-banner__header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.result-banner__icon-ring {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.banner--passed .result-banner__icon-ring {
  background: var(--status-passed-bg);
}

.banner--failed .result-banner__icon-ring {
  background: var(--status-failed-bg);
}

.result-banner__icon {
  font-size: 1.5rem;
  font-weight: 800;
  line-height: 1;
}

.banner--passed .result-banner__icon { color: var(--status-passed); }
.banner--failed .result-banner__icon { color: var(--status-failed); }

.result-banner__title {
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.result-banner__subtitle {
  font-size: 0.78rem;
  color: var(--text-secondary);
  margin-top: 2px;
}

/* ── 统计面板 ── */
.result-banner__stats {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
}

/* 主指标：功耗 */
.stat--hero {
  display: flex;
  align-items: baseline;
  gap: 2px;
  flex-shrink: 0;
}

.stat__value--power {
  font-family: var(--font-mono);
  font-size: 2.2rem;
  font-weight: 700;
  color: var(--accent);
  line-height: 1;
}

.stat__unit {
  font-family: var(--font-mono);
  font-size: 1rem;
  font-weight: 600;
  color: var(--accent);
  opacity: 0.7;
}

.stat--hero .stat__label {
  display: block;
  width: 100%;
  font-size: 0.68rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-top: 4px;
}

/* 分隔线 */
.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--border-subtle);
  flex-shrink: 0;
}

/* 诊断计数组 */
.stat-group {
  display: flex;
  gap: var(--space-md);
}

.stat--inline {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.stat__chip {
  font-family: var(--font-mono);
  font-size: 1.2rem;
  font-weight: 700;
  min-width: 36px;
  text-align: center;
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.stat__chip--passed {
  color: var(--status-passed);
  background: var(--status-passed-bg);
}

.stat__chip--warn {
  color: var(--status-warn);
  background: var(--status-warn-bg);
}

.stat__chip--failed {
  color: var(--status-failed);
  background: var(--status-failed-bg);
}

.stat--inline .stat__label {
  font-size: 0.65rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.08em;
}

@media (max-width: 520px) {
  .result-banner__stats {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-md);
  }
  .stat-divider {
    width: 100%;
    height: 1px;
  }
}
</style>
