<template>
  <div class="tab-pane-content">
    <BillDetailTable
      ref="billTableRef"
      v-model="innerItems"
      :permissions="permissions"
      @add="handleAdd"
      @sort-change="handleSortChange"
    >
      <template #toolbar-right>
        <PurchaseOrderColumnSetting
          v-model="visibleColumnKeys"
          :columns="columnOptions"
          :default-keys="permissionDefaultVisibleColumnKeys"
          :permissions="permissions"
        />
      </template>

      <el-table-column
        v-if="canShowColumn('itemCode')"
        column-key="itemCode"
        :label="permissionAccess.getFieldLabel('itemCode')"
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
            :disabled="permissionAccess.isFieldReadonly('itemCode')"
            data-enter-focus-stop
            @keydown.enter.stop.prevent="handleItemCodeEnter(scope.row, $event)"
          >
            <template v-if="permissionAccess.isButtonVisible('selectMaterial')" #append>
              <el-button
                icon="MoreFilled"
                :disabled="permissionAccess.isButtonReadonly('selectMaterial') || permissionAccess.isFieldReadonly('itemCode')"
                @click="showMaterialSelect(scope.$index)"
              />
            </template>
          </el-input>
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('itemName')"
        column-key="itemName"
        :label="permissionAccess.getFieldLabel('itemName')"
        prop="itemName"
        min-width="180"
        show-overflow-tooltip
        sortable
        :filters="itemNameFilters"
        :filter-method="filterByItemName"
      />

      <el-table-column
        v-if="canShowColumn('spec')"
        :label="permissionAccess.getFieldLabel('spec')"
        prop="spec"
        min-width="120"
        show-overflow-tooltip
        sortable
      />

      <el-table-column
        v-if="canShowColumn('unit')"
        column-key="unit"
        :label="permissionAccess.getFieldLabel('unit')"
        prop="unit"
        width="80"
        align="center"
        sortable
        :filters="unitFilters"
        :filter-method="filterByUnit"
      />

      <el-table-column
        v-if="canShowColumn('qty')"
        :label="permissionAccess.getFieldLabel('qty')"
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
            :disabled="permissionAccess.isFieldReadonly('qty')"
            size="small"
            style="width: 100%"
            @focus="handleFocus(scope.row, 'qty')"
            @blur="handleBlur(scope.row, 'qty')"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('priceExcl')"
        :label="permissionAccess.getFieldLabel('priceExcl')"
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
            :disabled="permissionAccess.isFieldReadonly('priceExcl')"
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
        :label="permissionAccess.getFieldLabel('taxRate')"
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
            :disabled="permissionAccess.isFieldReadonly('taxRate')"
            size="small"
            style="width: 100%"
            @focus="handleFocus(scope.row, 'taxRate')"
            @blur="handleBlur(scope.row, 'taxRate')"
          />
        </template>
      </el-table-column>

      <el-table-column
        v-if="canShowColumn('taxAmount')"
        :label="permissionAccess.getFieldLabel('taxAmount')"
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
        :label="permissionAccess.getFieldLabel('totalAmount')"
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
  createBillPermissionAccessor,
  isSameStringArray,
  normalizeVisibleKeys,
  type BillPermissionSource
} from '@/components/Bill/billPermission';
import MaterialSelectDialog from '@/components/SearchTable/MaterialSelectDialog.vue';
import { findMaterialByCode, type MaterialOption } from '@/components/SearchTable/materialMock';
import type { PurchaseOrderItem } from '@/api/purchaseOrder';
import { createDefaultPurchaseOrderItem } from '../purchaseOrderShared';
import PurchaseOrderColumnSetting from './PurchaseOrderColumnSetting.vue';
import { buildTextFilters, compareNumber, matchTextFilter } from './tableHelper';

interface BillDetailTableExpose {
  clearFilter?: (columnKeys?: string[]) => void;
  clearSort?: () => void;
  doLayout?: () => void;
}

const props = defineProps<{
  modelValue: PurchaseOrderItem[];
  permissions?: BillPermissionSource;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: PurchaseOrderItem[]]
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

const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

const billTableRef = ref<BillDetailTableExpose>();
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');
const materialDialogVisible = ref(false);
const currentMaterialRowIndex = ref(-1);
type EditableNumberField = 'qty' | 'priceExcl' | 'taxRate';
const originalValues = new WeakMap<PurchaseOrderItem, Partial<Record<EditableNumberField, number | null>>>();

const canShowColumn = (key: string) => {
  return visibleColumnKeys.value.includes(key) && permissionAccess.isFieldVisible(key);
};

