<template>
  <div class="app-container purchase-order-index">
    <el-row :gutter="8" class="stat-row">
      <el-col v-for="card in overviewCards" :key="card.key" :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon" :class="card.theme">
            <el-icon><component :is="overviewIconMap[card.icon]" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">
              {{ card.value }}
              <span v-if="card.unit" class="stat-unit">{{ card.unit }}</span>
            </div>
          </div>
          <div class="stat-footer">
            <template v-if="card.trendValue">
              {{ card.footerText }}
              <span :class="card.trendDirection === 'down' ? 'trend-down' : 'trend-up'">
                {{ card.trendValue }}
                <el-icon><component :is="card.trendDirection === 'down' ? CaretBottom : CaretTop" /></el-icon>
              </span>
            </template>
            <template v-else>{{ card.footerText }}</template>
          </div>
        </div>
      </el-col>
    </el-row>

    <div class="tech-card search-wrapper">
      <el-form :model="queryParams" label-width="80px">
        <el-row :gutter="20">
          <el-col v-if="queryFieldConfigMap.billNo.visible" :span="6">
            <el-form-item :label="queryFieldConfigMap.billNo.label">
              <el-input
                v-model="queryParams.billNo"
                :placeholder="queryFieldConfigMap.billNo.placeholder"
                clearable
                @keyup.enter="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="queryFieldConfigMap.supplierId.visible" :span="6">
            <el-form-item :label="queryFieldConfigMap.supplierId.label">
              <el-select
                v-model="queryParams.supplierId"
                :placeholder="queryFieldConfigMap.supplierId.placeholder"
                clearable
                style="width: 100%"
              >
                <el-option v-for="item in supplierOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="queryFieldConfigMap.billDate.visible" :span="6">
            <el-form-item :label="queryFieldConfigMap.billDate.label">
              <el-date-picker
                v-model="billDateRange"
                type="daterange"
                range-separator="-"
                start-placeholder="开始"
                end-placeholder="结束"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>

          <template v-if="showMoreSearch">
            <el-col v-if="queryFieldConfigMap.userName.visible" :span="6">
              <el-form-item :label="queryFieldConfigMap.userName.label">
                <el-input
                  v-model="queryParams.userName"
                  :placeholder="queryFieldConfigMap.userName.placeholder"
                  clearable
                  @keyup.enter="handleQuery"
                />
              </el-form-item>
            </el-col>
            <el-col v-if="queryFieldConfigMap.warehouseId.visible" :span="6">
              <el-form-item :label="queryFieldConfigMap.warehouseId.label">
                <el-select
                  v-model="queryParams.warehouseId"
                  :placeholder="queryFieldConfigMap.warehouseId.placeholder"
                  clearable
                  style="width: 100%"
                >
                  <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </template>

          <el-col :span="6" class="search-actions">
            <el-form-item label-width="0">
              <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button link @click="showMoreSearch = !showMoreSearch">
                {{ showMoreSearch ? '收起' : '更多' }}
                <el-icon class="el-icon--right"><component :is="showMoreSearch ? CaretTop : CaretBottom" /></el-icon>
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="tech-card table-wrapper">
      <div class="status-tabs-container">
        <el-tabs v-model="queryParams.status" class="custom-status-tabs" @tab-change="handleStatusTabChange">
          <el-tab-pane
            v-for="item in purchaseOrderStatusOptions"
            :key="item.value || 'all'"
            :label="item.label"
            :name="item.value"
          />
        </el-tabs>
      </div>

      <div class="table-operations">
        <div class="left-ops">
          <el-button
            v-if="buttonConfigMap.create.visible"
            type="primary"
            icon="Plus"
            :disabled="buttonConfigMap.create.disabled"
            @click="handleAdd"
          >
            {{ buttonConfigMap.create.label }}
          </el-button>
          <el-divider direction="vertical" />
          <el-button icon="Check" :disabled="!selectedRows.length" @click="handleBatchAudit">批量审核</el-button>
          <el-button icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
        <div class="right-ops">
          <el-button
            v-if="buttonConfigMap.export.visible"
            icon="Download"
            :disabled="buttonConfigMap.export.disabled || !billList.length"
            @click="handleExport"
          >
            {{ buttonConfigMap.export.label }}
          </el-button>
          <el-tooltip content="刷新数据" placement="top">
            <el-button circle icon="RefreshRight" @click="handleRefresh" />
          </el-tooltip>
          <PurchaseOrderColumnSetting
            v-model="visibleColumnKeys"
            :columns="columnOptions"
            :default-keys="permissionDefaultVisibleColumnKeys"
            :permissions="columnSettingPermissions"
          />
        </div>
      </div>

      <div class="table-content">
        <el-table
          ref="tableRef"
          v-loading="loading"
          :data="billList"
          border
          stripe
          highlight-current-row
          height="100%"
          class="main-table"
          @selection-change="handleSelectionChange"
          @sort-change="handleSortChange"
        >
          <el-table-column type="selection" width="45" align="center" fixed="left" />
          <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
          <el-table-column
            v-if="canShowColumn('billNo')"
            :label="listFieldConfigMap.billNo.label"
            prop="billNo"
            column-key="billNo"
            min-width="180"
            fixed="left"
            sortable
            :filters="billNoFilters"
            :filter-method="filterByBillNo"
          >
            <template #default="{ row }">
              <el-link type="primary" class="bill-link" @click="handleView(row)">{{ row.billNo }}</el-link>
            </template>
          </el-table-column>
          <el-table-column
            v-if="canShowColumn('billDate')"
            :label="listFieldConfigMap.billDate.label"
            prop="billDate"
            width="120"
            align="center"
            sortable
          />
          <el-table-column
            v-if="canShowColumn('supplierName')"
            :label="listFieldConfigMap.supplierName.label"
            prop="supplierName"
            column-key="supplierName"
            min-width="180"
            show-overflow-tooltip
            sortable
            :filters="supplierFilters"
            :filter-method="filterBySupplier"
          />
          <el-table-column
            v-if="canShowColumn('userName')"
            :label="listFieldConfigMap.userName.label"
            prop="userName"
            column-key="userName"
            width="120"
            align="center"
            sortable
            :filters="userNameFilters"
            :filter-method="filterByUserName"
          />
          <el-table-column
            v-if="canShowColumn('warehouseName')"
            :label="listFieldConfigMap.warehouseName.label"
            prop="warehouseName"
            column-key="warehouseName"
            min-width="160"
            show-overflow-tooltip
            sortable
            :filters="warehouseFilters"
            :filter-method="filterByWarehouse"
          />
          <el-table-column
            v-if="canShowColumn('totalAmount')"
            :label="listFieldConfigMap.totalAmount.label"
            prop="totalAmount"
            width="140"
            align="right"
            sortable
            :sort-method="sortByTotalAmount"
          >
            <template #default="{ row }">
              <span class="price-text">¥ {{ formatAmount(row.totalAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            v-if="canShowColumn('status')"
            :label="listFieldConfigMap.status.label"
            prop="status"
            column-key="status"
            align="center"
            width="100"
            sortable
            :sort-method="sortByStatus"
            :filters="statusFilters"
            :filter-method="filterByStatus"
          >
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.statusName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" fixed="right" width="200">
            <template #default="{ row }">
              <div class="row-ops">
                <el-button
                  v-if="buttonConfigMap.detail.visible"
                  link
                  type="primary"
                  :disabled="buttonConfigMap.detail.disabled"
                  @click="handleView(row)"
                >
                  {{ buttonConfigMap.detail.label }}
                </el-button>
                <el-button
                  v-if="buttonConfigMap.edit.visible"
                  link
                  type="primary"
                  :disabled="buttonConfigMap.edit.disabled"
                  @click="handleEdit(row)"
                >
                  {{ buttonConfigMap.edit.label }}
                </el-button>
                <el-dropdown trigger="click" @command="handleRowMoreCommand($event, row)">
                  <el-button link type="primary">
                    <el-icon><MoreFilled /></el-icon>
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item command="print">打印</el-dropdown-item>
                      <el-dropdown-item command="void" type="danger">作废</el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handlePageSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <FooterSummaryCard :items="listSummary" :more-items="listMoreSummary" class="natural-footer" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { CaretBottom, CaretTop, Clock, Document, Finished, Money, MoreFilled } from '@element-plus/icons-vue';
import { ElMessage, type TableInstance } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import {
  type BillPermissionSet,
  isSameStringArray,
  normalizeVisibleKeys
} from '@/components/Bill/billPermission';
import FooterSummaryCard, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import type {
  PurchaseOrderListItem,
  PurchaseOrderOverviewCard,
  PurchaseOrderPageQuery,
  PurchaseOrderStatusCode
} from '@/api/purchaseOrder';
import { getPurchaseOrderContext, getPurchaseOrderPage } from '@/api/purchaseOrder';
import { getEnumCode } from '@/utils/enum';
import { buildResolvedButtonConfigMap, buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import PurchaseOrderColumnSetting from './components/PurchaseOrderColumnSetting.vue';
import { buildTextFilters, compareNumber, matchTextFilter } from './components/tableHelper';
import { getStatusType, purchaseOrderStatusOptions, supplierOptions, warehouseOptions } from './purchaseOrderShared';

type QueryFieldCode = 'billNo' | 'supplierId' | 'billDate' | 'userName' | 'warehouseId';
type ListFieldCode = 'billNo' | 'billDate' | 'supplierName' | 'userName' | 'warehouseName' | 'totalAmount' | 'status';
type PageButtonCode = 'create' | 'export' | 'detail' | 'edit';

const router = useRouter();
const tableRef = ref<TableInstance>();
const loading = ref(false);
const showMoreSearch = ref(false);
const context = ref<ModuleContext | null>(null);
const overviewCards = ref<PurchaseOrderOverviewCard[]>([]);
const billList = ref<PurchaseOrderListItem[]>([]);
const total = ref(0);
const selectedRows = ref<PurchaseOrderListItem[]>([]);
const billDateRange = ref<string[]>([]);
const currentSortProp = ref('');

const defaultVisibleColumnKeys: ListFieldCode[] = ['billNo', 'billDate', 'supplierName', 'userName', 'warehouseName', 'totalAmount', 'status'];
const filterableColumnKeys: ListFieldCode[] = ['billNo', 'supplierName', 'userName', 'warehouseName', 'status'];
const visibleColumnKeys = ref<string[]>([...defaultVisibleColumnKeys]);

const queryParams = reactive<PurchaseOrderPageQuery>({
  pageNum: 1,
  pageSize: 20,
  billNo: '',
  supplierId: null,
  status: '',
  startBillDate: '',
  endBillDate: '',
  userName: '',
  warehouseId: null
});

const queryFieldFallbackMap: Record<QueryFieldCode, { label: string; placeholder?: string }> = {
  billNo: { label: '单据编号', placeholder: '请输入单据编号' },
  supplierId: { label: '供应商', placeholder: '请选择供应商' },
  billDate: { label: '单据日期', placeholder: '请选择单据日期' },
  userName: { label: '业务员', placeholder: '请输入业务员' },
  warehouseId: { label: '仓库', placeholder: '请选择仓库' }
};

const listFieldFallbackMap: Record<ListFieldCode, { label: string }> = {
  billNo: { label: '单据编号' },
  billDate: { label: '单据日期' },
  supplierName: { label: '供应商' },
  userName: { label: '业务员' },
  warehouseName: { label: '仓库' },
  totalAmount: { label: '价税合计' },
  status: { label: '状态' }
};

const buttonFallbackMap: Record<PageButtonCode, { label: string; visible?: boolean; disabled?: boolean }> = {
  create: { label: '新增' },
  export: { label: '导出' },
  detail: { label: '详情' },
  edit: { label: '编辑' }
};

const columnSettingPermissions = computed<BillPermissionSet>(() => {
  return {
    buttons: {
      columnSetting: {
        disabled: false,
        label: '列显示',
        visible: true
      }
    },
    fields: {}
  };
});

const queryFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'query', queryFieldFallbackMap);
});

const listFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'list', listFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(context.value?.fieldConfigs || {}, buttonFallbackMap);
});

