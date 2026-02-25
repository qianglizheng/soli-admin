
import { defineStore } from 'pinia';

export const usePermissionStore = defineStore('permission', {
  state: () => ({
    routes: [],
    addRoutes: []
  }),
  actions: {
    generateRoutes(roles: string[]) {
      // 简单模拟根据角色生成路由
      this.routes = [];
      return Promise.resolve(this.routes);
    }
  }
});
