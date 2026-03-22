<template>
  <div class="tab-pane-content">
    <BillDetailTable
      ref="billTableRef"
      v-model="innerItems"
      @add="handleAdd"
      @sort-change="handleSortChange"
      :permissions="permissions"
    >
      <template #toolbar-right>
        <BillTemplateColumnSetting
          v-model="visibleColumnKeys"
          :columns="columnOptions"
          :default-keys="permissionDefaultVisibleColumnKeys"
          :permissions="permissions"
        />
      </template>

      <el-table-column
        v-if="canShowColumn('itemCode')"
        column-key="itemCode"
        :label="getFieldTitle('itemCode')"
        prop="itemCode"
        width="130"
        fixed="left"
        sortable
        :filters="itemCodeFilters"
        :filter-method="filterByItemCode"
      >
        <template #default="scope">
          <el-input
            v-model="scope.row.itemCode"
            size="small"
            :disabled="isFieldReadonly('itemCode')"
            data-enter-focus-stop
            @keydown.enter.stop.prevent="handleItemCodeEnter(scope.row, $event)"
          >
            <template v-if="isButtonVisible('selectMaterial')" #append>
              <el-button
                icon="MoreFilled"
                :disabled="isButtonReadonly('selectMaterial') || isFieldReadonly('itemCode')"
                @click="showMaterialSelect(scope.$index)"
              />
            </template>
          </el-input>
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('itemName')"
        column-key="itemName"
        :label="getFieldTitle('itemName')"
        prop="itemName"
        min-width="180"
        show-overflow-tooltip
        sortable
        :filters="itemNameFilters"
        :filter-method="filterByItemName"
      />

      <el-table-column
        v-if="canShowColumn('spec')"
        :label="getFieldTitle('spec')"
        prop="spec"
        min-width="120"
        show-overflow-tooltip
        sortable
      />

      <el-table-column
        v-if="canShowColumn('unit')"
        column-key="unit"
        :label="getFieldTitle('unit')"
        prop="unit"
        width="80"
        align="center"
        sortable
        :filters="unitFilters"
        :filter-method="filterByUnit"
      />

      <el-table-column
        v-if="canShowColumn('qty')"
        :label="getFieldTitle('qty')"
        prop="qty"
        width="100"
        align="right"
        sortable
        :sort-method="sortByQty"
      >
        <template #default="scope">
          <el-input-number
            v-model="scope.row.qty"
            :controls="false"
            :disabled="isFieldReadonly('qty')"
            size="small"
            style="width: 100%"
            @focus="handleFocus(scope.row, 'qty')"
            @blur="handleBlur(scope.row, 'qty')"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('priceExcl')"
        :label="getFieldTitle('priceExcl')"
        prop="priceExcl"
        width="120"
        align="right"
        sortable
        :sort-method="sortByPriceExcl"
      >
        <template #default="scope">
          <el-input-number
            v-model="scope.row.priceExcl"
            :controls="false"
            :precision="4"
            :disabled="isFieldReadonly('priceExcl')"
            size="small"
            style="width: 100%"
            @focus="handleFocus(scope.row, 'priceExcl')"
            @blur="handleBlur(scope.row, 'priceExcl')"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('taxRate')"
        column-key="taxRate"
        :label="getFieldTitle('taxRate')"
        prop="taxRate"
        width="90"
        align="right"
        sortable
        :sort-method="sortByTaxRate"
        :filters="taxRateFilters"
        :filter-method="filterByTaxRate"
      >
        <template #default="scope">
          <el-input-number
            v-model="scope.row.taxRate"
            :controls="false"
            :disabled="isFieldReadonly('taxRate')"
            size="small"
            style="width: 100%"
            @focus="handleFocus(scope.row, 'taxRate')"
            @blur="handleBlur(scope.row, 'taxRate')"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('taxAmount')"
        :label="getFieldTitle('taxAmount')"
        prop="taxAmount"
        width="100"
        align="right"
        sortable
        :sort-method="sortByTaxAmount"
      >
        <template #default="scope">
          <span class="tax-amount">¥ {{ calcTaxAmount(scope.row) }}</span>
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('totalAmount')"
        :label="getFieldTitle('totalAmount')"
        prop="totalAmount"
        width="120"
        align="right"
        fixed="right"
        sortable
        :sort-method="sortByTotalAmount"
      >
        <template #default="scope">
          <span class="total-amount">¥ {{ calcTotalAmount(scope.row) }}</span>
        </template>
      </el-table-column>
    </BillDetailTable>

    <MaterialSelectDialog
      :visible="materialDialogVisible"
      @submit="handleMaterialSubmit"
      @cancel="handleMaterialDialogCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import BillDetailTable from '@/components/Bill/BillDetailTable.vue';