const columnOptions = computed(() => {
  return (Object.keys(listFieldFallbackMap) as ListFieldCode[])
    .filter((key) => {
      return listFieldConfigMap.value[key].visible;
    })
    .map((key) => {
      return {
        key,
        label: listFieldConfigMap.value[key].label
      };
    });
});

const permissionDefaultVisibleColumnKeys = computed(() => {
  return defaultVisibleColumnKeys.filter((key) => {
    return listFieldConfigMap.value[key].visible;
  });
});

const billNoFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.billNo;
  });
});

const supplierFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.supplierName;
  });
});

const userNameFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.userName;
  });
});

const warehouseFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.warehouseName;
  });
});

const statusFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.statusName;
  });
});

const listSummary = computed<SummaryItem[]>(() => {
  const totalAmount = billList.value.reduce((sum, item) => {
    return sum + Number(item.totalAmount || 0);
  }, 0);
  const totalQty = billList.value.reduce((sum, item) => {
    return sum + Number(item.totalQty || 0);
  }, 0);
  return [
    { label: '价税合计', value: formatAmount(totalAmount), type: 'amount', isMoney: true },
    { label: '合计数量', value: totalQty.toFixed(2) }
  ];
});

const listMoreSummary = computed<SummaryItem[]>(() => {
  const netAmount = billList.value.reduce((sum, item) => {
    return sum + Number(item.netAmount || 0);
  }, 0);
  const taxAmount = billList.value.reduce((sum, item) => {
    return sum + Number(item.taxAmount || 0);
  }, 0);
  return [
    { label: '不含税金额', value: formatAmount(netAmount), isMoney: true },
    { label: '总税额', value: formatAmount(taxAmount), isMoney: true },
    { label: '当前页单据数', value: billList.value.length }
  ];
});