const columnOptions = computed(() => {
  return allColumnKeys
    .filter((key) => {
      return permissionAccess.isFieldVisible(key);
    })
    .map((key) => {
      return {
        key,
        label: permissionAccess.getFieldLabel(key)
      };
    });
});

const permissionDefaultVisibleColumnKeys = computed(() => {
  return permissionAccess.filterVisibleFields(defaultVisibleColumnKeys);
});

const innerItems = computed({
  get: () => {
    return props.modelValue;
  },
  set: (value: PurchaseOrderItem[]) => {
    emit('update:modelValue', value);
  }
});

const itemCodeFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.itemCode;
  });
});

const itemNameFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.itemName;
  });
});

const unitFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.unit;
  });
});

const taxRateFilters = computed(() => {
  return buildTextFilters(props.modelValue, (item) => {
    return item.taxRate;
  });
});

const getSafeVal = (value: string | number | null | undefined) => {
  const result = parseFloat(String(value ?? ''));
  return Number.isNaN(result) ? 0 : result;
};

const handleFocus = (row: PurchaseOrderItem, field: EditableNumberField) => {
  if (!originalValues.has(row)) {
    originalValues.set(row, {});
  }
  originalValues.get(row)![field] = row[field];
  row[field] = null;
};

const handleBlur = (row: PurchaseOrderItem, field: EditableNumberField) => {
  if (row[field] === null || row[field] === undefined) {
    const original = originalValues.get(row)?.[field];
    row[field] = original !== undefined && original !== null ? original : 0;
  }
};

const calcTaxAmount = (row: PurchaseOrderItem) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (rate / 100)).toFixed(2);
};

const calcTotalAmount = (row: PurchaseOrderItem) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (1 + rate / 100)).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

const filterByItemCode = (value: string, row: PurchaseOrderItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.itemCode;
  });
};

const filterByItemName = (value: string, row: PurchaseOrderItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.itemName;
  });
};

const filterByUnit = (value: string, row: PurchaseOrderItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.unit;
  });
};

const filterByTaxRate = (value: string, row: PurchaseOrderItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.taxRate;
  });
};

const sortByQty = (left: PurchaseOrderItem, right: PurchaseOrderItem) => {
  return compareNumber(left.qty, right.qty);
};

const sortByPriceExcl = (left: PurchaseOrderItem, right: PurchaseOrderItem) => {
  return compareNumber(left.priceExcl, right.priceExcl);
};

const sortByTaxRate = (left: PurchaseOrderItem, right: PurchaseOrderItem) => {
  return compareNumber(left.taxRate, right.taxRate);
};

const sortByTaxAmount = (left: PurchaseOrderItem, right: PurchaseOrderItem) => {
  return compareNumber(calcTaxAmount(left), calcTaxAmount(right));
};

const sortByTotalAmount = (left: PurchaseOrderItem, right: PurchaseOrderItem) => {
  return compareNumber(calcTotalAmount(left), calcTotalAmount(right));
};

const handleAdd = () => {
  innerItems.value = [...innerItems.value, createDefaultPurchaseOrderItem()];
};

const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

const showMaterialSelect = (index: number) => {
  if (!permissionAccess.isButtonVisible('selectMaterial')
    || permissionAccess.isButtonReadonly('selectMaterial')
    || permissionAccess.isFieldReadonly('itemCode')) {
    return;
  }
  currentMaterialRowIndex.value = index;
  materialDialogVisible.value = true;
};

const applyMaterialToRow = (row: PurchaseOrderItem, material: MaterialOption) => {
  row.itemCode = material.itemCode;
  row.itemName = material.itemName;
  row.spec = material.spec;
  row.unit = material.unit;
};

const handleItemCodeEnter = (row: PurchaseOrderItem, event: KeyboardEvent) => {
  if (permissionAccess.isFieldReadonly('itemCode')) {
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
    const target = event.target as HTMLElement;
    if (target) {
      target.blur();
    }
  });
};

const handleMaterialDialogCancel = () => {
  materialDialogVisible.value = false;
  currentMaterialRowIndex.value = -1;
};

const handleMaterialSubmit = (material: MaterialOption) => {
  const rowIndex = currentMaterialRowIndex.value;
  if (rowIndex < 0 || !innerItems.value[rowIndex]) {
    handleMaterialDialogCancel();
    return;
  }
  applyMaterialToRow(innerItems.value[rowIndex], material);
  handleMaterialDialogCancel();
};

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