import {
  filterVisibleKeys,
  getFieldLabel,
  isPermissionReadonly,
  isPermissionVisible,
  isSameStringArray,
  normalizeVisibleKeys,
  type BillPermissionSet
} from '@/components/Bill/billPermission';
import MaterialSelectDialog from '@/components/SearchTable/MaterialSelectDialog.vue';
import { findMaterialByCode, type MaterialOption } from '@/components/SearchTable/materialMock.ts';
import BillTemplateColumnSetting from './BillTemplateColumnSetting.vue';
import { buildTextFilters, compareNumber, matchTextFilter } from './tableHelper';

interface BillTemplateItemRow {
  itemCode: string;
  itemName: string;
  spec: string;
  unit: string;
  qty: number | null;
  priceExcl: number | null;
  taxRate: number | null;
  note: string;
}

interface BillDetailTableExpose {
  scrollToBottom?: () => void;
  clearFilter?: (columnKeys?: string[]) => void;
  clearSort?: () => void;
  doLayout?: () => void;
  focusLastRowFirstEditable?: () => void;
  focusNextByTarget?: (target: HTMLElement) => boolean;
}

const props = defineProps<{
  modelValue: BillTemplateItemRow[];
  permissions?: BillPermissionSet;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: BillTemplateItemRow[]]
}>();

const defaultVisibleColumnKeys = ['itemCode', 'itemName', 'spec', 'unit', 'qty', 'priceExcl', 'taxRate', 'taxAmount', 'totalAmount'];
const filterableColumnKeys = ['itemCode', 'itemName', 'unit', 'taxRate'];
const defaultFieldLabels = {
  itemCode: '物料编码',
  itemName: '物料名称',
  spec: '规格型号',
  unit: '单位',
  qty: '数量',
  priceExcl: '不含税单价',
  taxRate: '税率%',
  taxAmount: '税额',
  totalAmount: '价税合计'
} as const;
const allColumnKeys = Object.keys(defaultFieldLabels) as Array<keyof typeof defaultFieldLabels>;

const billTableRef = ref<BillDetailTableExpose>();
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');
const materialDialogVisible = ref(false);
const currentMaterialRowIndex = ref(-1);
const originalValues = new WeakMap<BillTemplateItemRow, Record<string, number | null>>();

/**
 * 判断字段是否允许显示。
 */
const isFieldVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'fields', key);
};

/**
 * 判断字段是否处于只读状态。
 */
const isFieldReadonly = (key: string) => {
  return isPermissionReadonly(props.permissions, 'fields', key);
};

/**
 * 判断按钮是否允许显示。
 */
const isButtonVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'buttons', key);
};

/**
 * 判断按钮是否处于只读状态。
 */
const isButtonReadonly = (key: string) => {
  return isPermissionReadonly(props.permissions, 'buttons', key);
};

/**
 * 读取字段标题，优先使用权限返回的自定义标题。
 */
const getFieldTitle = (key: keyof typeof defaultFieldLabels) => {
  return getFieldLabel(props.permissions, key, defaultFieldLabels[key]);
};

/**
 * 判断当前列是否可显示。
 */
const canShowColumn = (key: string) => {
  return visibleColumnKeys.value.includes(key) && isFieldVisible(key);
};

/**
 * 根据字段权限动态生成列显示配置。
 */
