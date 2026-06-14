import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/hardware',
      name: 'hardware',
      component: () => import('@/views/HardwareLibraryView.vue'),
    },
    {
      path: '/build',
      name: 'build',
      component: () => import('@/views/BuildView.vue'),
    },
    {
      path: '/result',
      name: 'result',
      component: () => import('@/views/ResultView.vue'),
    },
    // 旧路由重定向
    {
      path: '/evaluate',
      redirect: '/build',
    },
  ],
})

export default router
