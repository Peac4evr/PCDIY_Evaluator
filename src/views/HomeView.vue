<script setup lang="ts">
import { useRouter } from 'vue-router'
import FeatureCard from '@/components/FeatureCard.vue'

const router = useRouter()

const features = [
  {
    icon: '🔌',
    title: '物理兼容性校验',
    description: '验证 CPU 插槽与主板匹配、内存代数一致性及 BIOS 版本兼容性，杜绝硬件物理冲突。',
  },
  {
    icon: '⚡',
    title: '供电安全评估',
    description: '核算整机峰值功耗与电源额定功率的安全冗余，防止供电不足导致宕机或硬件损坏。',
  },
  {
    icon: '📊',
    title: '性能瓶颈分析',
    description: '对比 CPU 与 GPU 的算力配比，识别高 U 低显或小马拉大车等性能短板。',
  },
]
</script>

<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="hero">
      <div class="hero-badge">PC DIY 智能评估引擎</div>
      <h1 class="hero-title">
        <span class="hero-logo" aria-hidden="true">◈</span>
        PC DIY Evaluator
      </h1>
      <p class="hero-subtitle">
        选取硬件型号，引擎将自动校验物理兼容性、供电安全与性能均衡度，
        为你的装机方案提供专业级诊断报告。
      </p>
      <div class="hero-actions">
        <button class="btn-primary" @click="router.push('/build')">开始装机评估</button>
        <button class="btn-secondary" @click="router.push('/hardware')">浏览硬件库</button>
      </div>
    </section>

    <!-- Features -->
    <section class="features-section">
      <h2 class="section-title">三大核心评估维度</h2>
      <div class="features-grid">
        <FeatureCard
          v-for="f in features"
          :key="f.title"
          :icon="f.icon"
          :title="f.title"
          :description="f.description"
        />
      </div>
    </section>

  </div>
</template>

<style scoped>
.home-page {
  max-width: 760px;
  margin: 0 auto;
  padding: var(--space-2xl) var(--space-lg) var(--space-3xl);
}

/* ── Hero ── */
.hero {
  text-align: center;
  padding: var(--space-2xl) 0;
  animation: fade-up 0.6s var(--ease-out-expo);
}

.hero-badge {
  display: inline-block;
  padding: 4px 14px;
  border-radius: var(--radius-full);
  background: var(--accent-dim);
  color: var(--accent);
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.04em;
  margin-bottom: var(--space-lg);
}

.hero-logo {
  font-size: 44px;
  color: var(--accent);
  animation: glow-pulse 3s ease-in-out infinite;
  vertical-align: middle;
  margin-right: 6px;
}

.hero-title {
  font-size: 34px;
  font-weight: 800;
  color: var(--text-primary);
  letter-spacing: -0.03em;
  margin-bottom: var(--space-md);
}

.hero-subtitle {
  font-size: 15px;
  color: var(--text-secondary);
  max-width: 480px;
  margin: 0 auto var(--space-xl);
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-md);
  flex-wrap: wrap;
}

/* ── Buttons ── */
.btn-primary {
  display: inline-flex;
  align-items: center;
  padding: 11px 28px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  background: var(--accent);
  border: none;
  cursor: pointer;
  transition: background 0.2s var(--ease-out-expo), box-shadow 0.2s var(--ease-out-expo);
}

.btn-primary:hover {
  background: var(--accent-strong);
  box-shadow: 0 4px 16px var(--accent-glow);
}

.btn-lg {
  padding: 14px 36px;
  font-size: 16px;
}

.btn-secondary {
  display: inline-flex;
  align-items: center;
  padding: 10px 27px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  background: var(--bg-elevated);
  border: 1px solid var(--border-default);
  cursor: pointer;
  transition: background 0.2s var(--ease-out-expo), border-color 0.2s var(--ease-out-expo);
}

.btn-secondary:hover {
  background: var(--bg-hover);
  border-color: var(--border-strong);
}

/* ── Section ── */
.features-section {
  margin-top: var(--space-3xl);
  animation: fade-up 0.6s var(--ease-out-expo) 0.15s both;
}

.section-title {
  text-align: center;
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: var(--space-xl);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--space-lg);
}

@media (max-width: 640px) {
  .features-grid {
    grid-template-columns: 1fr;
  }

  .hero-title {
    font-size: 26px;
  }
}

</style>
