<template>
  <div class="app-container">
    <!-- 1. 顶部操作看板 -->
    <el-row :gutter="8" class="stat-row">
      <el-col v-for="card in overviewCards" :key="card.key" :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon" :class="card.theme">
            <el-icon><component :is="overviewIconMap[card.icon]" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-label">{{ card.label }}</div>
            <div class="stat-value">{{ card.value }} <span v-if="card.unit" class="stat-unit">{{ card.unit }}</span></div>
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

    <!-- 2. 搜索过滤区域 -->
    <div class="tech-card search-wrapper">
      <el-form ref="queryRef" :model="queryParams" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="单据编号" prop="billNo">
              <el-input v-model="queryParams.billNo" placeholder="单号/外部订单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="供应商" prop="supplier">
              <el-select v-model="queryParams.supplier" placeholder="请选择" clearable style="width: 100%">
                <el-option v-for="item in supplierOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="单据日期" prop="billDate">
              <el-date-picker
                v-model="queryParams.billDate"
                type="daterange"
                range-separator="-"
                start-placeholder="开始"
                end-placeholder="结束"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>

          <template v-if="showMoreSearch">
            <el-col :span="6">
              <el-form-item label="业务员" prop="userName"><el-input v-model="queryParams.userName" clearable @keyup.enter="handleQuery" /></el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="收货仓库" prop="warehouse">
                <el-select v-model="queryParams.warehouse" clearable style="width: 100%">
                  <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </template>

          <el-col :span="showMoreSearch ? 6 : 6" class="search-actions">
            <el-form-item label-width="0">
              <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
              <el-button link @click="showMoreSearch = !showMoreSearch">
                {{ showMoreSearch ? '收起' : '更多' }}
                <el-icon class="el-icon--right"><component :is="showMoreSearch ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 3. 数据表格卡片 -->
    <div class="tech-card table-wrapper">
      <div class="status-tabs-container">
        <el-tabs v-model="queryParams.status" @tab-change="handleQuery" class="custom-status-tabs">
          <el-tab-pane label="全部单据" name="" />
          <el-tab-pane label="未审核" name="0" />
          <el-tab-pane label="已预审" name="1" />
          <el-tab-pane label="已审核" name="2" />
          <el-tab-pane label="已发货" name="3" />
          <el-tab-pane label="已完成" name="4" />
        </el-tabs>
      </div>

      <div class="table-operations">
        <div class="left-ops">
          <el-button type="primary" icon="Plus" @click="handleAdd">新建采购单</el-button>
          <el-divider direction="vertical" />
          <el-button icon="Check" :disabled="!selectedRows.length" @click="handleBatchAudit">批量审核</el-button>
          <el-button icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
        <div class="right-ops">
          <el-tooltip content="刷新数据" placement="top"><el-button circle icon="RefreshRight" @click="handleRefresh" /></el-tooltip>
          <BillTemplateColumnSetting
            v-model="visibleColumnKeys"
            :columns="columnOptions"
            :default-keys="defaultVisibleColumnKeys"
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
            v-if="visibleColumnKeys.includes('billNo')"
            label="单据编号"
            prop="billNo"
            column-key="billNo"
            min-width="160"
            fixed="left"
            sortable
            :filters="billNoFilters"
            :filter-method="filterByBillNo"
          >
            <template #default="scope">
              <el-link type="primary" class="bill-link" @click="handleView(scope.row)">{{ scope.row.billNo }}</el-link>
            </template>
          </el-table-column>
          <el-table-column
            v-if="visibleColumnKeys.includes('billType')"
            label="单据类型"
            prop="billType"
            column-key="billType"
            width="120"
            align="center"
            sortable
            :filters="billTypeFilters"
            :filter-method="filterByBillType"
          />
          <el-table-column
            v-if="visibleColumnKeys.includes('supplierName')"
            label="供应商"
            prop="supplierName"
            column-key="supplierName"
            show-overflow-tooltip
            min-width="180"
            sortable
            :filters="supplierFilters"
            :filter-method="filterBySupplier"
          />
          <el-table-column
            v-if="visibleColumnKeys.includes('totalAmount')"
            label="金额"
            prop="totalAmount"
            width="140"
            align="right"
            sortable
            :sort-method="sortByTotalAmount"
          >
            <template #default="scope"><span class="price-text">¥ {{ scope.row.totalAmount }}</span></template>
          </el-table-column>
          <el-table-column
            v-if="visibleColumnKeys.includes('status')"
            label="状态"
            prop="status"
            column-key="status"
            align="center"
            width="100"
            sortable
            :sort-method="sortByStatus"
            :filters="statusFilters"
            :filter-method="filterByStatus"
          >
            <template #default="scope"><el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusName }}</el-tag></template>
          </el-table-column>
          <el-table-column label="操作" align="center" fixed="right" width="200">
            <template #default="scope">
              <div class="row-ops">
                <el-button link type="primary" @click="handleView(scope.row)">详情</el-button>
                <el-button link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
                <el-dropdown trigger="click" @command="handleRowMoreCommand($event, scope.row)">
                  <el-button link type="primary" style="margin-left: 8px"><el-icon><MoreFilled /></el-icon></el-button>
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

    <!-- 4. 合计栏回归自然流：直接跟在表格卡片下方 -->
    <FooterSummary :items="listSummary" :more-items="listMoreSummary" class="natural-footer" />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { CaretBottom, CaretTop, Clock, Document, Finished, Money, MoreFilled } from '@element-plus/icons-vue';
