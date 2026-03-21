import { defineStore } from 'pinia';
import router, { dashboardRoute } from '@/router';
import { getMenuTree } from '@/api/menu';
import systemRoute, { systemExtraRoutes } from '@/router/modules/system';
import { useUserStore } from '@/store/modules/user';
import type { RouteRecordRaw } from 'vue-router';
import type { SysMenuDTO } from '@/types/global';
import { ref } from 'vue';

const Layout = () => import('@/layout/index.vue');

const staticMenuRoutes: RouteRecordRaw[] = [dashboardRoute];

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([...staticMenuRoutes]);
  const addRoutes = ref<RouteRecordRaw[]>([]);
  const isRoutesLoaded = ref(false);

  const buildRoutesFromMenus = (menus: SysMenuDTO[]): RouteRecordRaw[] => {
    const records: Record<number, RouteRecordRaw> = {};
    const roots: RouteRecordRaw[] = [];

    const mapNode = (node: SysMenuDTO): RouteRecordRaw => {
      const record: RouteRecordRaw = {
        path: node.type === '0'
          ? (node.path?.startsWith('/') ? node.path : `/${node.path || ''}`)
          : (node.path || ''),
        name: node.name,
        component: node.type === '0' ? Layout : undefined,
        meta: { title: node.name, icon: node.icon },
        children: []
      };
      if (node.type === '1' && node.component) {
        record.component = () => import(`@/views/${node.component}.vue`);
      }
      return record;
    };

    const traverse = (nodes: SysMenuDTO[]) => {
      nodes.forEach(n => {
        if (n.type === '2') return;
        const rec = mapNode(n);
        records[n.id] = rec;
        if (!n.parentId || n.parentId === 0) {
          roots.push(rec);
        } else {
          const parent = records[n.parentId];
          if (parent) {
            (parent.children as RouteRecordRaw[]).push(rec);
          } else {
            roots.push(rec);
          }
        }
        if (n.children && n.children.length) traverse(n.children);
      });
    };

    traverse(menus);
    return roots;
  };

  const loadRoutes = async () => {
    if (isRoutesLoaded.value) {
      return addRoutes.value;
    }

    const userStore = useUserStore();
    let dynamicRoutes: RouteRecordRaw[] = [];

    if (userStore.isSuperAdmin) {
      dynamicRoutes = [systemRoute];
    } else {
      const res = await getMenuTree();
      const menus = (res.data || []) as SysMenuDTO[];
      dynamicRoutes = buildRoutesFromMenus(menus);
    }

    const systemMenuRoute = dynamicRoutes.find(route => route.path === '/system' || route.name === 'System');
    if (systemMenuRoute) {
      const children = (systemMenuRoute.children ||= []);
      systemExtraRoutes.forEach(route => {
        if (!children.some(child => child.path === route.path)) {
          children.push(route);
        }
      });
    }

    dynamicRoutes.forEach(route => router.addRoute(route));
    addRoutes.value = dynamicRoutes;
    routes.value = [...staticMenuRoutes, ...dynamicRoutes];
    isRoutesLoaded.value = true;
    return dynamicRoutes;
  };

  const resetRoutes = () => {
    addRoutes.value.forEach(route => {
      if (route.name && router.hasRoute(route.name)) {
        router.removeRoute(route.name);
      }
    });
    addRoutes.value = [];
    routes.value = [...staticMenuRoutes];
    isRoutesLoaded.value = false;
  };

  return {
    routes,
    addRoutes,
    isRoutesLoaded,
    loadRoutes,
    resetRoutes,
    buildRoutesFromMenus
  };
});
