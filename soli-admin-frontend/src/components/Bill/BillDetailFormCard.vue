<template>
  <div class="tech-card collapsible-card">
    <div class="collapsible-card__head" @click="handleToggle">
      <div class="collapsible-card__title">{{ title }}</div>
      <slot name="extra">
        <el-button v-if="collapsible" link class="collapsible-card__toggle" @click.stop="handleToggle">
          {{ currentExpanded ? collapseText : expandText }}
          <el-icon class="collapsible-card__toggle-icon" :class="{ expanded: currentExpanded }"><ArrowDown /></el-icon>
        </el-button>
      </slot>
    </div>

    <div v-show="currentExpanded" class="collapsible-card__body">
      <slot />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ArrowDown } from '@element-plus/icons-vue';

const props = withDefaults(defineProps<{
  title: string;
  modelValue?: boolean;
  collapsible?: boolean;
  expandText?: string;
  collapseText?: string;
}>(), {
  modelValue: true,
  collapsible: true,
  expandText: '展开',
  collapseText: '收起'
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
}>();

/**
 * 当前卡片是否展开；不可折叠时始终视为展开。
 */
const currentExpanded = computed(() => {
  return props.collapsible ? props.modelValue : true;
});

/**
 * 切换卡片展开状态。
 */
const handleToggle = () => {
  if (!props.collapsible) {
    return;
  }
  emit('update:modelValue', !props.modelValue);
};
</script>

<style scoped lang="scss">
.collapsible-card {
  flex-shrink: 0;
  padding: 0 !important;
  overflow: hidden;
}

.collapsible-card__head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.collapsible-card__title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.collapsible-card__toggle {
  padding: 0;
  color: var(--el-text-color-secondary);
}

.collapsible-card__toggle-icon {
  transition: transform 0.2s ease;
}

.collapsible-card__toggle-icon.expanded {
  transform: rotate(180deg);
}

.collapsible-card__body {
  padding: 0 16px 16px;
}
</style>
