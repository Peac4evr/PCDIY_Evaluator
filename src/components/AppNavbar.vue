<script setup lang="ts">
</script>

<template>
  <nav class="navbar" role="navigation" aria-label="主导航">
    <div class="navbar-inner">
      <!-- 品牌 -->
      <router-link to="/" class="navbar-brand" aria-label="返回首页">
        <span class="navbar-logo" aria-hidden="true">◈</span>
        <span class="navbar-title">PC DIY</span>
        <span class="navbar-subtitle">Evaluator</span>
      </router-link>

      <!-- 分隔 -->
      <span class="navbar-sep" aria-hidden="true"></span>

      <!-- 导航链接 -->
      <div class="navbar-links">
        <router-link to="/" class="nav-link" exact-active-class="active">首页</router-link>
        <router-link to="/hardware" class="nav-link" active-class="active">硬件库</router-link>
        <router-link to="/build" class="nav-link" active-class="active">装机评估</router-link>
      </div>
    </div>
  </nav>
</template>

<style scoped>
/* ═══════════════════════════════════════════════════════════════
   顶栏导航 — 64px 固定定位，居中布局
   ═══════════════════════════════════════════════════════════════ */

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 50;
  height: 64px;
  background: rgba(255, 255, 255, 0.82);
  border-bottom: 1px solid var(--border-subtle);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03);
  backdrop-filter: blur(16px) saturate(180%);
  -webkit-backdrop-filter: blur(16px) saturate(180%);
}

.navbar-inner {
  max-width: 1000px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 var(--space-xl);
  gap: var(--space-lg);
}

/* ── 品牌 ── */
.navbar-brand {
  display: flex;
  align-items: baseline;
  gap: 3px;
  text-decoration: none;
  flex-shrink: 0;
}

.navbar-logo {
  font-size: 26px;
  color: var(--accent);
  animation: glow-pulse 3s ease-in-out infinite;
  line-height: 1;
  margin-right: 2px;
}

.navbar-title {
  font-family: var(--font-mono);
  font-size: 17px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.03em;
}

.navbar-subtitle {
  font-family: var(--font-mono);
  font-size: 17px;
  font-weight: 400;
  color: var(--accent);
  letter-spacing: -0.03em;
}

/* ── 分隔线 ── */
.navbar-sep {
  width: 1px;
  height: 22px;
  background: var(--border-subtle);
  flex-shrink: 0;
}

/* ── 导航链接 ── */
.navbar-links {
  display: flex;
  align-items: center;
  gap: 2px;
}

.nav-link {
  padding: 8px 18px;
  border-radius: var(--radius-sm);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  letter-spacing: 0.01em;
  transition: color 0.2s var(--ease-out-expo), background 0.2s var(--ease-out-expo);
  position: relative;
}

.nav-link:hover {
  color: var(--text-primary);
  background: var(--bg-hover);
}

.nav-link.active {
  color: var(--accent);
  background: var(--accent-dim);
  font-weight: 600;
}

/* ── 响应式 ── */
@media (max-width: 640px) {
  .navbar {
    height: 56px;
  }

  .navbar-subtitle {
    display: none;
  }

  .navbar-sep {
    display: none;
  }

  .navbar-inner {
    gap: var(--space-sm);
    padding: 0 var(--space-md);
  }

  .navbar-title {
    font-size: 15px;
  }

  .navbar-logo {
    font-size: 22px;
  }

  .nav-link {
    font-size: 13px;
    padding: 6px 10px;
  }
}
</style>
