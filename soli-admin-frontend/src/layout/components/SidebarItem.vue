<template>
  <div class="sidebar-item-container">
    <!-- Leaf node (no children) -->
    <template v-if="!hasChildren(item)">
      <div class="menu-item" :class="{ 'is-active': isActive }" @click="handleClick">
        <el-icon v-if="item.meta && item.meta.icon" class="menu-icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span class="menu-title">{{ item.meta?.title }}</span>
      </div>
    </template>

    <!-- Node with children -->
    <template v-else>
      <el-popover v-model:visible="popoverVisible" placement="right-start" :width="200" trigger="click"
        popper-class="sidebar-popper" transition="el-zoom-in-top">
        <template #reference>
          <div class="menu-item has-children" :class="{ 'is-active': isChildActive, 'is-opened': popoverVisible }">
            <el-icon v-if="item.meta && item.meta.icon" class="menu-icon">
              <component :is="item.meta.icon" />
            </el-icon>
            <span class="menu-title">{{ item.meta?.title }}</span>
            <template v-if="!isCollapse">
              <el-icon class="arrow-icon">
                <ArrowRight />
              </el-icon>
            </template>
          </div>
        </template>

        <div class="submenu-list">
          <sidebar-item v-for="child in item.children" :key="child.path" :item="child"
            :base-path="resolvePath(basePath, item.path)" @item-click="handleChildClick" />
        </div>
      </el-popover>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ArrowRight } from '@element-plus/icons-vue';
import path from 'path-browserify';

defineOptions({
  name: "LayoutSidebarItem"
})

const props = defineProps({
  basePath: {
    default: '',
    type: String
  },
  isCollapse: {
    default: false,
    type: Boolean
  },
  item: {
    required: true,
    type: Object
  }
});

const emit = defineEmits(['item-click']);

const route = useRoute();
const router = useRouter();
const popoverVisible = ref(false);

const hasChildren = (item: any) => {
  if (item.children) {
    const showingChildren = item.children.filter((item: any) => !item.hidden);
    return showingChildren.length > 0;
  }
  return false;
};

const resolvePath = (base: string, p: string) => path.resolve(base, p);

const fullPath = computed(() => resolvePath(props.basePath, props.item.path));

const isActive = computed(() => route.path === fullPath.value);

const isChildActive = computed(() => route.path.startsWith(fullPath.value + '/'));

const handleClick = () => {
  router.push(fullPath.value);
  emit('item-click'); // Trigger auto-close
};

const handleChildClick = () => {
  popoverVisible.value = false; // Close current level
  emit('item-click'); // Propagate close event up to parent
};
</script>

<style scoped lang="scss">
// Variables
$menuText: #bfcbd9;
$menuActiveText: #fff;
$menuHover: #263445;
$menuActiveBg: #1890ff; // Keeping variable but overriding below with rgba
$menuOpenedBg: rgba(0, 0, 0, 0.25);

.sidebar-item-container {
  width: 100%;
}

.menu-item {
  display: flex;
  align-items: center;
  height: 50px;
  line-height: 50px;
  padding: 0 16px;
  cursor: pointer;
  color: $menuText;
  transition: all 0.3s;
  position: relative;
  user-select: none;
  font-size: 14px;

  &:hover {
    background-color: $menuHover;
    color: #fff;
  }

  &.is-active {
    color: $menuActiveText;
    // Use a more subtle, lower opacity blue for active state
    background-color: rgba(24, 144, 255, 0.3);
    border-left: 3px solid #1890ff; // Add a border indicator

    .menu-icon {
      color: #fff;
    }
  }

  &.is-opened {
    background-color: $menuOpenedBg;
    color: #fff;

    .menu-icon {
      color: #fff;
    }
  }

  .menu-icon {
    margin-right: 12px;
    font-size: 18px;
    width: 24px;
    text-align: center;
    color: $menuText;
    transition: color 0.3s;
  }

  .menu-title {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .arrow-icon {
    font-size: 12px;
    margin-left: 8px;
    color: $menuText;
  }
}
</style>
