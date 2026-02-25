
<template>
  <div class="sidebar" :class="{ 'has-logo': showLogo }">
    <div v-if="showLogo" class="sidebar-logo-container" :class="{ collapse: isCollapse }">
      <router-link key="collapse" class="sidebar-logo-link" to="/">
        <img v-if="isCollapse" :src="logoMini" class="sidebar-logo-mini" />
        <img v-else :src="logoFull" class="sidebar-logo-full" />
      </router-link>
    </div>
    <el-scrollbar wrap-class="scrollbar-wrapper">
      <div class="custom-menu">
        <sidebar-item
          v-for="item in menuRoutes"
          :key="item.path"
          :item="item"
          :base-path="item.path"
          :isCollapse="isCollapse"
        />
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAppStore } from '@/store/modules/app';
import SidebarItem from './SidebarItem.vue';
import path from 'path-browserify';
import logoMini from '@/assets/logo-mini.svg';
import logoFull from '@/assets/logo-full.svg';

const router = useRouter();
const appStore = useAppStore();

const isCollapse = computed(() => !appStore.sidebar.opened);
const showLogo = true;

// Get routes from router options for now (in real app use permission store)
const menuRoutes = computed(() => {
  const routes = router.options.routes;
  const constantRoutes = routes.filter(r => !r.meta?.hidden);

  // Process routes to lift Dashboard children if needed
  return constantRoutes.map(route => {
    // Special handling for Dashboard: if it has children but we want to show it as root
    if (route.path === '/' && route.children && route.children.length === 1 && route.children[0]?.path === 'dashboard') {
      const dashboardChild = route.children[0];
      return {
        ...dashboardChild,
        path: '/dashboard', // Absolute path
        children: undefined // Remove children to make it a leaf node
      };
    }
    return route;
  });
});
</script>

<style scoped lang="scss">
// Variables
$menuBg: #304156;
$subMenuBg: #1f2d3d;
$menuHover: #263445;
$menuText: #bfcbd9;
$menuActiveText: #409EFF;

.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: $menuBg;
}

.sidebar-logo-container {
  position: relative;
  width: 100%;
  height: 50px;
  line-height: 50px;
  background: #2b2f3a;
  text-align: center;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

  & .sidebar-logo-link {
    height: 100%;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;

    & .sidebar-logo-mini {
      width: 32px;
      height: 32px;
      vertical-align: middle;
    }

    & .sidebar-logo-full {
      height: 30px;
      vertical-align: middle;
    }
  }

  &.collapse {
    .sidebar-logo-mini {
      margin-right: 0px;
    }
  }
}

.custom-menu {
  padding: 0;
}

:deep(.el-scrollbar__view) {
  height: 100%;
}
</style>

<style lang="scss">
/* Global styles for popover content */
.sidebar-popper {
  padding: 0 !important;
  background-color: #304156 !important;
  border: none !important;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3) !important;

  .el-popper__arrow::before {
    background-color: #304156 !important;
    border-color: #304156 !important;
  }

  .submenu-list {
    padding: 5px 0;
    max-height: 400px;
    overflow-y: auto;
  }
}
</style>
