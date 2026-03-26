import { defineStore } from 'pinia';
import { ref } from 'vue';
import type { RouteRecordRaw } from 'vue-router';
import { getModuleNavTree, type ModuleTreeNode } from '@/api/moduleCenter';
import router, { dashboardRoute } from '@/router';

const Layout = () => import('@/layout/index.vue');

const viewModules = import.meta.glob('../../views/**/*.vue');

const staticRoutes: RouteRecordRaw[] = [dashboardRoute];

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([...staticRoutes]);
  const addRoutes = ref<RouteRecordRaw[]>([]);
  const isRoutesLoaded = ref(false);

  const normalizeRootPath = (path?: string) => {
    if (!path) {
      return '';
    }
    return path.startsWith('/') ? path : `/${path}`;
  };

  const joinPath = (parentPath: string, currentPath: string) => {
    if (!parentPath) {
      return normalizeRootPath(currentPath);
    }
    if (!currentPath) {
      return parentPath;
    }
    if (currentPath.startsWith('/')) {
      return currentPath;
    }
    return `${parentPath.replace(/\/$/, '')}/${currentPath.replace(/^\//, '')}`;
  };

  const resolveViewComponent = (componentPath?: string) => {
    if (!componentPath) {
      return undefined;
    }
    return viewModules[`../../views/${componentPath}.vue`];
  };

  const buildRouteFromModule = (
    node: ModuleTreeNode,
    parentFullPath = ''
  ): { fullPath: string; route: RouteRecordRaw } | undefined => {
    if (!node.routePath) {
      return undefined;
    }

    const fullPath = joinPath(parentFullPath, node.routePath);
    const routePath = parentFullPath ? node.routePath : fullPath;
    const route = {
      path: routePath,
      name: node.moduleCode,
      meta: {
        title: node.moduleName,
        icon: node.icon,
        hidden: node.navVisible === '0'
      }
    } as unknown as RouteRecordRaw;

    if (node.moduleType === 'CATALOG') {
      route.component = Layout;
    } else {
      const component = resolveViewComponent(node.componentPath);
      if (!component) {
        return undefined;
      }
      route.component = component;
    }

    const childRoutes = (node.children || [])
      .map((child) => buildRouteFromModule(child, fullPath))
      .filter((item): item is { fullPath: string; route: RouteRecordRaw } => Boolean(item));

    if (childRoutes.length) {
      route.children = childRoutes.map((item) => item.route);
      const redirectChildRoute =
        childRoutes.find((item) => !item.route.meta?.hidden) || childRoutes[0];
      if (node.moduleType === 'CATALOG' && redirectChildRoute) {
        route.redirect = redirectChildRoute.fullPath;
      }
    }

    return {
      fullPath,
      route
    };
  };

  const buildRoutesFromModules = (modules: ModuleTreeNode[]): RouteRecordRaw[] => {
    return modules
      .map((module) => buildRouteFromModule(module))
      .filter((item): item is { fullPath: string; route: RouteRecordRaw } => Boolean(item))
      .map((item) => item.route);
  };

  const loadRoutes = async () => {
    if (isRoutesLoaded.value) {
      return addRoutes.value;
    }

    const moduleTreeResponse = await getModuleNavTree();
    const dynamicRoutes = buildRoutesFromModules(moduleTreeResponse.data || []);

    dynamicRoutes.forEach((route) => router.addRoute(route));
    addRoutes.value = dynamicRoutes;
    routes.value = [...staticRoutes, ...dynamicRoutes];
    isRoutesLoaded.value = true;
    return dynamicRoutes;
  };

  const resetRoutes = () => {
    addRoutes.value.forEach((route) => {
      if (route.name && router.hasRoute(route.name)) {
        router.removeRoute(route.name);
      }
    });
    addRoutes.value = [];
    routes.value = [...staticRoutes];
    isRoutesLoaded.value = false;
  };

  return {
    routes,
    addRoutes,
    isRoutesLoaded,
    loadRoutes,
    resetRoutes,
    buildRoutesFromModules
  };
});
