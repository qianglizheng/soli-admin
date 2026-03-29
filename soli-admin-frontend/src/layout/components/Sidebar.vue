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
        <sidebar-item v-for="item in menuRoutes" :key="item.path" :item="item" :base-path="item.path"
          :isCollapse="isCollapse" />
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useAppStore } from '@/store/modules/app';
import SidebarItem from './SidebarItem.vue';
import logoMini from '@/assets/logo-mini.svg';
import logoFull from '@/assets/logo-full.svg';
import { usePermissionStore } from '@/store/modules/permission';

defineOptions({
  name: "LayoutSidebar"
})

const appStore = useAppStore();

const isCollapse = computed(() => !appStore.sidebar.opened);
const showLogo = true;

const permissionStore = usePermissionStore();

// 使用权限仓库中的动态路由
const menuRoutes = computed(() => {
  const routes = permissionStore.routes as any[];
  const constantRoutes = routes.filter((r: any) => !r.meta?.hidden);

  // 处理路由结构，必要时提升仪表盘子路由
  return constantRoutes.map((route: any) => {
    // 仪表盘特殊处理：当它只有一个 dashboard 子路由时，直接作为根节点展示
    if (route.path === '/' && route.children && route.children.length === 1 && route.children[0]?.path === 'dashboard') {
      const dashboardChild = route.children[0];
      return {
        ...dashboardChild,
        path: '/dashboard', // 改为绝对路径
        children: undefined // 移除子节点，使其成为叶子节点
      };
    }
    return route;
  });
});
</script>

<style scoped lang="scss">
// 颜色变量
$menuBg: #001529;
$subMenuBg: #000c17;
$menuHover: #1890ff;
$menuText: rgba(255, 255, 255, 0.65);
$menuActiveText: #fff;

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
  background: #002140;
  text-align: center;
  overflow: hidden;

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
/* 弹出菜单的全局样式 */
.sidebar-popper {
  padding: 0 !important;
  background-color: #001529 !important;
  border: none !important;
  box-shadow: 2px 2px 8px rgba(0, 0, 0, 0.3) !important;

  .el-popper__arrow::before {
    background-color: #001529 !important;
    border-color: #001529 !important;
  }

  .submenu-list {
    padding: 5px 0;
    max-height: 400px;
    overflow-y: auto;
  }
}
</style>