const columnOptions = computed(() => {
  return allColumnKeys
    .filter((key) => {
      return isFieldVisible(key);
    })
    .map((key) => {
      return {
        key,
        label: getFieldTitle(key)
      };
    });
});

/**
 * 根据权限过滤默认可见列。
 */
const permissionDefaultVisibleColumnKeys = computed(() => {
  return filterVisibleKeys(defaultVisibleColumnKeys, props.permissions, 'fields');
});

const innerItems = computed({
  get: () => {
    return props.modelValue;
  },
  set: (value: BillTemplateItemRow[]) => {
    emit('update:modelValue', value);
  }
});

/**
 * 生成物料编码筛选项。
 */
const itemCodeFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.itemCode;
  });
});

/**
 * 生成物料名称筛选项。
 */
const itemNameFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.itemName;
  });
});

/**
 * 生成单位筛选项。
 */
const unitFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.unit;
  });
});

/**
 * 生成税率筛选项。
 */
const taxRateFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.taxRate;
  });
});

/**
 * 将输入值安全转换为数字。
 */
const getSafeVal = (val: string | number | null | undefined) => {
  const n = parseFloat(String(val ?? ''));
  return Number.isNaN(n) ? 0 : n;
};

/**
 * 聚焦输入框时暂存原值，便于数字输入完整覆盖。
 */
const handleFocus = (row: BillTemplateItemRow, field: keyof BillTemplateItemRow) => {
  if (!originalValues.has(row)) {
    originalValues.set(row, {});
  }
  originalValues.get(row)![String(field)] = row[field] as number | null;
  (row[field] as number | null) = null;
};

/**
 * 失焦时恢复空值字段，避免留下空数字。
 */
const handleBlur = (row: BillTemplateItemRow, field: keyof BillTemplateItemRow) => {
  if (row[field] === null || row[field] === undefined) {
    const original = originalValues.get(row)?.[String(field)];
    (row[field] as number | null) = original !== undefined && original !== null ? original : 0;
  }
};

/**
 * 计算单行税额。
 */
const calcTaxAmount = (row: BillTemplateItemRow) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (rate / 100)).toFixed(2);
};

/**
 * 计算单行价税合计。
 */
const calcTotalAmount = (row: BillTemplateItemRow) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (1 + rate / 100)).toLocaleString(undefined, { minimumFractionDigits: 2 });
};

/**
 * 按物料编码筛选数据。
 */
const filterByItemCode = (value: string, row: BillTemplateItemRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.itemCode;
  });
};

/**
 * 按物料名称筛选数据。
 */
const filterByItemName = (value: string, row: BillTemplateItemRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.itemName;
  });
};

/**
 * 按单位筛选数据。
 */
const filterByUnit = (value: string, row: BillTemplateItemRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.unit;
  });
};

/**
 * 按税率筛选数据。
 */
const filterByTaxRate = (value: string, row: BillTemplateItemRow) => {
  return matchTextFilter(value, row, (item) => {
    return item.taxRate;
  });
};

/**
 * 按数量排序。
 */
const sortByQty = (left: BillTemplateItemRow, right: BillTemplateItemRow) => {
  return compareNumber(left.qty, right.qty);
};

/**
 * 按不含税单价排序。
 */
const sortByPriceExcl = (left: BillTemplateItemRow, right: BillTemplateItemRow) => {
  return compareNumber(left.priceExcl, right.priceExcl);
};

/**
 * 按税率排序。
 */
const sortByTaxRate = (left: BillTemplateItemRow, right: BillTemplateItemRow) => {
  return compareNumber(left.taxRate, right.taxRate);
};

/**
 * 按税额排序。
 */
const sortByTaxAmount = (left: BillTemplateItemRow, right: BillTemplateItemRow) => {
  return compareNumber(calcTaxAmount(left), calcTaxAmount(right));
};

/**
 * 按价税合计排序。
 */
const sortByTotalAmount = (left: BillTemplateItemRow, right: BillTemplateItemRow) => {
  return compareNumber(calcTotalAmount(left), calcTotalAmount(right));
};

/**
 * 新增一行空明细。
 */
