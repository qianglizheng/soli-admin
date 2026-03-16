
import { defineStore } from 'pinia';
import router from '@/router';
import Layout from '@/layout/index.vue';
import { getMenuTree } from '@/api/menu';
import type { RouteRecordRaw } from 'vue-router';
import type { SysMenuDTO } from '@/types/global';

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [] as RouteRecordRaw[],
    addRoutes: [] as RouteRecordRaw[]
  }),
  actions: {
    async loadRoutes() {
      const res = await getMenuTree();
      const menus = (res.data || []) as SysMenuDTO[];
      const dynamicRoutes = this.buildRoutesFromMenus(menus);
      dynamicRoutes.forEach(r => router.addRoute(r));
      this.addRoutes = dynamicRoutes;
      this.routes = dynamicRoutes;
      return dynamicRoutes;
    },
    buildRoutesFromMenus(menus: SysMenuDTO[]): RouteRecordRaw[] {
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
          if (n.type === '2') return; // skip button
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
    }
  }
});
