<script setup lang="ts">
defineProps<{
  icon: string
  title: string
  description: string
}>()
</script>

<template>
  <div class="feature-card">
    <span class="feature-icon" aria-hidden="true">{{ icon }}</span>
    <h3 class="feature-title">{{ title }}</h3>
    <p class="feature-desc">{{ description }}</p>
  </div>
</template>

<style scoped>
.feature-card {
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  padding: var(--space-xl);
  text-align: center;
  transition: transform 0.25s var(--ease-out-expo), box-shadow 0.25s var(--ease-out-expo);
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-elevated);
}

.feature-icon {
  font-size: 32px;
  display: block;
  margin-bottom: var(--space-md);
  line-height: 1;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-sm);
}

.feature-desc {
  font-size: 13px;
  color: var(--text-muted);
  line-height: 1.6;
}
</style>
