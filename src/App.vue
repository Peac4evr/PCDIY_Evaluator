<script setup lang="ts">
import { RouterView } from 'vue-router'
import AppNavbar from '@/components/AppNavbar.vue'
</script>

<template>
  <!-- 清爽背景层 -->
  <div class="bg-atmosphere" aria-hidden="true">
    <div class="bg-grid"></div>
    <div class="bg-glow bg-glow--top"></div>
  </div>

  <!-- 顶栏导航 -->
  <AppNavbar />

  <!-- 主内容 -->
  <div class="app-content">
    <RouterView v-slot="{ Component }">
      <Transition name="page" mode="out-in">
        <component :is="Component" />
      </Transition>
    </RouterView>
  </div>
</template>

<style>
/* ═══════════════════════════════════════════════════════════════
   全局背景氛围层 — 清爽现代白
   ═══════════════════════════════════════════════════════════════ */

.bg-atmosphere {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
  overflow: hidden;
}

/* ── 极淡网格点阵 ── */
.bg-grid {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(circle at 1px 1px, rgba(0, 0, 0, 0.04) 1px, transparent 0);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse 60% 60% at 50% 40%, black 20%, transparent 70%);
  -webkit-mask-image: radial-gradient(ellipse 60% 60% at 50% 40%, black 20%, transparent 70%);
}

/* ── 淡蓝色顶部光晕 ── */
.bg-glow {
  position: absolute;
  width: 700px;
  height: 500px;
  border-radius: 50%;
  filter: blur(150px);
  opacity: 0.06;
}

.bg-glow--top {
  top: -200px;
  right: -100px;
  background: radial-gradient(circle, var(--accent), transparent 70%);
}

/* ── 主内容层 ── */
.app-content {
  position: relative;
  z-index: 1;
  padding-top: 64px; /* 顶栏高度 */
  min-height: 100vh;
}

/* ── 页面过渡 ── */
.page-enter-active {
  transition: opacity 0.3s var(--ease-out-expo), transform 0.3s var(--ease-out-expo);
}

.page-leave-active {
  transition: opacity 0.2s ease-in, transform 0.2s ease-in;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(12px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-8px);
}
</style>
