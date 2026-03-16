
import { defineStore } from 'pinia';
import { storage } from '@/utils/storage';
import { reactive } from 'vue';

export const useAppStore = defineStore('app', () => {
  const sidebar = reactive({
    opened: storage.get('sidebarStatus') ? !!+storage.get('sidebarStatus') : true,
    withoutAnimation: false
  });

  const toggleSideBar = () => {
    sidebar.opened = !sidebar.opened;
    sidebar.withoutAnimation = false;
    if (sidebar.opened) {
      storage.set('sidebarStatus', 1);
    } else {
      storage.set('sidebarStatus', 0);
    }
  };

  const closeSideBar = (withoutAnimation: boolean) => {
    storage.set('sidebarStatus', 0);
    sidebar.opened = false;
    sidebar.withoutAnimation = withoutAnimation;
  };

  return {
    sidebar,
    toggleSideBar,
    closeSideBar
  };
});
