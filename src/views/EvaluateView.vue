<script setup lang="ts">
import { onMounted } from 'vue'
import { useEvaluatorStore } from '@/stores/evaluator'
import HardwareSelector from '@/components/HardwareSelector.vue'
import DiagnosisCard from '@/components/DiagnosisCard.vue'
import ResultBanner from '@/components/ResultBanner.vue'

const store = useEvaluatorStore()

// 页面加载时拉取硬件列表
onMounted(() => {
  store.fetchAllHardware()
})

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
  <div class="evaluate-page">
    <!-- ═══════════════════ 页面头部 ═══════════════════ -->
    <header class="page-header">
      <div class="page-header__inner">
        <!-- 品牌区 -->
        <div class="page-header__brand">
          <div class="page-header__logo-wrap">
            <span class="page-header__logo">◈</span>
            <span class="page-header__logo-ring"></span>
          </div>
          <div>
            <h1 class="page-header__title">PC DIY Evaluator</h1>
            <p class="page-header__subtitle">智能装机配置评估引擎</p>
          </div>
        </div>

        <!-- 描述 -->
        <p class="page-header__desc">
          选取硬件型号 ，引擎将依次校验<span class="text-accent">物理兼容性</span>、
          <span class="text-accent">供电安全</span>与<span class="text-accent">性能均衡度</span>
        </p>

        <!-- 流程指示器 -->
        <div class="flow-indicator" aria-hidden="true">
          <div class="flow-step">
            <span class="flow-step__dot flow-step__dot--active"></span>
            <span class="flow-step__label">物理兼容</span>
          </div>
          <span class="flow-step__connector"></span>
          <div class="flow-step">
            <span class="flow-step__dot"></span>
            <span class="flow-step__label">供电评估</span>
          </div>
          <span class="flow-step__connector"></span>
          <div class="flow-step">
            <span class="flow-step__dot"></span>
            <span class="flow-step__label">性能瓶颈</span>
          </div>
        </div>
      </div>

      <!-- 装饰性对角线 -->
      <div class="page-header__decor" aria-hidden="true">
        <svg viewBox="0 0 200 120" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M0 120 L120 0 L200 0 L80 120 Z" fill="currentColor" opacity="0.03" />
        </svg>
      </div>
    </header>

    <!-- ═══════════════════ 表单区域 ═══════════════════ -->
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
          <button class="btn btn--ghost" @click="onReset" :disabled="store.loading">
            清空
          </button>
          <button
            class="btn btn--primary"
            :disabled="!store.canSubmit"
            @click="onSubmit"
          >
            <span v-if="store.loading" class="btn__spinner"></span>
            <template v-else>
              <span class="btn__icon">⚡</span>
              开始评估
            </template>
          </button>
        </div>
      </div>
    </section>

    <!-- ═══════════════════ 错误提示 ═══════════════════ -->
    <Transition name="error-in">
      <div v-if="store.error" class="error-bar">
        <span class="error-bar__icon">⚠</span>
        <span class="error-bar__text">{{ store.error }}</span>
      </div>
    </Transition>

    <!-- ═══════════════════ 结果区域 ═══════════════════ -->
    <section v-if="store.report" class="results-section">
      <ResultBanner
        :passed="store.report.passed"
        :total-power="store.report.totalHardwareCostW"
        :details-count="{
          passed: store.passedCount,
          warn: store.warnItems.length,
          failed: store.failedItems.length,
        }"
      />

      <div class="results-section__header">
        <h2 class="section-label">诊断详情</h2>
        <span class="section-label__line"></span>
      </div>

      <div class="diagnosis-list">
        <DiagnosisCard
          v-for="(item, idx) in store.report.details"
          :key="item.ruleName"
          :item="item"
          :index="idx"
        />
      </div>
    </section>

    <!-- ═══════════════════ 空状态 ═══════════════════ -->
    <section v-else-if="!store.loading" class="empty-state">
      <div class="empty-state__inner">
        <!-- 抽象硬件拓扑图 -->
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
            <div class="topo-node">
              <span>▣</span>
            </div>
            <div class="topo-node">
              <span>▣</span>
            </div>
            <div class="topo-node">
              <span>▣</span>
            </div>
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
.evaluate-page {
  max-width: 760px;
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-lg) var(--space-3xl);
  animation: page-in 0.5s var(--ease-out-expo);
}

