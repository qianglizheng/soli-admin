<template>
  <el-dialog
    :model-value="visible"
    title="选择物料"
    width="960px"
    destroy-on-close
    align-center
    @update:model-value="handleVisibleChange"
  >
    <div class="material-select-dialog">
      <el-form :model="queryParams" label-width="72px" size="small" class="material-select-dialog__toolbar">
        <el-row :gutter="12">
          <el-col :span="6">
            <el-form-item label="物料编码">
              <el-input v-model="queryParams.itemCode" clearable @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="物料名称">
              <el-input v-model="queryParams.itemName" clearable @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="规格型号">
              <el-input v-model="queryParams.spec" clearable @keyup.enter="handleSearch" />
            </el-form-item>
          </el-col>

          <template v-if="showMoreSearch">
            <el-col :span="6">
              <el-form-item label="物料分类">
                <el-input v-model="queryParams.category" clearable @keyup.enter="handleSearch" />
              </el-form-item>
            </el-col>
          </template>

          <el-col :span="showMoreSearch ? 24 : 6" class="material-select-dialog__action-col">
            <el-form-item label-width="0" class="material-select-dialog__action-item">
              <el-button icon="Search" @click="handleSearch">搜索</el-button>
              <el-button icon="Refresh" @click="handleReset">重置</el-button>
              <el-button link @click="showMoreSearch = !showMoreSearch">
                {{ showMoreSearch ? '收起' : '更多' }}
                <el-icon class="el-icon--right"><component :is="showMoreSearch ? ArrowUp : ArrowDown" /></el-icon>
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-table
        ref="tableRef"
        :data="pagedMaterialList"
        border
        size="small"
        height="360"
        highlight-current-row
        row-key="itemCode"
        @current-change="handleCurrentChange"
        @selection-change="handleSelectionChange"
        @select="handleSelect"
        @select-all="handleSelectAll"
        @row-dblclick="handleRowDblClick"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="物料编码" prop="itemCode" min-width="140" show-overflow-tooltip />
        <el-table-column label="物料名称" prop="itemName" min-width="180" show-overflow-tooltip />
        <el-table-column label="规格型号" prop="spec" min-width="140" show-overflow-tooltip />
        <el-table-column label="单位" prop="unit" width="80" align="center" />
        <el-table-column label="分类" prop="category" min-width="120" show-overflow-tooltip />
        <el-table-column label="可用库存" prop="stockQty" width="100" align="right" />
      </el-table>

      <div class="material-select-dialog__pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next"
          :total="filteredMaterialList.length"
        />
      </div>
    </div>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" :disabled="!selectedMaterial" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue';
import { computed, nextTick, reactive, ref, watch } from 'vue';
import { type TableInstance } from 'element-plus';
import { filterMaterialList, type MaterialOption, type MaterialQueryParams } from './materialMock.ts';

const props = defineProps<{
  visible: boolean;
}>();

const emit = defineEmits<{
  submit: [value: MaterialOption]
  cancel: []
}>();

const tableRef = ref<TableInstance>();
const selectedMaterial = ref<MaterialOption>();
const syncingSelection = ref(false);
const showMoreSearch = ref(false);

const queryParams = reactive<MaterialQueryParams & { pageNum: number; pageSize: number }>({
  itemCode: '',
  itemName: '',
  spec: '',
  category: '',
  pageNum: 1,
  pageSize: 5
});

const filteredMaterialList = computed(() => filterMaterialList(queryParams));

const pagedMaterialList = computed(() => {
  const start = (queryParams.pageNum - 1) * queryParams.pageSize;
  const end = start + queryParams.pageSize;
  return filteredMaterialList.value.slice(start, end);
});

const syncSelectedState = () => {
  const pageMatchedItem = pagedMaterialList.value.find(item => item.itemCode === selectedMaterial.value?.itemCode);
  if (!pageMatchedItem) {
    selectedMaterial.value = undefined;
  }
  nextTick(() => {
    syncingSelection.value = true;
    tableRef.value?.clearSelection();
    if (pageMatchedItem) {
      tableRef.value?.toggleRowSelection(pageMatchedItem, true);
    }
    tableRef.value?.setCurrentRow(pageMatchedItem || null);
    syncingSelection.value = false;
  });
};

const handleSearch = () => {
  queryParams.pageNum = 1;
};

const handleReset = () => {
  queryParams.itemCode = '';
  queryParams.itemName = '';
  queryParams.spec = '';
  queryParams.category = '';
  queryParams.pageNum = 1;
  queryParams.pageSize = 5;
  showMoreSearch.value = false;
};

const handleCurrentChange = (row?: MaterialOption) => {
  if (row) {
    selectedMaterial.value = row;
  }
};

const handleSelectionChange = (selection: MaterialOption[]) => {
  if (syncingSelection.value) {
    return;
  }
  selectedMaterial.value = selection[0];
};

const handleSelect = (selection: MaterialOption[], row: MaterialOption) => {
  if (syncingSelection.value) {
    return;
  }
  const isSelected = selection.some(item => item.itemCode === row.itemCode);
  syncingSelection.value = true;
  tableRef.value?.clearSelection();
  if (isSelected) {
    tableRef.value?.toggleRowSelection(row, true);
    tableRef.value?.setCurrentRow(row);
    selectedMaterial.value = row;
  } else {
    tableRef.value?.setCurrentRow(null);
    selectedMaterial.value = undefined;
  }
  nextTick(() => {
    syncingSelection.value = false;
  });
};

const handleSelectAll = () => {
  syncingSelection.value = true;
  tableRef.value?.clearSelection();
  selectedMaterial.value = undefined;
  nextTick(() => {
    syncingSelection.value = false;
  });
};

const handleConfirm = () => {
  if (!selectedMaterial.value) {
    return;
  }
  emit('submit', selectedMaterial.value);
};

const handleRowDblClick = (row: MaterialOption) => {
  selectedMaterial.value = row;
  emit('submit', row);
};

const handleCancel = () => {
  emit('cancel');
};

const handleVisibleChange = (visible: boolean) => {
  if (!visible) {
    handleCancel();
  }
};

watch(() => props.visible, (visible) => {
  if (visible) {
    handleReset();
    selectedMaterial.value = undefined;
    syncSelectedState();
  }
});

watch(pagedMaterialList, () => {
  syncSelectedState();
});

watch(filteredMaterialList, (list) => {
  const maxPage = Math.max(1, Math.ceil(list.length / queryParams.pageSize));
  if (queryParams.pageNum > maxPage) {
    queryParams.pageNum = maxPage;
  }
});
</script>

<style scoped lang="scss">
.material-select-dialog {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.material-select-dialog__toolbar {
  padding-bottom: 4px;
}

.material-select-dialog__action-col :deep(.el-form-item__content) {
  justify-content: flex-end;
}

.material-select-dialog__action-item {
  margin-bottom: 0;
}

.material-select-dialog__pagination {
  display: flex;
  justify-content: flex-end;
}
</style>
