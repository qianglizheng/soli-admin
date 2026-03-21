<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper">
      <router-link v-for="tag in visitedViews" :key="tag.path" :to="{ path: tag.path, query: tag.query }"
        class="tags-view-item" :class="isActive(tag) ? 'active' : ''">
        {{ tag.title }}
        <el-icon v-if="!isAffix(tag)" class="el-icon-close" @click.prevent.stop="closeSelectedTag(tag)">
          <Close />
        </el-icon>
      </router-link>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useTagsViewStore } from '@/store/modules/tagsView';
import { Close } from '@element-plus/icons-vue';

defineOptions({
  name: "LayoutTagsView"
})

const tagsViewStore = useTagsViewStore();
const route = useRoute();
const router = useRouter();

const visitedViews = computed(() => tagsViewStore.visitedViews);

const isActive = (tag: any) => tag.path === route.path;

const isAffix = (tag: any) => tag.meta && tag.meta.affix;

const addTags = () => {
  const { name } = route;
  if (name) {
    tagsViewStore.addView(route);
  }
  return false;
};

const initTags = () => {
  // Manually add dashboard
  const dashboardRoute = {
    meta: { affix: true, title: '仪表盘' },
    name: 'Dashboard',
    path: '/dashboard'
  };
  tagsViewStore.addView(dashboardRoute);
};

const closeSelectedTag = (view: any) => {
  tagsViewStore.delView(view).then(({ visitedViews }: any) => {
    if (isActive(view)) {
      toLastView(visitedViews, view);
    }
  });
};

const toLastView = (visitedViews: any[], view: any) => {
  const latestView = visitedViews[visitedViews.length - 1];
  if (latestView) {
    router.push(latestView.fullPath);
  } else {
    if (view.name === 'Dashboard') {
      router.replace({ path: '/redirect' + view.fullPath });
    } else {
      router.push('/');
    }
  }
};

watch(
  () => route.path,
  () => {
    addTags();
  }
);

onMounted(() => {
  initTags();
  addTags();
});
</script>

<style scoped lang="scss">
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);

  .tags-view-wrapper {
    .tags-view-item {
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      margin-top: 4px;
      text-decoration: none;
      transition: all 0.3s;

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background-color: var(--app-primary-color, #409EFF);
        color: #fff;
        border-color: var(--app-primary-color, #409EFF);

        &::before {
          content: '';
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 2px;
        }
      }
    }
  }
}

.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      transform-origin: 100% 50%;

      &:before {
        transform: scale(0.6);
        display: inline-block;
        vertical-align: -3px;
      }

      &:hover {
        background-color: #b4bccc;
        color: #fff;
      }
    }
  }
}
</style>
