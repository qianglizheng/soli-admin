<template>
  <div class="bill-detail-table-wrapper">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="left">
        <el-button 
          type="primary" 
          icon="Plus" 
          size="small" 
          @click="handleAdd"
          v-hasPermi="permissionAdd || []"
        >添加行</el-button>
        
        <el-button 
          icon="CopyDocument" 
          size="small" 
          :disabled="!selectedRows.length" 
          @click="handleCopy"
          v-hasPermi="permissionAdd || []"
        >复制行</el-button>
        
        <el-button 
          type="danger" 
          plain 
          icon="Delete" 
          size="small" 
          :disabled="!selectedRows.length" 
          @click="handleDelete"
          v-hasPermi="permissionAdd || []"
        >批量删除</el-button>
        
        <el-divider direction="vertical" />
        
        <el-button 
          icon="Upload" 
          size="small" 
          @click="handleImport"
          v-hasPermi="permissionImport || []"
        >导入</el-button>
        
        <el-button 
          icon="Download" 
          size="small" 
          @click="handleExport"
          v-hasPermi="permissionExport || []"
        >导出</el-button>
      </div>
      <div class="right">
        <slot name="toolbar-right"></slot>
        <el-input placeholder="搜索物料..." size="small" style="width: 180px; margin-left: 12px">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
      </div>
    </div>

    <!-- 表格主体 -->
    <div class="table-main">
      <el-table
        ref="tableRef"
        :data="modelValue"
        border
        height="100%"
        size="small"
        @selection-change="handleSelectionChange"
        class="modern-table"
      >
        <el-table-column type="selection" width="40" align="center" fixed="left" />
        <el-table-column type="index" width="45" label="ID" align="center" fixed="left" />
        <slot></slot>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { Plus, Delete, CopyDocument, Upload, Download, Search } from '@element-plus/icons-vue';
import { ElMessage, type TableInstance } from 'element-plus';

const props = defineProps<{
  modelValue: any[];
  permissionAdd?: string[];
  permissionImport?: string[];
  permissionExport?: string[];
}>();

const emit = defineEmits(['update:modelValue', 'add', 'import', 'export', 'selection-change']);

const tableRef = ref<TableInstance>();
const selectedRows = ref<any[]>([]);

// 自动滚动到底部的方法
const scrollToBottom = () => {
  nextTick(() => {
    if (tableRef.value) {
      const scrollWrapper = tableRef.value.$el.querySelector('.el-scrollbar__wrap');
      if (scrollWrapper) {
        scrollWrapper.scrollTop = scrollWrapper.scrollHeight;
      }
    }
  });
};

// 暴露给外部调用
defineExpose({ scrollToBottom });

const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val;
  emit('selection-change', val);
};

const handleAdd = () => {
  emit('add');
};

const handleCopy = () => {
  const newItems = [...props.modelValue];
  selectedRows.value.forEach(row => {
    newItems.push({ ...row });
  });
  emit('update:modelValue', newItems);
  ElMessage.success(`成功复制 ${selectedRows.value.length} 行`);
  scrollToBottom(); // 复制后也滚动
};

const handleDelete = () => {
  const newItems = props.modelValue.filter(item => !selectedRows.value.includes(item));
  emit('update:modelValue', newItems);
  selectedRows.value = [];
  ElMessage.success('删除成功');
};

const handleImport = () => {
  emit('import');
};

const handleExport = () => {
  emit('export');
};
</script>

<style scoped lang="scss">
.bill-detail-table-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 12px 16px;
  box-sizing: border-box;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.table-main {
  flex: 1;
  min-height: 0;
  padding-bottom: 1px; 
}

:deep(.el-table) {
  border-bottom: 1px solid var(--el-table-border-color) !important;
}
</style>
