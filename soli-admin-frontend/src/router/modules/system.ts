import type { RouteRecordRaw } from 'vue-router';

const Layout = () => import('@/layout/index.vue');

export const systemExtraRoutes: RouteRecordRaw[] = [
  {
    component: () => import('@/views/system/dict-data/index.vue'),
    meta: { hidden: true, title: '字典数据' },
    name: 'DictData',
    path: 'dict/:dictId/data'
  }
];

const systemRoute: RouteRecordRaw = {
  children: [
    {
      component: () => import('@/views/system/user/index.vue'),
      meta: { icon: 'User', title: '用户管理' },
      name: 'User',
      path: 'user'
    },
    {
      component: () => import('@/views/system/role/index.vue'),
      meta: { icon: 'Avatar', title: '角色管理' },
      name: 'Role',
      path: 'role'
    },
    {
      component: () => import('@/views/system/menu/index.vue'),
      meta: { icon: 'Menu', title: '菜单管理' },
      name: 'Menu',
      path: 'menu'
    },
    {
      component: () => import('@/views/system/dict/index.vue'),
      meta: { icon: 'Reading', title: '字典管理' },
      name: 'Dict',
      path: 'dict'
    },
    {
      component: () => import('@/views/system/config/index.vue'),
      meta: { icon: 'Tools', title: '参数设置' },
      name: 'Config',
      path: 'config'
    },
    {
      children: [
        {
          component: () => import('@/views/system/monitor/operlog/index.vue'),
          meta: { icon: 'Document', title: '操作日志' },
          name: 'Operlog',
          path: 'operlog'
        },
        {
          component: () => import('@/views/system/monitor/logininfor/index.vue'),
          meta: { icon: 'Key', title: '登录日志' },
          name: 'Logininfor',
          path: 'logininfor'
        }
      ],
      meta: { icon: 'Monitor', title: '日志管理' },
      name: 'Log',
      path: 'log'
    }
  ],
  component: Layout,
  meta: { icon: 'Tools', title: '系统管理' },
  name: 'System',
  path: '/system',
  redirect: '/system/user'
};

export default systemRoute;
