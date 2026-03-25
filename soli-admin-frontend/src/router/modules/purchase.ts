import type { RouteRecordRaw } from 'vue-router';

export const purchaseExtraRoutes: RouteRecordRaw[] = [
  {
    path: 'bill-template/create',
    name: 'BillTemplateCreate',
    component: () => import('@/views/purchase/bill-template/BillTemplateCreate.vue'),
    meta: { title: '新建单据', hidden: true, activeMenu: '/purchase/bill-template' }
  },
  {
    path: 'bill-template/detail',
    name: 'BillTemplateDetail',
    component: () => import('@/views/purchase/bill-template/BillTemplateDetail.vue'),
    meta: { title: '单据详情', hidden: true, activeMenu: '/purchase/bill-template' }
  }
];
