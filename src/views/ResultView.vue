<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useEvaluatorStore } from '@/stores/evaluator'
import ResultBanner from '@/components/ResultBanner.vue'
import DiagnosisCard from '@/components/DiagnosisCard.vue'

const store = useEvaluatorStore()
const router = useRouter()

/* ── 守卫：无报告时重定向到评估页 ── */
onMounted(() => {
  if (!store.report) {
    router.replace({ name: 'build' })
  }
})

function onBackToBuild() {
  store.resetForm()
  router.push({ name: 'build' })
}
</script>

<template>
  <div v-if="store.report" class="result-page">
    <!-- 页头 -->
    <header class="page-header">
      <h1 class="page-title">评估报告</h1>
      <p class="page-desc">引擎已完成三重诊断，以下是详细评估结果</p>
    </header>

    <!-- 结果横幅 -->
    <ResultBanner
      :passed="store.report.passed"
      :total-power="store.report.totalHardwareCostW"
      :details-count="{
        passed: store.passedCount,
        warn: store.warnItems.length,
        failed: store.failedItems.length,
      }"
    />

    <!-- 诊断详情 -->
    <section class="details-section">
      <div class="details-section__header">
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

    <!-- 底部操作 -->
    <div class="result-actions">
      <button class="btn-back" @click="onBackToBuild">
        <span>←</span>
        返回重新评估
      </button>
    </div>
  </div>
</template>

<style scoped>
.result-page {
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
}

/* ── 详情区域 ── */
.details-section {
  margin-top: var(--space-xl);
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.details-section__header {
  display: flex;
  align-items: center;
  gap: var(--space-md);
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

.diagnosis-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
}

/* ── 底部按钮 ── */
.result-actions {
  text-align: center;
  margin-top: var(--space-2xl);
}

.btn-back {
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
  padding: 11px 28px;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-default);
  background: var(--bg-elevated);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.2s var(--ease-out-expo);
}

.btn-back:hover {
  background: var(--bg-hover);
  border-color: var(--border-strong);
  transform: translateY(-1px);
}
</style>