import { ElMessage, type FormInstance, type TableInstance } from 'element-plus';
import FooterSummary, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import { useBillTemplateStore } from '@/store/modules/billTemplate';
import BillTemplateColumnSetting from './components/BillTemplateColumnSetting.vue';
import {
  fetchBillTemplateListData,
  type BillTemplateListItem,
  type BillTemplateOverviewCard,
  type BillTemplateOverviewIcon
} from './billDataMock';
import { buildTextFilters, compareNumber, matchTextFilter } from './components/tableHelper';

const router = useRouter();
const billTemplateStore = useBillTemplateStore();
const queryRef = ref<FormInstance>();
const tableRef = ref<TableInstance>();
const loading = ref(false);
const showMoreSearch = ref(false);
const selectedRows = ref<BillTemplateListItem[]>([]);
const overviewCards = ref<BillTemplateOverviewCard[]>([]);
const allBillList = ref<BillTemplateListItem[]>([]);
const filteredBillList = ref<BillTemplateListItem[]>([]);
const billList = ref<BillTemplateListItem[]>([]);
const defaultVisibleColumnKeys = ['billNo', 'billType', 'supplierName', 'totalAmount', 'status'];
const filterableColumnKeys = ['billNo', 'billType', 'supplierName', 'status'];
const columnOptions = [
  { key: 'billNo', label: '单据编号' },
  { key: 'billType', label: '单据类型' },
  { key: 'supplierName', label: '供应商' },
  { key: 'totalAmount', label: '金额' },
  { key: 'status', label: '状态' }
];
const visibleColumnKeys = ref([...defaultVisibleColumnKeys]);
const currentSortProp = ref('');
const overviewIconMap: Record<BillTemplateOverviewIcon, any> = {
  document: Document,
  clock: Clock,
  money: Money,
  finished: Finished
};

/**
 * 将金额字符串安全转换为数字。
 */
const getSafeAmount = (value: string | number) => {
  const amount = Number(String(value).replace(/,/g, ''));
  return Number.isFinite(amount) ? amount : 0;
};

/**
 * 格式化金额展示。
 */
const formatAmount = (value: number) => {
  return value.toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  });
};

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  billNo: '',
  supplier: '',
  status: '',
  billDate: [] as string[],
  userName: '',
  warehouse: ''
});

/**
 * 生成供应商下拉选项。
 */
const supplierOptions = computed(() => {
  const optionMap = new Map<string, string>();
  allBillList.value.forEach((item) => {
    optionMap.set(item.supplierId, item.supplierName);
  });
  return Array.from(optionMap.entries()).map(([value, label]) => {
    return { value, label };
  });
});

/**
 * 生成仓库下拉选项。
 */
const warehouseOptions = computed(() => {
  const optionMap = new Map<string, string>();
  allBillList.value.forEach((item) => {
    optionMap.set(item.warehouseId, item.warehouseName);
  });
  return Array.from(optionMap.entries()).map(([value, label]) => {
    return { value, label };
  });
});

/**
 * 生成单据编号筛选项。
 */
const billNoFilters = computed(() => {
  return buildTextFilters(filteredBillList.value, (item) => {
    return item.billNo;
  });
});

/**
 * 生成单据类型筛选项。
 */
const billTypeFilters = computed(() => {
  return buildTextFilters(filteredBillList.value, (item) => {
    return item.billType;
  });
});

/**
 * 生成供应商筛选项。
 */
const supplierFilters = computed(() => {
  return buildTextFilters(filteredBillList.value, (item) => {
    return item.supplierName;
  });
});

/**
 * 生成状态筛选项。
 */
const statusFilters = computed(() => {
  return buildTextFilters(filteredBillList.value, (item) => {
    return item.statusName;
  });
});

/**
 * 计算筛选后的总数。
 */
const total = computed(() => {
  return filteredBillList.value.length;
});

/**
 * 计算列表主合计。
 */
