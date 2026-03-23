<template>
  <div class="bill-detail-table-wrapper">
    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="left">
        <el-button
          v-if="permissionAccess.isButtonVisible('add')"
          type="primary"
          icon="Plus"
          size="small"
          :disabled="permissionAccess.isButtonReadonly('add')"
          @click="handleAdd"
        >{{ permissionAccess.getButtonLabel('add') }}</el-button>

        <el-button
          v-if="permissionAccess.isButtonVisible('copy')"
          icon="CopyDocument"
          size="small"
          :disabled="!selectedRows.length || permissionAccess.isButtonReadonly('copy')"
          @click="handleCopy"
        >{{ permissionAccess.getButtonLabel('copy') }}</el-button>

        <el-button
          v-if="permissionAccess.isButtonVisible('delete')"
          type="danger"
          plain
          icon="Delete"
          size="small"
          :disabled="!selectedRows.length || permissionAccess.isButtonReadonly('delete')"
          @click="handleDelete"
        >{{ permissionAccess.getButtonLabel('delete') }}</el-button>

        <el-divider v-if="showToolbarDivider" direction="vertical" />

        <el-button
          v-if="permissionAccess.isButtonVisible('import')"
          icon="Upload"
          size="small"
          :disabled="permissionAccess.isButtonReadonly('import')"
          @click="handleImport"
        >{{ permissionAccess.getButtonLabel('import') }}</el-button>

        <el-button
          v-if="permissionAccess.isButtonVisible('export')"
          icon="Download"
          size="small"
          :disabled="permissionAccess.isButtonReadonly('export')"
          @click="handleExport"
        >{{ permissionAccess.getButtonLabel('export') }}</el-button>
      </div>
      <div class="right">
        <slot name="toolbar-right"></slot>
      </div>
    </div>

    <!-- 表格主体 -->
    <div class="table-main">
      <EnterFocusScope ref="focusScopeRef" fill @reach-end="handleReachEnd">
        <el-table
          ref="tableRef"
          :data="modelValue"
          border
          height="100%"
          size="small"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
          class="modern-table"
        >
          <el-table-column v-if="showSelectionColumn" type="selection" width="40" align="center" fixed="left" />
          <el-table-column type="index" width="45" label="ID" align="center" fixed="left" />
          <slot></slot>
        </el-table>
      </EnterFocusScope>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, nextTick } from 'vue';
import { ElMessage, type TableInstance } from 'element-plus';
import {
  createBillPermissionAccessor,
  type BillPermissionSource
} from './billPermission';

interface EnterFocusScopeExpose {
  focusNextFrom?: (target: HTMLElement) => boolean;
  focusInLastContainer?: (containerSelector: string) => boolean;
}

const props = withDefaults(defineProps<{
  modelValue: any[];
  permissions?: BillPermissionSource;
  focusNewRowOnAdd?: boolean;
  appendRowOnEnterEnd?: boolean;
  focusRowSelector?: string;
}>(), {
  focusNewRowOnAdd: true,
  appendRowOnEnterEnd: true,
  focusRowSelector: '.el-table__body-wrapper tbody tr'
});

const emit = defineEmits(['update:modelValue', 'add', 'import', 'export', 'selection-change', 'sort-change']);

const focusScopeRef = ref<EnterFocusScopeExpose>();
const tableRef = ref<TableInstance>();
const selectedRows = ref<any[]>([]);
const toolbarSplitLeftKeys = ['add', 'copy', 'delete'];
const toolbarSplitRightKeys = ['import', 'export'];
const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  buttonLabels: {
    add: '添加行',
    copy: '复制行',
    delete: '批量删除',
    import: '导入',
    export: '导出'
  }
});

/**
 * 判断当前是否允许新增行。
 */
const canAddRow = computed(() => {
  return permissionAccess.isButtonVisible('add') && !permissionAccess.isButtonReadonly('add');
});

/**
 * 当复制或删除可用时，展示勾选列。
 */
const showSelectionColumn = computed(() => {
  return permissionAccess.isButtonVisible('copy') || permissionAccess.isButtonVisible('delete');
});

/**
 * 仅在左右两组按钮都存在时展示分割线。
 */
