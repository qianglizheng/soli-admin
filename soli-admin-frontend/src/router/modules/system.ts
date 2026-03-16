
import Layout from '@/layout/index.vue';

export default {
  path: '/system',
  component: Layout,
  redirect: '/system/user',
  name: 'System',
  meta: { title: '系统管理', icon: 'Tools' },
  children: [
    {
      path: 'user',
      name: 'User',
      component: () => import('@/views/system/user/index.vue'),
      meta: { title: '用户管理', icon: 'User' }
    },
    {
      path: 'role',
      name: 'Role',
      component: () => import('@/views/system/role/index.vue'),
      meta: { title: '角色管理', icon: 'Avatar' }
    },
    {
      path: 'menu',
      name: 'Menu',
      component: () => import('@/views/system/menu/index.vue'),
      meta: { title: '菜单管理', icon: 'Menu' }
    },
    {
      path: 'dept',
      name: 'Dept',
      component: () => import('@/views/system/dept/index.vue'),
      meta: { title: '部门管理', icon: 'Connection' }
    },
    {
      path: 'post',
      name: 'Post',
      component: () => import('@/views/system/post/index.vue'),
      meta: { title: '岗位管理', icon: 'Position' }
    },
    {
      path: 'dict',
      name: 'Dict',
      component: () => import('@/views/system/dict/index.vue'),
      meta: { title: '字典管理', icon: 'Reading' }
    },
    {
      path: 'config',
      name: 'Config',
      component: () => import('@/views/system/config/index.vue'),
      meta: { title: '选项设置', icon: 'Tools' }
    },
    {
      path: 'log',
      name: 'Log',
      meta: { title: '日志管理', icon: 'Monitor' },
      children: [
        {
          path: 'operlog',
          name: 'Operlog',
          component: () => import('@/views/system/monitor/operlog/index.vue'),
          meta: { title: '操作日志', icon: 'Document' }
        },
        {
          path: 'logininfor',
          name: 'Logininfor',
          component: () => import('@/views/system/monitor/logininfor/index.vue'),
          meta: { title: '登录日志', icon: 'Key' }
        }
      ]
    }
  ]
};