const listSummary = computed<SummaryItem[]>(() => {
  let totalAmount = 0;
  let totalQty = 0;

  billList.value.forEach((item) => {
    totalAmount += getSafeAmount(item.totalAmount);
    totalQty += item.totalQty;
  });

  return [
    { label: '价税合计', value: formatAmount(totalAmount), type: 'amount', isMoney: true },
    { label: '合计数量', value: totalQty.toFixed(2) }
  ];
});

/**
 * 计算列表扩展合计。
 */
const listMoreSummary = computed<SummaryItem[]>(() => {
  let netAmount = 0;
  let taxAmount = 0;

  billList.value.forEach((item) => {
    netAmount += getSafeAmount(item.netAmount);
    taxAmount += getSafeAmount(item.taxAmount);
  });

  return [
    { label: '不含税金额', value: formatAmount(netAmount), isMoney: true },
    { label: '总税额', value: formatAmount(taxAmount), isMoney: true },
    { label: '单据总笔数', value: billList.value.length }
  ];
});

/**
 * 根据当前筛选条件过滤列表数据并分页。
 */
const applyQueryFilters = () => {
  let nextList = [...allBillList.value];

  if (queryParams.billNo.trim()) {
    const keyword = queryParams.billNo.trim().toLowerCase();
    nextList = nextList.filter((item) => {
      return item.billNo.toLowerCase().includes(keyword);
    });
  }
  if (queryParams.supplier) {
    nextList = nextList.filter((item) => {
      return item.supplierId === queryParams.supplier;
    });
  }
  if (queryParams.status) {
    nextList = nextList.filter((item) => {
      return item.status === queryParams.status;
    });
  }
  if (queryParams.billDate.length === 2) {
    const startDate = queryParams.billDate[0];
    const endDate = queryParams.billDate[1];
    if (startDate && endDate) {
      nextList = nextList.filter((item) => {
        return item.billDate >= startDate && item.billDate <= endDate;
      });
    }
  }
  if (queryParams.userName.trim()) {
    const keyword = queryParams.userName.trim().toLowerCase();
    nextList = nextList.filter((item) => {
      return item.userName.toLowerCase().includes(keyword);
    });
  }
  if (queryParams.warehouse) {
    nextList = nextList.filter((item) => {
      return item.warehouseId === queryParams.warehouse;
    });
  }

  filteredBillList.value = nextList;
  const maxPage = Math.max(1, Math.ceil(nextList.length / queryParams.pageSize));
  if (queryParams.pageNum > maxPage) {
    queryParams.pageNum = maxPage;
  }
  const startIndex = (queryParams.pageNum - 1) * queryParams.pageSize;
  billList.value = nextList.slice(startIndex, startIndex + queryParams.pageSize);
  selectedRows.value = [];
};

/**
 * 加载列表 mock 数据。
 */
const loadBillListData = async () => {
  const data = await fetchBillTemplateListData();
  overviewCards.value = data.overviewCards;
  allBillList.value = data.items;
};

/**
 * 按当前查询条件刷新列表。
 */
const refreshBillList = async (reload = false) => {
  loading.value = true;
  try {
    if (reload || !allBillList.value.length) {
      await loadBillListData();
    }
    applyQueryFilters();
  } finally {
    loading.value = false;
  }
};

/**
 * 执行查询并重置到第一页。
 */
const handleQuery = async () => {
  queryParams.pageNum = 1;
  await refreshBillList();
};

/**
 * 刷新当前列表数据。
 */
const handleRefresh = async () => {
  await refreshBillList(true);
};

/**
 * 分页页码变化时刷新当前页数据。
 */
const handlePageChange = async () => {
  await refreshBillList();
};

/**
 * 分页大小变化时回到第一页后刷新。
 */
const handlePageSizeChange = async () => {
  queryParams.pageNum = 1;
  await refreshBillList();
};

/**
 * 重置查询条件并重新查询。
 */
const resetQuery = async () => {
  queryParams.pageNum = 1;
  queryParams.pageSize = 20;
  queryParams.billNo = '';
  queryParams.supplier = '';
  queryParams.status = '';
  queryParams.billDate = [];
  queryParams.userName = '';
  queryParams.warehouse = '';
  await refreshBillList();
};

/**
 * 按单据编号筛选。
 */
const filterByBillNo = (value: string, row: BillTemplateListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.billNo;
  });
};

/**
 * 按单据类型筛选。
 */
const filterByBillType = (value: string, row: BillTemplateListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.billType;
  });
};

/**
 * 按供应商筛选。
 */
const filterBySupplier = (value: string, row: BillTemplateListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.supplierName;
  });
};

/**
 * 按状态筛选。
 */
const filterByStatus = (value: string, row: BillTemplateListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.statusName;
  });
};

