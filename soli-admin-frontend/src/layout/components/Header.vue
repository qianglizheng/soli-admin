<template>
  <div class="header">
    <div class="header-left">
      <div class="hamburger-container" @click="toggleSideBar">
        <el-icon :class="{ 'is-active': sidebar.opened }" class="hamburger">
          <Expand v-if="!sidebar.opened" />
          <Fold v-else />
        </el-icon>
      </div>
      <div class="breadcrumb">
        首页 / {{ route.meta.title }}
      </div>
    </div>

    <div class="right-menu">
      <header-search id="header-search" class="right-menu-item" />
      <el-dropdown @command="handleCommand" class="right-menu-item">
        <span class="el-dropdown-link">
          {{ userStore.name || '管理员' }}
          <el-icon class="el-icon--right"><arrow-down /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { usePermissionStore } from '@/store/modules/permission';
import { useAppStore } from '@/store/modules/app';
import { ArrowDown, Expand, Fold } from '@element-plus/icons-vue';
import HeaderSearch from '@/components/HeaderSearch/index.vue';

defineOptions({
  name: "LayoutHeader"
})

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const permissionStore = usePermissionStore();
const appStore = useAppStore();

const sidebar = computed(() => appStore.sidebar);

const toggleSideBar = () => {
  appStore.toggleSideBar();
};

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    await userStore.logout();
    permissionStore.resetRoutes();
    router.push('/login');
  }
};
</script>

<style scoped lang="scss">
.header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  border-bottom: 1px solid #f0f0f0;

  .header-left {
    display: flex;
    align-items: center;
  }
}

.hamburger-container {
  padding: 0 16px;
  line-height: 50px;
  height: 100%;
  float: left;
  cursor: pointer;
  transition: background .3s;
  -webkit-tap-highlight-color: transparent;
  display: flex;
  align-items: center;

  &:hover {
    background: rgba(0, 0, 0, .025)
  }

  .hamburger {
    font-size: 20px;
    color: #515a6e;
  }
}

.breadcrumb {
  margin-left: 10px;
  color: #97a8be;
  font-size: 14px;
}

.right-menu {
  display: flex;
  align-items: center;
  padding-right: 20px;
  height: 100%;

  .right-menu-item {
    display: inline-flex;
    align-items: center;
    padding: 0 12px;
    height: 100%;
    font-size: 16px;
    color: #5a5e66;
    vertical-align: text-bottom;
    transition: background 0.3s;

    &:hover {
      background: rgba(0, 0, 0, .025);
    }
  }
}

.el-dropdown-link {
  cursor: pointer;
  display: flex;
  align-items: center;
  outline: none;
  font-size: 14px;
}
</style>