const showToolbarDivider = computed(() => {
  return toolbarSplitLeftKeys.some((key) => {
    return permissionAccess.isButtonVisible(key);
  }) && toolbarSplitRightKeys.some((key) => {
    return permissionAccess.isButtonVisible(key);
  });
});

// 自动滚动到底部的方法
const setScrollToBottom = () => {
  if (!tableRef.value) {
    return;
  }
  const scrollWrapper = tableRef.value.$el.querySelector('.el-scrollbar__wrap');
  if (scrollWrapper) {
    scrollWrapper.scrollTop = scrollWrapper.scrollHeight;
  }
};

/**
 * 滚动到表格底部。
 */
const scrollToBottom = () => {
  nextTick(() => {
    setScrollToBottom();
  });
};

// 暴露给外部调用
const clearFilter = (columnKeys?: string[]) => {
  if (!tableRef.value) {
    return;
  }
  if (!columnKeys?.length) {
    tableRef.value.clearFilter();
    return;
  }
  const tableColumns = (((tableRef.value as any).store?.states?.columns?.value) || []) as Array<{ columnKey?: string }>;
  const matchedKeys = columnKeys.filter((key) => {
    return tableColumns.some((column) => {
      return column.columnKey === key;
    });
  });
  if (matchedKeys.length) {
    tableRef.value.clearFilter(matchedKeys);
  }
};

/**
 * 清空表格排序。
 */
const clearSort = () => {
  tableRef.value?.clearSort();
};

/**
 * 重新计算表格布局。
 */
const doLayout = () => {
  tableRef.value?.doLayout();
};

/**
 * 聚焦最后一行的第一个可编辑控件。
 */
const focusLastRowFirstEditable = () => {
  nextTick(() => {
    doLayout();
    setScrollToBottom();
    requestAnimationFrame(() => {
      focusScopeRef.value?.focusInLastContainer?.(props.focusRowSelector);
    });
  });
};

/**
 * 从当前焦点跳转到下一个可编辑控件。
 */
const focusNextByTarget = (target: HTMLElement) => {
  return focusScopeRef.value?.focusNextFrom?.(target) || false;
};

defineExpose({ scrollToBottom, clearFilter, clearSort, doLayout, focusLastRowFirstEditable, focusNextByTarget });

/**
 * 同步勾选行。
 */
const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val;
  emit('selection-change', val);
};

/**
 * 转发排序变更事件。
 */
const handleSortChange = (val: any) => {
  emit('sort-change', val);
};

/**
 * 新增一行并在允许时自动聚焦到新行。
 */
const handleAdd = () => {
  if (!canAddRow.value) {
    return;
  }
  emit('add');
  if (props.focusNewRowOnAdd) {
    focusLastRowFirstEditable();
  }
};

/**
 * 当回车导航到最后一个可编辑控件时，自动追加新行。
 */
const handleReachEnd = () => {
  if (!props.appendRowOnEnterEnd || !canAddRow.value) {
    return;
  }
  handleAdd();
};

/**
 * 复制当前选中的明细行。
 */
const handleCopy = () => {
  if (!permissionAccess.isButtonVisible('copy') || permissionAccess.isButtonReadonly('copy')) {
    return;
  }
  const newItems = [...props.modelValue];
  selectedRows.value.forEach((row) => {
    newItems.push({ ...row });
  });
  emit('update:modelValue', newItems);
  ElMessage.success(`成功复制 ${selectedRows.value.length} 行`);
  scrollToBottom(); // 复制后也滚动
};

/**
 * 删除当前选中的明细行。
 */
const handleDelete = () => {
  if (!permissionAccess.isButtonVisible('delete') || permissionAccess.isButtonReadonly('delete')) {
    return;
  }
  const newItems = props.modelValue.filter((item) => {
    return !selectedRows.value.includes(item);
  });
  emit('update:modelValue', newItems);
  selectedRows.value = [];
  ElMessage.success('删除成功');
};

/**
 * 触发导入事件。
 */
const handleImport = () => {
  if (!permissionAccess.isButtonVisible('import') || permissionAccess.isButtonReadonly('import')) {
    return;
  }
  emit('import');
};

/**
 * 触发导出事件。
 */
const handleExport = () => {
  if (!permissionAccess.isButtonVisible('export') || permissionAccess.isButtonReadonly('export')) {
    return;
  }
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
