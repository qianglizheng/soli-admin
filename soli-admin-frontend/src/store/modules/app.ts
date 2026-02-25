
import { defineStore } from 'pinia';
import { storage } from '@/utils/storage';

export const useAppStore = defineStore('app', {
  state: () => ({
    sidebar: {
      opened: storage.get('sidebarStatus') ? !!+storage.get('sidebarStatus') : true,
      withoutAnimation: false
    }
  }),
  actions: {
    toggleSideBar() {
      this.sidebar.opened = !this.sidebar.opened;
      this.sidebar.withoutAnimation = false;
      if (this.sidebar.opened) {
        storage.set('sidebarStatus', 1);
      } else {
        storage.set('sidebarStatus', 0);
      }
    },
    closeSideBar(withoutAnimation: boolean) {
      storage.set('sidebarStatus', 0);
      this.sidebar.opened = false;
      this.sidebar.withoutAnimation = withoutAnimation;
    }
  }
});
