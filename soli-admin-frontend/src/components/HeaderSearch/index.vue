
<template>
  <div class="header-search">
    <div class="search-trigger" @click="showDialog = true">
      <el-icon class="search-icon"><Search /></el-icon>
      <span class="search-text">搜索菜单...</span>
      <span class="search-shortcut">⌘ K</span>
    </div>

    <el-dialog
      v-model="showDialog"
      :show-close="false"
      width="600px"
      append-to-body
      destroy-on-close
      class="search-dialog"
      @opened="onDialogOpen"
    >
      <div class="search-dialog-content">
        <div class="search-input-wrapper">
          <el-icon class="prefix-icon"><Search /></el-icon>
          <input
            ref="inputRef"
            v-model="search"
            placeholder="搜索菜单 (支持拼音、路径)..."
            class="custom-search-input"
            @input="onSearch"
            @keydown.up.prevent="moveActive(-1)"
            @keydown.down.prevent="moveActive(1)"
            @keydown.enter="handleEnter"
          />
          <div class="close-hint" @click="showDialog = false">ESC</div>
        </div>

        <div v-if="options.length > 0" class="search-results-list">
          <div
            v-for="(item, index) in options"
            :key="item.path"
            class="result-item"
            :class="{ active: activeIndex === index }"
            @mouseenter="activeIndex = index"
            @click="handleSelect(item)"
          >
            <div class="result-icon">
              <el-icon><component :is="item.icon || 'Link'" /></el-icon>
            </div>
            <div class="result-info">
              <div class="result-title">{{ item.title[item.title.length - 1] }}</div>
              <div class="result-path">{{ item.title.join(' / ') }}</div>
            </div>
            <el-icon class="enter-icon"><Right /></el-icon>
          </div>
        </div>

        <div v-else-if="search" class="search-empty">
          <el-empty :image-size="60" description="未找到相关菜单" />
        </div>

        <div v-else class="search-footer-hint">
          <span><kbd>↑</kbd><kbd>↓</kbd> 选择</span>
          <span><kbd>Enter</kbd> 跳转</span>
          <span><kbd>Esc</kbd> 关闭</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, onUnmounted } from 'vue';
import { Search, Right } from '@element-plus/icons-vue';
import Fuse from 'fuse.js';
import { useRouter } from 'vue-router';
import path from 'path-browserify';
import { usePermissionStore } from '@/store/modules/permission';

const router = useRouter();
const permissionStore = usePermissionStore();

const showDialog = ref(false);
const search = ref('');
const activeIndex = ref(0);
const options = ref<any[]>([]);
const searchPool = ref<any[]>([]);
const fuse = ref<any>(undefined);
const inputRef = ref<HTMLInputElement | null>(null);

const routes = computed(() => permissionStore.routes);

const onDialogOpen = () => {
  inputRef.value?.focus();
};

const onSearch = () => {
  if (search.value !== '') {
    options.value = fuse.value.search(search.value).map((result: any) => result.item).slice(0, 8);
    activeIndex.value = 0;
  } else {
    options.value = [];
  }
};

const moveActive = (step: number) => {
  const next = activeIndex.value + step;
  if (next >= 0 && next < options.value.length) {
    activeIndex.value = next;
  }
};

const handleEnter = () => {
  if (options.value[activeIndex.value]) {
    handleSelect(options.value[activeIndex.value]);
  }
};

const handleSelect = (item: any) => {
  if (item.path) {
    router.push(item.path);
  }
  showDialog.value = false;
  search.value = '';
  options.value = [];
};

const initFuse = (list: any[]) => {
  fuse.value = new Fuse(list, {
    shouldSort: true,
    threshold: 0.4,
    keys: [{ name: 'title', weight: 0.7 }, { name: 'path', weight: 0.3 }]
  });
};

