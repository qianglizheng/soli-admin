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
        首页 / <span class="active-title">{{ route.meta.title }}</span>
      </div>
    </div>

    <div class="right-menu">
      <header-search id="header-search" class="right-menu-item" />
      <button class="right-menu-item company-entry" type="button" @click="handleOpenCompanySelector">
        <span class="company-entry__icon">
          <el-icon><OfficeBuilding /></el-icon>
        </span>
        <span class="company-entry__content">
          <span class="company-entry__label">当前公司</span>
          <span class="company-entry__name">{{ currentCompanyName }}</span>
        </span>
        <el-tag v-if="currentCompanyTypeLabel" effect="plain" size="small" type="success">
          {{ currentCompanyTypeLabel }}
        </el-tag>
        <el-icon class="company-entry__arrow"><arrow-down /></el-icon>
      </button>
      <el-dropdown @command="handleCommand" class="right-menu-item user-entry">
        <span class="el-dropdown-link">
          <span class="user-entry__avatar">
            <el-icon><UserFilled /></el-icon>
          </span>
          <span class="user-entry__content">
            <span class="user-entry__label">当前用户</span>
            <span class="user-entry__name">{{ userStore.name || '管理员' }}</span>
          </span>
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
import { useCompanyStore } from '@/store/modules/company';
import { usePermissionStore } from '@/store/modules/permission';
import { useAppStore } from '@/store/modules/app';
import { ArrowDown, Expand, Fold, OfficeBuilding, UserFilled } from '@element-plus/icons-vue';
import HeaderSearch from '@/components/HeaderSearch/index.vue';

defineOptions({
  name: "LayoutHeader"
})

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const companyStore = useCompanyStore();
const permissionStore = usePermissionStore();
const appStore = useAppStore();

const sidebar = computed(() => appStore.sidebar);
const currentCompanyName = computed(() => companyStore.currentCompany?.nodeName || '请选择公司');
const currentCompanyTypeLabel = computed(() => companyStore.currentCompany?.typeLabel || '');

const toggleSideBar = () => {
  appStore.toggleSideBar();
};

const handleOpenCompanySelector = async () => {
  await companyStore.openSelector(false);
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
  width: 100%; /* 核心修复：确保顶栏撑满 100% 宽度 */
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0;
  border-bottom: 1px solid var(--app-border-color);
  background: var(--app-header-bg);

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

  .active-title {
    color: #262626;
    font-weight: 600;
    margin-left: 4px;
  }
}

.right-menu {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-right: 20px;
  height: 100%;

  .right-menu-item {
    display: inline-flex;
    align-items: center;
    padding: 0 14px;
    height: calc(100% - 14px);
    color: #5a5e66;
    border-radius: 14px;
    border: 1px solid transparent;
    background: transparent;
    vertical-align: text-bottom;
    transition: background 0.3s;

    &:hover {
      background: rgba(64, 158, 255, 0.06);
      border-color: rgba(64, 158, 255, 0.12);
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

.company-entry,
.user-entry {
  gap: 12px;
}

.company-entry {
  cursor: pointer;

  &__icon {
    width: 36px;
    height: 36px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.14), rgba(54, 207, 201, 0.2));
    color: #409eff;
    font-size: 18px;
    flex-shrink: 0;
  }

  &__content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    line-height: 1.2;
    min-width: 0;
  }

  &__label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
  }

  &__name {
    font-size: 14px;
    color: #303133;
    font-weight: 600;
    max-width: 180px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__arrow {
    color: #909399;
    flex-shrink: 0;
  }
}

.user-entry {
  &__avatar {
    width: 36px;
    height: 36px;
    border-radius: 999px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #409eff, #36cfc9);
    color: #fff;
    font-size: 18px;
    flex-shrink: 0;
    box-shadow: 0 10px 24px rgba(64, 158, 255, 0.18);
  }

  &__content {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    line-height: 1.2;
  }

  &__label {
    font-size: 12px;
    color: #909399;
    margin-bottom: 4px;
  }

  &__name {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }
}

@media screen and (max-width: 768px) {
  .breadcrumb {
    display: none;
  }

  .right-menu {
    gap: 6px;
    padding-right: 10px;

    .right-menu-item {
      padding: 0 10px;
    }
  }

  .company-entry__content,
  .user-entry__content {
    display: none;
  }

  .company-entry__name {
    max-width: 90px;
  }
}
</style>
