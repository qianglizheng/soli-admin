<template>
  <div class="tech-card detail-header">
    <el-page-header @back="$emit('back')" class="custom-header">
      <template #content>
        <div class="header-info">
          <span class="bill-title">{{ title }}</span>
          <span v-if="billNo" class="bill-no">{{ billNo }}</span>
          <el-tag v-if="statusName" :type="statusType" effect="plain" size="small" class="status-tag">
            {{ statusName }}
          </el-tag>
        </div>
      </template>
      <template #extra>
        <div class="right-actions">
          <slot name="extra"></slot>
        </div>
      </template>
    </el-page-header>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title: string;
  billNo?: string;
  statusName?: string;
  statusType?: string;
}

defineProps<Props>();
defineEmits(['back']);
</script>

<style scoped lang="scss">
.detail-header {
  flex-shrink: 0;
  padding: 6px 16px !important;
  margin: 0 !important;
}

.custom-header {
  width: 100%;
  :deep(.el-page-header__header) {
    align-items: center;
    height: 32px;
  }
  :deep(.el-page-header__left) {
    margin-right: 12px;
    .el-page-header__back {
      font-size: 13px;
    }
    &::after {
      content: "";
      width: 1px;
      height: 14px;
      background-color: var(--app-border-color);
      margin-left: 12px;
    }
  }
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bill-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.bill-no {
  font-family: 'Consolas', monospace;
  font-size: 12px;
  color: var(--el-text-color-regular);
  background: var(--app-bg-color);
  padding: 1px 6px;
  border-radius: 4px;
  border: 1px solid var(--app-border-color);
}

.status-tag {
  font-weight: 600;
}

.right-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
