<script setup lang="ts">
import { onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useEvaluatorStore } from '@/stores/evaluator'
import HardwareSelector from '@/components/HardwareSelector.vue'

const store = useEvaluatorStore()
const router = useRouter()

onMounted(() => {
  store.fetchAllHardware()
})

/* ── 提交成功后跳转到结果页 ── */
watch(
  () => store.hasEvaluated,
  (evaluated) => {
    if (evaluated && store.report) {
      router.push({ name: 'result' })
    }
  },
)

const hardwareFields = [
  { key: 'cpuId' as const,         type: 'cpu' as const,          label: 'CPU 处理器',   icon: '🔲' },
  { key: 'motherboardId' as const, type: 'motherboard' as const,  label: '主板',         icon: '📟' },
  { key: 'memoryId' as const,      type: 'memory' as const,       label: '内存',         icon: '🧮' },
  { key: 'gpuId' as const,         type: 'gpu' as const,          label: '显卡',         icon: '🎮' },
  { key: 'psuId' as const,         type: 'psu' as const,          label: '电源供应器',    icon: '⚡' },
]

function onSubmit() {
  store.submit()
}

function onReset() {
  store.resetForm()
}
</script>

<template>
  <div class="build-page">
    <!-- 页头（简洁） -->
    <header class="page-header">
      <h1 class="page-title">装机配置评估</h1>
      <p class="page-desc">
        选择硬件型号，引擎将依次校验
        <span class="text-accent">物理兼容性</span>、
        <span class="text-accent">供电安全</span> 与
        <span class="text-accent">性能均衡度</span>
      </p>
    </header>

    <!-- 表单区域 -->
    <section class="form-section">
      <div class="form-section__header">
        <h2 class="section-label">硬件配置</h2>
        <span class="section-label__line"></span>
      </div>

      <div class="form-grid">
        <HardwareSelector
          v-for="hw in hardwareFields"
          :key="hw.key"
          :model-value="store.form[hw.key]"
          :label="hw.label"
          :icon="hw.icon"
          :hw-type="hw.type"
          @update:model-value="(v) => store.updateField(hw.key, v)"
        />
      </div>

      <div class="form-actions">
        <div class="form-actions__left">
          <span v-if="store.filledCount > 0" class="form-actions__count">
            <span class="count-dot"></span>
            已选 {{ store.filledCount }} / {{ hardwareFields.length }} 件
          </span>
        </div>
        <div class="form-actions__right">
          <button class="btn btn--ghost" :disabled="store.loading" @click="onReset">
            清空
          </button>
          <button class="btn btn--primary" :disabled="!store.canSubmit" @click="onSubmit">
            <span v-if="store.loading" class="btn__spinner"></span>
            <template v-else>
              <span class="btn__icon">⚡</span>
              开始评估
            </template>
          </button>
        </div>
      </div>
    </section>

    <!-- 错误提示 -->
    <Transition name="error-in">
      <div v-if="store.error" class="error-bar">
        <span class="error-bar__icon">⚠</span>
        <span class="error-bar__text">{{ store.error }}</span>
      </div>
    </Transition>

    <!-- 空状态 -->
    <section v-if="!store.loading && !store.hasEvaluated" class="empty-state">
      <div class="empty-state__inner">
        <div class="empty-state__art">
          <div class="topo-node topo-node--large">
            <span>⬡</span>
          </div>
          <svg class="topo-lines" viewBox="0 0 280 120" fill="none">
            <line x1="140" y1="30" x2="60" y2="90" stroke="currentColor" stroke-opacity="0.15" stroke-width="1" stroke-dasharray="4 4" />
            <line x1="140" y1="30" x2="220" y2="90" stroke="currentColor" stroke-opacity="0.15" stroke-width="1" stroke-dasharray="4 4" />
            <line x1="60" y1="90" x2="220" y2="90" stroke="currentColor" stroke-opacity="0.10" stroke-width="1" stroke-dasharray="4 4" />
            <circle cx="140" cy="30" r="3" fill="currentColor" fill-opacity="0.3" />
            <circle cx="60" cy="90" r="3" fill="currentColor" fill-opacity="0.2" />
            <circle cx="220" cy="90" r="3" fill="currentColor" fill-opacity="0.2" />
            <circle cx="140" cy="105" r="3" fill="currentColor" fill-opacity="0.15" />
            <line x1="140" y1="90" x2="140" y2="105" stroke="currentColor" stroke-opacity="0.15" stroke-width="1" />
            <line x1="60" y1="90" x2="140" y2="105" stroke="currentColor" stroke-opacity="0.12" stroke-width="1" />
            <line x1="220" y1="90" x2="140" y2="105" stroke="currentColor" stroke-opacity="0.12" stroke-width="1" />
          </svg>
          <div class="topo-row">
            <div class="topo-node"><span>▣</span></div>
            <div class="topo-node"><span>▣</span></div>
            <div class="topo-node"><span>▣</span></div>
          </div>
        </div>

        <p class="empty-state__text">
          在上方下拉列表中选择配件<br />
          引擎将自动执行<span class="text-accent">三重诊断规则</span>并生成评估报告
        </p>

        <div class="empty-state__tips">
          <span>物理兼容校验</span>
          <span class="empty-state__sep">→</span>
          <span>供电安全评估</span>
          <span class="empty-state__sep">→</span>
          <span>性能瓶颈分析</span>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.build-page {
  max-width: 760px;
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-lg) var(--space-3xl);
  animation: page-in 0.5s var(--ease-out-expo);
}

