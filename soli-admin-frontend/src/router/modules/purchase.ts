import type { RouteRecordRaw } from 'vue-router';

const Layout = () => import('@/layout/index.vue');

const purchaseRoute: RouteRecordRaw = {
  path: '/purchase',
  component: Layout,
  name: 'Purchase',
  meta: { title: '进货管理', icon: 'ShoppingCart' },
  children: [
    {
      path: 'bill-template',
      name: 'BillTemplate',
      component: () => import('@/views/purchase/bill-template/BillTemplateIndex.vue'),
      meta: { title: '单据模板', icon: 'Document' }
    },
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
  ]
};

export default purchaseRoute;
