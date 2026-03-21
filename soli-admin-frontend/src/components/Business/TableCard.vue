<template>
  <div class="tech-card detail-tables-wrapper">
    <slot></slot>
  </div>
</template>

<style scoped lang="scss">
.detail-tables-wrapper {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
  padding: 0 !important;
  margin: 0 !important;
  overflow: hidden;

  :deep(.tech-tabs-fill) {
    display: flex;
    flex-direction: column;
    height: 100%;
    .el-tabs__header { margin: 0; background: #f8f9fb; padding: 0 12px; border-bottom: 1px solid #f0f0f0; }
    .el-tabs__content { flex: 1; min-height: 0; }
    .el-tab-pane { height: 100%; }
  }

  :deep(.tab-pane-content) {
    display: flex;
    flex-direction: column;
    height: 100%;
    padding: 8px 12px;
    box-sizing: border-box;
  }

  :deep(.table-container) {
    flex: 1;
    min-height: 0;
    margin-top: 4px;
    position: relative;
  }
}
</style>