@keyframes page-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* ── 页头 ── */
.page-header {
  text-align: center;
  margin-bottom: var(--space-xl);
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
  max-width: 480px;
  margin: 0 auto;
  line-height: 1.6;
}

.text-accent {
  color: var(--accent);
  font-weight: 500;
}

/* ── Section Label ── */
.form-section {
  margin-bottom: var(--space-xl);
}

.form-section__header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-md);
}

.section-label {
  font-size: 0.72rem;
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.12em;
  flex-shrink: 0;
}

.section-label__line {
  flex: 1;
  height: 1px;
  background: var(--border-subtle);
}

/* ── Form Grid ── */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-sm);
  margin-bottom: var(--space-md);
}

@media (max-width: 520px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

/* ── Actions ── */
.form-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-md);
}

.form-actions__left { flex: 1; }

.form-actions__right {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.form-actions__count {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.76rem;
  color: var(--text-secondary);
  font-family: var(--font-mono);
}

.count-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--accent);
}

/* ── Buttons ── */
.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 10px 24px;
  border: none;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s var(--ease-out-expo);
  letter-spacing: -0.01em;
}

.btn:disabled { opacity: 0.35; cursor: not-allowed; }

.btn--primary {
  background: var(--accent);
  color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 102, 255, 0.25);
}

.btn--primary:not(:disabled):hover {
  box-shadow: 0 4px 16px rgba(0, 102, 255, 0.35);
  transform: translateY(-1px);
}

.btn--primary:not(:disabled):active {
  transform: translateY(0);
  box-shadow: 0 1px 4px rgba(0, 102, 255, 0.2);
}

.btn__icon { font-size: 0.9rem; }

.btn--ghost {
  background: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-subtle);
}

.btn--ghost:not(:disabled):hover {
  color: var(--text-primary);
  border-color: var(--border-default);
  background: var(--bg-hover);
}

.btn__spinner {
  width: 16px;
  height: 16px;
  border: 2px solid transparent;
  border-top-color: currentColor;
  border-radius: 50%;
  animation: spin 0.7s linear infinite;
}

/* ── Error Bar ── */
.error-bar {
  display: flex;
  align-items: flex-start;
  gap: var(--space-sm);
  padding: var(--space-md) var(--space-lg);
  margin-bottom: var(--space-lg);
  background: var(--status-failed-bg);
  border: 1px solid rgba(224, 49, 49, 0.25);
  border-radius: var(--radius-md);
  color: var(--status-failed);
  font-size: 0.84rem;
  font-weight: 500;
}

.error-bar__icon { font-size: 1rem; flex-shrink: 0; margin-top: 1px; }

.error-in-enter-active { transition: all 0.35s var(--ease-out-expo); }
.error-in-leave-active { transition: all 0.25s ease-in; }
.error-in-enter-from { opacity: 0; transform: translateY(-8px); }
.error-in-leave-to   { opacity: 0; transform: translateY(-4px); }

/* ── Empty State ── */
.empty-state {
  padding: var(--space-2xl) var(--space-lg);
}

.empty-state__inner {
  text-align: center;
  max-width: 360px;
  margin: 0 auto;
}

.empty-state__art {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--space-xl);
}

.topo-node {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  font-size: 0.85rem;
  color: var(--text-muted);
  transition: all 0.4s var(--ease-out-expo);
}

.topo-node--large {
  width: 52px;
  height: 52px;
  font-size: 1.2rem;
  border-color: var(--border-default);
  color: var(--accent);
  background: var(--bg-field);
}

.topo-lines {
  width: 280px;
  height: 120px;
  color: var(--text-muted);
}

.topo-row {
  display: flex;
  gap: var(--space-md);
  margin-top: -12px;
}

.empty-state__text {
  font-size: 0.88rem;
  color: var(--text-secondary);
  line-height: 1.7;
  margin-bottom: var(--space-lg);
}

.empty-state__tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--space-sm);
  font-family: var(--font-mono);
  font-size: 0.65rem;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.empty-state__sep {
  color: var(--border-default);
}
</style>