@keyframes page-in {
  from { opacity: 0; }
  to { opacity: 1; }
}

/* ═══════════════════════════════════════════════════════════
   Page Header
   ═══════════════════════════════════════════════════════════ */

.page-header {
  position: relative;
  margin-bottom: var(--space-2xl);
  padding: var(--space-xl);
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-card);
  overflow: hidden;
}

.page-header__inner {
  position: relative;
  z-index: 1;
}

.page-header__brand {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-lg);
}

.page-header__logo-wrap {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
}

.page-header__logo {
  font-size: 1.8rem;
  color: var(--accent);
  position: relative;
  z-index: 1;
}

.page-header__logo-ring {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 1px solid var(--border-glow);
  animation: glow-pulse 4s ease-in-out infinite;
}

.page-header__title {
  font-size: 1.4rem;
  font-weight: 700;
  letter-spacing: -0.03em;
  line-height: 1.1;
}

.page-header__subtitle {
  font-size: 0.78rem;
  color: var(--text-secondary);
  letter-spacing: 0.06em;
  margin-top: 2px;
}

.page-header__desc {
  font-size: 0.85rem;
  color: var(--text-muted);
  max-width: 480px;
  line-height: 1.6;
}

.text-accent {
  color: var(--accent);
  font-weight: 500;
}

/* ── 流程指示器 ── */
.flow-indicator {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  margin-top: var(--space-lg);
  padding-top: var(--space-md);
  border-top: 1px solid var(--border-subtle);
}

.flow-step {
  display: flex;
  align-items: center;
  gap: 6px;
}

.flow-step__dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--border-default);
  transition: background 0.3s, box-shadow 0.3s;
}

.flow-step__dot--active {
  background: var(--accent);
  box-shadow: 0 0 8px var(--accent-glow);
}

.flow-step__label {
  font-size: 0.7rem;
  font-weight: 500;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.flow-step__connector {
  width: 24px;
  height: 1px;
  background: var(--border-subtle);
}

/* ── 装饰元素 ── */
.page-header__decor {
  position: absolute;
  top: 0;
  right: 0;
  width: 200px;
  height: 120px;
  color: var(--accent);
  opacity: 0.08;
}

/* ═══════════════════════════════════════════════════════════
   Section Label
   ═══════════════════════════════════════════════════════════ */

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

/* ═══════════════════════════════════════════════════════════
   Form Section
   ═══════════════════════════════════════════════════════════ */

.form-section {
  margin-bottom: var(--space-xl);
}

.form-section__header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  margin-bottom: var(--space-md);
}

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

/* ═══════════════════════════════════════════════════════════
   Buttons
   ═══════════════════════════════════════════════════════════ */

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

/* ═══════════════════════════════════════════════════════════
   Error Bar
   ═══════════════════════════════════════════════════════════ */

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

/* ── Error transition ── */
.error-in-enter-active { transition: all 0.35s var(--ease-out-expo); }
.error-in-leave-active { transition: all 0.25s ease-in; }
.error-in-enter-from { opacity: 0; transform: translateY(-8px); }
.error-in-leave-to   { opacity: 0; transform: translateY(-4px); }

/* ═══════════════════════════════════════════════════════════
   Results Section
   ═══════════════════════════════════════════════════════════ */

.results-section {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
  animation: results-in 0.5s var(--ease-out-expo);
}

@keyframes results-in {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}

.results-section__header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
}

.diagnosis-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

/* ═══════════════════════════════════════════════════════════
   Empty State
   ═══════════════════════════════════════════════════════════ */

.empty-state {
  padding: var(--space-2xl) var(--space-lg);
}

.empty-state__inner {
  text-align: center;
  max-width: 360px;
  margin: 0 auto;
}

/* ── 拓扑图 ── */
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

/* ── 文本 ── */
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