const overviewIconMap = {
  clock: Clock,
  document: Document,
  finished: Finished,
  money: Money
} as const;

const statusOrderMap: Record<PurchaseOrderStatusCode, number> = {
  unaudited: 1,
  pre_audited: 2,
  audited: 3,
  shipped: 4,
  completed: 5
};

const canShowColumn = (key: ListFieldCode) => {
  return visibleColumnKeys.value.includes(key) && listFieldConfigMap.value[key].visible;
};

const formatAmount = (value: number | null | undefined) => {
  return Number(value || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

const loadContext = async () => {
  const res = await getPurchaseOrderContext();
  context.value = res.data;
};

const loadPage = async () => {
  loading.value = true;
  try {
    queryParams.startBillDate = billDateRange.value[0] || '';
    queryParams.endBillDate = billDateRange.value[1] || '';
    const res = await getPurchaseOrderPage(queryParams);
    overviewCards.value = res.data.overviewCards || [];
    billList.value = res.data.page.list || [];
    total.value = res.data.page.total || 0;
    selectedRows.value = [];
  } finally {
    loading.value = false;
  }
};

const handleQuery = async () => {
  queryParams.pageNum = 1;
  await loadPage();
};

const handleRefresh = async () => {
  await loadPage();
};

const handlePageChange = async () => {
  await loadPage();
};

const handlePageSizeChange = async () => {
  queryParams.pageNum = 1;
  await loadPage();
};

const handleStatusTabChange = async () => {
  queryParams.pageNum = 1;
  await loadPage();
};

const resetQuery = async () => {
  queryParams.pageNum = 1;
  queryParams.pageSize = 20;
  queryParams.billNo = '';
  queryParams.supplierId = null;
  queryParams.status = '';
  queryParams.startBillDate = '';
  queryParams.endBillDate = '';
  queryParams.userName = '';
  queryParams.warehouseId = null;
  billDateRange.value = [];
  await loadPage();
};

const handleAdd = () => {
  if (!buttonConfigMap.value.create.visible || buttonConfigMap.value.create.disabled) {
    return;
  }
  router.push('/purchase/order/create');
};

const handleView = (row: PurchaseOrderListItem) => {
  router.push({ path: '/purchase/order/detail', query: { id: row.id } });
};

const handleEdit = (row: PurchaseOrderListItem) => {
  if (!buttonConfigMap.value.edit.visible || buttonConfigMap.value.edit.disabled) {
    return;
  }
  router.push({ path: '/purchase/order/detail', query: { id: row.id, mode: 'edit' } });
};

const handleBatchAudit = () => {
  if (!selectedRows.value.length) {
    return;
  }
  ElMessage.info('批量审核功能待接入');
};

const handleBatchDelete = () => {
  if (!selectedRows.value.length) {
    return;
  }
  ElMessage.info('批量删除功能待接入');
};

const handleRowMoreCommand = (command: string, row: PurchaseOrderListItem) => {
  if (command === 'print') {
    ElMessage.info(`打印功能待接入，单据号：${row.billNo}`);
    return;
  }
  if (command === 'void') {
    ElMessage.info(`作废功能待接入，单据号：${row.billNo}`);
  }
};

const handleSelectionChange = (rows: PurchaseOrderListItem[]) => {
  selectedRows.value = rows;
};

const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

const filterByBillNo = (value: string, row: PurchaseOrderListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.billNo;
  });
};

