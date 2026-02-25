
<template>
  <div class="header-search">
    <el-select
      ref="headerSearchSelect"
      v-model="search"
      :remote-method="querySearch"
      filterable
      default-first-option
      remote
      placeholder="搜索菜单..."
      class="header-search-select"
      @change="change"
      popper-class="header-search-popper"
    >
      <template #prefix>
        <el-icon class="search-icon">
          <Search />
        </el-icon>
      </template>
      <el-option
        v-for="item in options"
        :key="item.path"
        :value="item"
        :label="item.title.join(' > ')"
      >
        <div class="option-item">
          <div class="option-content">
            <el-icon v-if="item.icon" class="menu-icon"><component :is="item.icon" /></el-icon>
            <span class="option-title">{{ item.title.join(' > ') }}</span>
          </div>
        </div>
      </el-option>
    </el-select>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { Search, Link } from '@element-plus/icons-vue';
import Fuse from 'fuse.js';
import { useRouter } from 'vue-router';
import path from 'path-browserify';
import { usePermissionStore } from '@/store/modules/permission';

const router = useRouter();
const permissionStore = usePermissionStore();

const search = ref('');
const options = ref<any[]>([]);
const searchPool = ref<any[]>([]);
const fuse = ref<any>(undefined);
const headerSearchSelect = ref(null);

const routes = computed(() => permissionStore.routes);

const change = (val: any) => {
  if (val.path) {
    router.push(val.path);
  }
  search.value = '';
  options.value = [];
};

const initFuse = (list: any[]) => {
  fuse.value = new Fuse(list, {
    shouldSort: true,
    threshold: 0.4,
    location: 0,
    distance: 100,
    minMatchCharLength: 1,
    keys: [{
      name: 'title',
      weight: 0.7
    }, {
      name: 'path',
      weight: 0.3
    }]
  });
};

const generateRoutes = (routes: any[], basePath = '/', prefixTitle: string[] = []) => {
  let res: any[] = [];

  for (const router of routes) {
    if (router.hidden) { continue; }

    const data: any = {
      path: path.resolve(basePath, router.path),
      title: [...prefixTitle]
    };

    if (router.meta && router.meta.title) {
      data.title = [...data.title, router.meta.title];

      if (router.meta.icon) {
        data.icon = router.meta.icon;
      }
    }

    let childrenRes: any[] = [];
    if (router.children) {
      childrenRes = generateRoutes(router.children, data.path, data.title);
      if (childrenRes.length >= 1) {
        res = [...res, ...childrenRes];
      }
    }

    if (childrenRes.length === 0 && router.meta && router.meta.title) {
      if (router.redirect !== 'noRedirect') {
        res.push(data);
      }
    }
  }
  return res;
};

const querySearch = (query: string) => {
  if (query !== '') {
    options.value = fuse.value.search(query).map((result: any) => result.item);
  } else {
    options.value = [];
  }
};

watch(routes, () => {
  searchPool.value = generateRoutes(routes.value);
  initFuse(searchPool.value);
});

onMounted(() => {
  let routeList = permissionStore.routes;
  if (!routeList || routeList.length === 0) {
      // @ts-ignore
      routeList = router.options.routes;
  }

  searchPool.value = generateRoutes(routeList);
  initFuse(searchPool.value);
});
</script>

<style lang="scss">
.header-search-popper {
  .el-select-dropdown__item {
    height: 40px;
    line-height: 40px;
    padding: 0 10px;

    .option-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
    }
  }
}
</style>

<style lang="scss" scoped>
.header-search {
  display: flex;
  align-items: center;
  margin-right: 10px;

  .header-search-select {
    width: 260px;
    transition: width 0.3s;

    :deep(.el-input__inner) {
      height: 36px;
      line-height: 36px;
    }

    .search-icon {
      font-size: 14px;
      color: #909399;
      margin-right: 5px;
    }
  }
}

.option-content {
  display: flex;
  align-items: center;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;

  .menu-icon {
    font-size: 14px;
    margin-right: 8px;
    color: #909399;
    vertical-align: middle;
  }

  .option-title {
    color: #606266;
    font-size: 13px;
  }
}

.option-arrow {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
</style>
