<template>
  <div class="tags-view-container">
    <el-scrollbar class="tags-view-wrapper">
      <div class="tags-inner">
        <router-link 
          v-for="tag in visitedViews" 
          :key="tag.path" 
          :to="{ path: tag.path, query: tag.query }"
          class="tags-view-item" 
          :class="{ active: isActive(tag) }"
        >
          <span class="tag-title">{{ tag.title }}</span>
          <el-icon v-if="!isAffix(tag)" class="close-icon" @click.prevent.stop="closeSelectedTag(tag)">
            <Close />
          </el-icon>
        </router-link>
      </div>
    </el-scrollbar>
  </div>
</template>

<script setup lang="ts">
import { computed, watch, onMounted } from 'vue';
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

watch(() => route.path, () => { addTags(); });

onMounted(() => {
  initTags();
  addTags();
});
</script>

<style scoped lang="scss">
.tags-view-container {
  height: 34px;
  width: 100%;
  background: #fff; /* 还原为纯白色 */
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;

  .tags-view-wrapper {
    width: 100%;
    
    .tags-inner {
      display: flex;
      align-items: center;
      padding: 0 12px;
      height: 34px;
    }

    .tags-view-item {
      display: inline-flex;
      align-items: center;
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d9d9d9;
      background: #fff;
      color: #595959;
      padding: 0 8px;
      font-size: 12px;
      margin-right: 4px;
      text-decoration: none;
      transition: all 0.2s;
      
      &:hover {
        color: var(--app-primary);
        border-color: var(--app-primary);
      }

      &.active {
        background-color: var(--app-primary);
        color: #fff;
        border-color: var(--app-primary);
        
        .close-icon {
          color: #fff;
        }
      }

      .tag-title {
        padding: 0 2px;
      }

      .close-icon {
        width: 14px;
        height: 14px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 10px;
        margin-left: 4px;
        transition: all 0.2s;

        &:hover {
          background-color: rgba(0, 0, 0, 0.1);
        }
      }
    }
  }
}
</style>