const generateRoutes = (routes: any[], basePath = '/', prefixTitle: string[] = []) => {
  let res: any[] = [];
  for (const router of routes) {
    if (router.hidden || router.meta?.hidden) continue;
    const data: any = {
      path: path.resolve(basePath, router.path),
      title: [...prefixTitle]
    };
    if (router.meta && router.meta.title) {
      data.title = [...data.title, router.meta.title];
      if (router.meta.icon) data.icon = router.meta.icon;
    }
    let childrenRes: any[] = [];
    if (router.children) {
      childrenRes = generateRoutes(router.children, data.path, data.title);
      if (childrenRes.length >= 1) res = [...res, ...childrenRes];
    }
    if (childrenRes.length === 0 && router.meta && router.meta.title) {
      if (router.redirect !== 'noRedirect') res.push(data);
    }
  }
  return res;
};

// 快捷键监听
const handleKeyDown = (e: KeyboardEvent) => {
  if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
    e.preventDefault();
    showDialog.value = true;
  }
};

watch(routes, () => {
  searchPool.value = generateRoutes(routes.value);
  initFuse(searchPool.value);
});

onMounted(() => {
  searchPool.value = generateRoutes(permissionStore.routes);
  initFuse(searchPool.value);
  window.addEventListener('keydown', handleKeyDown);
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeyDown);
});
</script>

<style lang="scss">
/* 全局样式修改 Dialog 表现 */
.search-dialog {
  border-radius: 12px !important;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04) !important;

  .el-dialog__header {
    display: none;
  }
  .el-dialog__body {
    padding: 0;
  }
}
</style>

<style lang="scss" scoped>
.header-search {
  display: flex;
  align-items: center;
}

.search-trigger {
  display: flex;
  align-items: center;
  background: #f4f4f5;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  height: 32px;
  padding: 0 8px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 140px;

  &:hover {
    background: #e9e9eb;
    border-color: var(--app-primary);
  }

  .search-icon {
    font-size: 14px;
    color: #606266;
  }

  .search-text {
    margin-left: 8px;
    font-size: 13px;
    color: #909399;
    flex: 1;
  }

  .search-shortcut {
    font-size: 11px;
    color: #a8abb2;
    background: #fff;
    border: 1px solid #dcdfe6;
    padding: 1px 4px;
    border-radius: 4px;
    line-height: 1;
    margin-left: 8px;
  }
}

.search-dialog-content {
  .search-input-wrapper {
    display: flex;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;

    .prefix-icon {
      font-size: 20px;
      color: var(--app-primary);
    }

    .custom-search-input {
      flex: 1;
      border: none;
      outline: none;
      margin-left: 12px;
      font-size: 16px;
      color: #303133;
      background: transparent;

      &::placeholder {
        color: #c0c4cc;
      }
    }

    .close-hint {
      font-size: 12px;
      color: #909399;
      background: #f4f4f5;
      padding: 2px 6px;
      border-radius: 4px;
      cursor: pointer;
    }
  }

  .search-results-list {
    max-height: 400px;
    overflow-y: auto;
    padding: 8px;

    .result-item {
      display: flex;
      align-items: center;
      padding: 10px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;
      margin-bottom: 2px;

      &.active {
        background: #f5f7fa; /* 低调的浅灰色背景 */

        .result-title {
          color: var(--app-primary) !important; /* 仅改变字体颜色 */
        }

        .result-path, .result-icon, .enter-icon {
          color: var(--app-primary) !important;
        }
      }

      .result-icon {
        width: 36px;
        height: 36px;
        background: #f0f7ff;
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        color: var(--app-primary);
        margin-right: 12px;
      }

      .result-info {
        flex: 1;

        .result-title {
          font-size: 14px;
          font-weight: 500;
          color: #303133;
          margin-bottom: 2px;
        }

        .result-path {
          font-size: 12px;
          color: #909399;
        }
      }

      .enter-icon {
        font-size: 16px;
        color: #c0c4cc;
        opacity: 0;
        transition: opacity 0.2s;
      }

      &.active .enter-icon {
        opacity: 1;
      }
    }
  }

  .search-footer-hint {
    padding: 12px 20px;
    border-top: 1px solid #f0f0f0;
    display: flex;
    gap: 20px;
    color: #909399;
    font-size: 12px;

    kbd {
      background: #f4f4f5;
      border: 1px solid #dcdfe6;
      border-radius: 3px;
      padding: 0 4px;
      margin: 0 2px;
      font-family: inherit;
    }
  }
}
</style>
