<template>
  <div class="tab-pane-content table-tab-pane">
    <div class="table-toolbar">
      <PurchaseOrderColumnSetting
        v-model="visibleColumnKeys"
        :columns="columnOptions"
        :default-keys="permissionDefaultVisibleColumnKeys"
        :permissions="permissions"
      />
    </div>

    <div class="table-main">
      <el-table
        ref="tableRef"
        :data="rows"
        border
        size="small"
        height="100%"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />

        <el-table-column
          v-if="canShowColumn('sourceBillNo')"
          :label="permissionAccess.getFieldLabel('sourceBillNo')"
          prop="sourceBillNo"
          column-key="sourceBillNo"
          min-width="160"
          sortable
          :filters="sourceBillNoFilters"
          :filter-method="filterBySourceBillNo"
        />

        <el-table-column
          v-if="canShowColumn('sourceType')"
          :label="permissionAccess.getFieldLabel('sourceType')"
          prop="sourceType"
          column-key="sourceType"
          width="120"
          align="center"
          sortable
          :filters="sourceTypeFilters"
          :filter-method="filterBySourceType"
        >
          <template #default="scope">
            {{ getEnumName(scope.row.sourceType) || '-' }}
          </template>
        </el-table-column>

        <el-table-column
          v-if="canShowColumn('supplierName')"
          :label="permissionAccess.getFieldLabel('supplierName')"
          prop="supplierName"
          column-key="supplierName"
          min-width="160"
          show-overflow-tooltip
          sortable
          :filters="supplierFilters"
          :filter-method="filterBySupplier"
        />

        <el-table-column
          v-if="canShowColumn('billDate')"
          :label="permissionAccess.getFieldLabel('billDate')"
          prop="billDate"
          width="120"
          align="center"
          sortable
        />

        <el-table-column
          v-if="canShowColumn('totalAmount')"
          :label="permissionAccess.getFieldLabel('totalAmount')"
          prop="totalAmount"
          width="140"
          align="right"
          sortable
          :sort-method="sortByTotalAmount"
        >
          <template #default="scope">
            <span class="amount-text">¥ {{ Number(scope.row.totalAmount || 0).toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column
          v-if="canShowColumn('status')"
          :label="permissionAccess.getFieldLabel('status')"
          prop="status"
          column-key="status"
          width="100"
          align="center"
          sortable
          :filters="statusFilters"
          :filter-method="filterByStatus"
        >
          <template #default="scope">
            <el-tag size="small" :type="getEnumName(scope.row.status) === '已审核' ? 'success' : 'info'">
              {{ getEnumName(scope.row.status) || '-' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column
          v-if="canShowColumn('remark')"
          :label="permissionAccess.getFieldLabel('remark')"
          prop="remark"
          min-width="180"
          show-overflow-tooltip
          sortable
        />
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue';
import { type TableInstance } from 'element-plus';
import {
  createBillPermissionAccessor,
  isSameStringArray,
  normalizeVisibleKeys,
  type BillPermissionSource
} from '@/components/Bill/billPermission';
import type { PurchaseOrderSourceBill } from '@/api/purchaseOrder';
import { getEnumName } from '@/utils/enum';
import PurchaseOrderColumnSetting from './PurchaseOrderColumnSetting.vue';
import { buildTextFilters, compareNumber, matchTextFilter } from './tableHelper';

const props = defineProps<{
  rows: PurchaseOrderSourceBill[];
  permissions?: BillPermissionSource;
}>();

const defaultVisibleColumnKeys = ['sourceBillNo', 'sourceType', 'supplierName', 'billDate', 'totalAmount', 'status', 'remark'];
const filterableColumnKeys = ['sourceBillNo', 'sourceType', 'supplierName', 'status'];
const defaultFieldLabels = {
  sourceBillNo: '来源单号',
  sourceType: '来源类型',
  supplierName: '供应商',
  billDate: '单据日期',
  totalAmount: '价税合计',
  status: '状态',
  remark: '备注'
} as const;
const allColumnKeys = Object.keys(defaultFieldLabels) as Array<keyof typeof defaultFieldLabels>;

const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

const tableRef = ref<TableInstance>();
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');

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

const sourceBillNoFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.sourceBillNo;
  });
});

const sourceTypeFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return getEnumName(item.sourceType);
  });
});

const supplierFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return item.supplierName;
  });
});

const statusFilters = computed(() => {
  return buildTextFilters(props.rows, (item) => {
    return getEnumName(item.status);
  });
});

const filterBySourceBillNo = (value: string, row: PurchaseOrderSourceBill) => {
  return matchTextFilter(value, row, (item) => {
    return item.sourceBillNo;
  });
};

const filterBySourceType = (value: string, row: PurchaseOrderSourceBill) => {
  return matchTextFilter(value, row, (item) => {
    return getEnumName(item.sourceType);
  });
};

const filterBySupplier = (value: string, row: PurchaseOrderSourceBill) => {
  return matchTextFilter(value, row, (item) => {
    return item.supplierName;
  });
};

const filterByStatus = (value: string, row: PurchaseOrderSourceBill) => {
  return matchTextFilter(value, row, (item) => {
    return getEnumName(item.status);
  });
};

const sortByTotalAmount = (left: PurchaseOrderSourceBill, right: PurchaseOrderSourceBill) => {
  return compareNumber(left.totalAmount, right.totalAmount);
};

const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
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
    tableRef.value?.clearFilter(hiddenFilterKeys);
  }
  const hiddenKeys = oldValue.filter((key) => {
    return !value.includes(key);
  });
  if (currentSortProp.value && hiddenKeys.includes(currentSortProp.value)) {
    tableRef.value?.clearSort();
    currentSortProp.value = '';
  }
  nextTick(() => {
    tableRef.value?.doLayout();
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

.table-tab-pane {
  padding: 12px 16px;
  box-sizing: border-box;
}

.table-toolbar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 12px;
}

.table-main {
  flex: 1;
  min-height: 0;
}

.amount-text {
  font-family: 'Consolas', monospace;
  font-weight: 700;
  color: #cf1322;
}
</style>
