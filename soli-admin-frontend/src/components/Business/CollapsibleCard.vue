<template>
  <div class="tech-card collapsible-section" :class="{ 'is-collapsed': !modelValue }">
    <div class="section-header" @click="$emit('update:modelValue', !modelValue)">
      <div class="title">
        <el-icon v-if="icon"><component :is="icon" /></el-icon>
        <span>{{ title }}</span>
      </div>
      <div class="toggle-icon">
        <el-icon><ArrowUp v-if="modelValue" /><ArrowDown v-else /></el-icon>
      </div>
    </div>
    
    <div v-show="modelValue" class="section-content">
      <slot></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue';

interface Props {
  title: string;
  icon?: any;
  modelValue: boolean;
}

defineProps<Props>();
defineEmits(['update:modelValue']);
</script>

<style scoped lang="scss">
.collapsible-section {
  flex-shrink: 0;
  padding: 0 !important;
  margin: 0 !important;
  .section-header {
    display: flex; 
    justify-content: space-between; 
    padding: 4px 12px; 
    cursor: pointer;
    background: #f8f9fb; 
    border-bottom: 1px solid #f0f0f0; 
    font-size: 12px; 
    font-weight: 600;
  }
  .section-content { 
    padding: 6px 12px; 
  }
}
</style>
