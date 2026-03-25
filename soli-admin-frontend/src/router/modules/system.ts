import type { RouteRecordRaw } from 'vue-router';

export const systemExtraRoutes: RouteRecordRaw[] = [
  {
    component: () => import('@/views/system/dict-data/index.vue'),
    meta: { hidden: true, title: '字典数据' },
    name: 'DictData',
    path: 'dict/:dictId/data'
  }
];