const filterBySupplier = (value: string, row: PurchaseOrderListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.supplierName;
  });
};

const filterByUserName = (value: string, row: PurchaseOrderListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.userName;
  });
};

const filterByWarehouse = (value: string, row: PurchaseOrderListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.warehouseName;
  });
};

const filterByStatus = (value: string, row: PurchaseOrderListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.statusName;
  });
};

const sortByTotalAmount = (left: PurchaseOrderListItem, right: PurchaseOrderListItem) => {
  return compareNumber(left.totalAmount, right.totalAmount);
};

const sortByStatus = (left: PurchaseOrderListItem, right: PurchaseOrderListItem) => {
  return compareNumber(statusOrderMap[getEnumCode(left.status) || 'unaudited'], statusOrderMap[getEnumCode(right.status) || 'unaudited']);
};

const handleExport = () => {
  if (!buttonConfigMap.value.export.visible || buttonConfigMap.value.export.disabled || !billList.value.length) {
    return;
  }
  const rows = [
    ['单据编号', '单据日期', '供应商', '业务员', '仓库', '价税合计', '状态'].join(','),
    ...billList.value.map((item) => {
      return [
        item.billNo,
        item.billDate,
        item.supplierName,
        item.userName,
        item.warehouseName,
        item.totalAmount,
        item.statusName
      ].join(',');
    })
  ];
  const blob = new Blob([`\ufeff${rows.join('\n')}`], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = 'purchase-order.csv';
  link.click();
  URL.revokeObjectURL(url);
  ElMessage.success('导出成功');
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
    return !value.includes(key) && filterableColumnKeys.includes(key as ListFieldCode);
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

onMounted(async () => {
  await Promise.all([loadContext(), loadPage()]);
});
</script>

<style scoped lang="scss">
.purchase-order-index {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  gap: 8px;
  overflow: hidden;
}

.stat-row {
  flex-shrink: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 16px;
  position: relative;
  height: 90px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 16px;

  &.primary {
    background: rgba(22, 119, 255, 0.1);
    color: #1677ff;
  }

  &.warning {
    background: rgba(250, 173, 20, 0.1);
    color: #faad14;
  }

  &.danger {
    background: rgba(255, 77, 79, 0.1);
    color: #ff4d4f;
  }

  &.success {
    background: rgba(82, 196, 26, 0.1);
    color: #52c41a;
  }
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 13px;
  color: #8c8c8c;
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
}

.stat-unit {
  font-size: 12px;
  font-weight: 400;
  color: #8c8c8c;
}

.stat-footer {
  position: absolute;
  bottom: 8px;
  right: 16px;
  font-size: 12px;
  color: #bfbfbf;

  .trend-up {
    color: #52c41a;
  }

  .trend-down {
    color: #ff4d4f;
  }
}

.search-wrapper {
  flex-shrink: 0;
  padding: 16px 16px 4px;
}

.search-actions {
  text-align: right;
}

.table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 0 !important;
  overflow: hidden;
}

.status-tabs-container {
  flex-shrink: 0;
  padding: 0 16px;
  border-bottom: 1px solid #f0f0f0;

  :deep(.el-tabs__header) {
    margin: 0;
    border-bottom: none;
  }
}

.table-operations {
  flex-shrink: 0;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-ops,
.right-ops,
.row-ops {
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-content {
  flex: 1;
  min-height: 0;
  padding: 0 16px;
  overflow: hidden;
}

.bill-link {
  font-weight: 600;
}

.price-text {
  font-family: 'Consolas', monospace;
  font-weight: 600;
  color: #cf1322;
}

.pagination-container {
  flex-shrink: 0;
  padding: 12px 16px;
  background: #fff;
  display: flex;
  justify-content: flex-end;
}

:deep(.natural-footer) {
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  border: 1px solid var(--app-border-color);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);

  .footer-summary-container {
    border-top: none;
  }

  .summary-wrapper {
    padding: 0 16px;
  }

  .summary-main {
    height: 44px;
  }
}
</style>