const handleAdd = () => {
  if (!isButtonVisible('add') || isButtonReadonly('add')) {
    return;
  }
  innerItems.value = [
    ...innerItems.value,
    { itemCode: '', itemName: '新物料', spec: '', unit: '', qty: 0, priceExcl: 0, taxRate: 13, note: '' }
  ];
};

/**
 * 记录当前排序列，便于列隐藏时清除排序。
 */
const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

/**
 * 打开物料选择弹窗。
 */
const showMaterialSelect = (index: number) => {
  if (!isButtonVisible('selectMaterial') || isButtonReadonly('selectMaterial') || isFieldReadonly('itemCode')) {
    return;
  }
  currentMaterialRowIndex.value = index;
  materialDialogVisible.value = true;
};

/**
 * 将选中的物料信息回填到当前行。
 */
const applyMaterialToRow = (row: BillTemplateItemRow, material: MaterialOption) => {
  row.itemCode = material.itemCode;
  row.itemName = material.itemName;
  row.spec = material.spec;
  row.unit = material.unit;
};

/**
 * 录入物料编码后按回车，尝试自动匹配 mock 物料。
 */
const handleItemCodeEnter = (row: BillTemplateItemRow, event: KeyboardEvent) => {
  if (isFieldReadonly('itemCode')) {
    return;
  }
  if (!row.itemCode.trim()) {
    return;
  }
  const material = findMaterialByCode(row.itemCode);
  if (!material) {
    row.itemName = '';
    row.spec = '';
    row.unit = '';
    ElMessage.warning('未找到对应物料，请检查物料编码或通过弹窗选择');
    return;
  }
  applyMaterialToRow(row, material);
  nextTick(() => {
    billTableRef.value?.focusNextByTarget?.(event.target as HTMLElement);
  });
};

/**
 * 关闭物料选择弹窗并重置当前行索引。
 */
const handleMaterialDialogCancel = () => {
  materialDialogVisible.value = false;
  currentMaterialRowIndex.value = -1;
};

/**
 * 提交物料选择结果并回填当前编辑行。
 */
const handleMaterialSubmit = (material: MaterialOption) => {
  const rowIndex = currentMaterialRowIndex.value;
  if (rowIndex < 0 || !innerItems.value[rowIndex]) {
    handleMaterialDialogCancel();
    return;
  }
  applyMaterialToRow(innerItems.value[rowIndex], material);
  handleMaterialDialogCancel();
};

/**
 * 当字段权限变化时，同步修正列显示设置。
 */
watch([columnOptions, permissionDefaultVisibleColumnKeys], () => {
  const allowedKeys = columnOptions.value.map((item) => {
    return item.key;
  });
  const nextVisibleColumnKeys = normalizeVisibleKeys(
    visibleColumnKeys.value,
    permissionDefaultVisibleColumnKeys.value,
    allowedKeys
  );
  if (!isSameStringArray(nextVisibleColumnKeys, visibleColumnKeys.value)) {
    visibleColumnKeys.value = nextVisibleColumnKeys;
  }
}, { immediate: true, deep: true });

/**
 * 当列被隐藏时，清理对应筛选和排序状态。
 */
watch(visibleColumnKeys, (value, oldValue) => {
  const hiddenFilterKeys = oldValue.filter((key) => {
    return !value.includes(key) && filterableColumnKeys.includes(key);
  });
  if (hiddenFilterKeys.length) {
    billTableRef.value?.clearFilter?.(hiddenFilterKeys);
  }
  const hiddenKeys = oldValue.filter((key) => {
    return !value.includes(key);
  });
  if (currentSortProp.value && hiddenKeys.includes(currentSortProp.value)) {
    billTableRef.value?.clearSort?.();
    currentSortProp.value = '';
  }
  nextTick(() => {
    billTableRef.value?.doLayout?.();
  });
}, { flush: 'sync' });
</script>

<style scoped lang="scss">
.tab-pane-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.total-amount {
  font-family: 'Consolas', monospace;
  font-weight: 700;
  color: #cf1322;
}

.tax-amount {
  color: #8c8c8c;
  font-size: 11px;
}
</style>