/**
 * 同步表格勾选行。
 */
const handleSelectionChange = (val: BillTemplateListItem[]) => {
  selectedRows.value = val;
};

/**
 * 按金额排序。
 */
const sortByTotalAmount = (left: BillTemplateListItem, right: BillTemplateListItem) => {
  return compareNumber(left.totalAmount, right.totalAmount);
};

/**
 * 按状态值排序。
 */
const sortByStatus = (left: BillTemplateListItem, right: BillTemplateListItem) => {
  return compareNumber(left.status, right.status);
};

/**
 * 记录当前排序列。
 */
const handleSortChange = (payload: { prop?: string | null }) => {
  currentSortProp.value = payload.prop ? String(payload.prop) : '';
};

/**
 * 解析状态标签类型。
 */
const getStatusType = (status: string) => {
  return ({ '0': 'info', '1': 'warning', '2': 'primary', '3': 'success', '4': 'success' }[status] || 'info');
};

/**
 * 跳转到新建单据页。
 */
const handleAdd = () => {
  billTemplateStore.resetHeaderDraft();
  router.push('/purchase/bill-template/create');
};

/**
 * 跳转到单据详情页。
 */
const handleView = (row: BillTemplateListItem) => {
  router.push({ path: '/purchase/bill-template/detail', query: { id: row.billNo } });
};

/**
 * 跳转到单据编辑页。
 */
const handleEdit = (row: BillTemplateListItem) => {
  router.push({ path: '/purchase/bill-template/detail', query: { id: row.billNo, mode: 'edit' } });
};

/**
 * 批量审核当前选中的单据。
 */
const handleBatchAudit = () => {
  ElMessage.success('批量审核成功');
};

/**
 * 批量删除当前选中的单据。
 */
const handleBatchDelete = () => {
  ElMessage.success('批量删除成功');
};

/**
 * 处理行级更多操作。
 */
const handleRowMore = (cmd: string, row: BillTemplateListItem) => {
  ElMessage.info(`操作 ${cmd} 对于 ${row.billNo}`);
};

/**
 * 适配下拉菜单的命令回调。
 */
const handleRowMoreCommand = (cmd: string, row: BillTemplateListItem) => {
  handleRowMore(cmd, row);
};

/**
 * 当列显示设置变化时，清理对应筛选和排序状态。
 */
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

/**
 * 初始化列表数据。
 */
onMounted(async () => {
  await refreshBillList(true);
});
</script>

<style scoped lang="scss">
.app-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 0;
  gap: 8px;
  overflow: hidden;
}

.stat-row { flex-shrink: 0; margin-bottom: 0; }
.stat-card {
  display: flex; align-items: center; padding: 16px; position: relative; height: 90px;
  .stat-icon {
    width: 48px; height: 48px; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 24px; margin-right: 16px;
    &.primary { background: rgba(22, 119, 255, 0.1); color: #1677ff; }
    &.warning { background: rgba(250, 173, 20, 0.1); color: #faad14; }
    &.danger { background: rgba(255, 77, 79, 0.1); color: #ff4d4f; }
    &.success { background: rgba(82, 196, 26, 0.1); color: #52c41a; }
  }
  .stat-info { flex: 1; .stat-label { font-size: 13px; color: #8c8c8c; } .stat-value { font-size: 20px; font-weight: bold; } }
  .stat-footer {
    position: absolute; bottom: 8px; right: 16px; font-size: 12px; color: #bfbfbf;
    .trend-up { color: #52c41a; }
    .trend-down { color: #ff4d4f; }
  }
}

.search-wrapper { flex-shrink: 0; padding: 16px 16px 4px; .search-actions { text-align: right; } }

.table-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  padding: 0;
  overflow: hidden;
}

.status-tabs-container { flex-shrink: 0; padding: 0 16px; border-bottom: 1px solid #f0f0f0; :deep(.el-tabs__header) { margin: 0; border-bottom: none; } }
.table-operations {
  flex-shrink: 0;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  .right-ops { display: flex; align-items: center; gap: 8px; }
}

.table-content {
  flex: 1;
  min-height: 0;
  padding: 0 16px;
  overflow: hidden;
}

:deep(.main-table.el-table) { width: 100% !important; .price-text { font-family: 'Consolas'; font-weight: 600; color: #cf1322; } }

.pagination-container { flex-shrink: 0; margin-top: 0; padding: 12px 16px; background: #fff; display: flex; justify-content: flex-end; }

:deep(.natural-footer) {
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  border: 1px solid var(--app-border-color);
  box-shadow: 0 1px 3px rgba(0,0,0,0.04);
  .footer-summary-container { border-top: none; }
  .summary-wrapper { padding: 0 16px; }
  .summary-main { height: 44px; }
}
</style>
