
import { type RouteRecordRaw, createRouter, createWebHistory } from 'vue-router';
import Layout from '@/layout/index.vue';
import systemRouter from './modules/system';

export const constantRoutes: RouteRecordRaw[] = [
  {
    component: () => import('@/views/login/index.vue'),
    meta: { hidden: true },
    path: '/login'
  },
  {
    children: [
      {
        component: () => import('@/views/dashboard/index.vue'),
        meta: { affix: true, icon: 'Odometer', title: '仪表盘' },
        name: 'Dashboard',
        path: 'dashboard'
      }
    ],
    component: Layout,
    path: '/',
    redirect: '/dashboard'
  },
  systemRouter
];

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: constantRoutes
});

export default router;
