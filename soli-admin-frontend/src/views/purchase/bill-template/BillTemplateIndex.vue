<template>
  <div class="app-container">
    <!-- 1. 顶部操作看板 -->
    <el-row :gutter="8" class="stat-row">
      <el-col :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon primary"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">今日新增</div>
            <div class="stat-value">12 <span class="stat-unit">笔</span></div>
          </div>
          <div class="stat-footer">环比昨日 <span class="trend-up">+20% <el-icon><CaretTop /></el-icon></span></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon warning"><el-icon><Clock /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">待审核</div>
            <div class="stat-value">5 <span class="stat-unit">笔</span></div>
          </div>
          <div class="stat-footer">等待主管处理</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon danger"><el-icon><Money /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">本月总金额</div>
            <div class="stat-value">856,400 <span class="stat-unit">元</span></div>
          </div>
          <div class="stat-footer">较上月 <span class="trend-down">-5.2% <el-icon><CaretBottom /></el-icon></span></div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="tech-card stat-card">
          <div class="stat-icon success"><el-icon><Finished /></el-icon></div>
          <div class="stat-info">
            <div class="stat-label">已入库</div>
            <div class="stat-value">128 <span class="stat-unit">笔</span></div>
          </div>
          <div class="stat-footer">执行率 98.5%</div>
        </div>
      </el-col>
    </el-row>

    <!-- 2. 搜索过滤区域 -->
    <div class="tech-card search-wrapper">
      <el-form :model="queryParams" ref="queryRef" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="单据编号" prop="billNo">
              <el-input v-model="queryParams.billNo" placeholder="单号/外部订单号" clearable @keyup.enter="handleQuery" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="供应商" prop="supplier">
              <el-select v-model="queryParams.supplier" placeholder="请选择" clearable style="width: 100%">
                <el-option label="华为技术" value="1" />
                <el-option label="小米通讯" value="2" />
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
              <el-form-item label="业务员" prop="userName"><el-input v-model="queryParams.userName" clearable /></el-form-item>
            </el-col>
            <el-col :span="6">
              <el-form-item label="收货仓库" prop="warehouse">
                <el-select v-model="queryParams.warehouse" clearable style="width: 100%">
                  <el-option label="深圳仓" value="1" />
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
          <el-tab-pane label="待审核" name="1" />
          <el-tab-pane label="已审核" name="2" />
          <el-tab-pane label="已作废" name="3" />
        </el-tabs>
      </div>

      <div class="table-operations">
        <div class="left-ops">
          <!-- 修复新建按钮颜色 -->
          <el-button type="primary" icon="Plus" @click="handleAdd">新建采购单</el-button>
          <el-divider direction="vertical" />
          <el-button icon="Check" :disabled="!selectedRows.length" @click="handleBatchAudit">批量审核</el-button>
          <el-button icon="Delete" :disabled="!selectedRows.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
        <div class="right-ops">
          <el-tooltip content="刷新数据" placement="top"><el-button circle icon="RefreshRight" @click="handleQuery" /></el-tooltip>
          <BillTemplateColumnSetting
            v-model="visibleColumnKeys"
            :columns="columnOptions"
            :default-keys="defaultVisibleColumnKeys"
          />
        </div>
      </div>

      <!-- 表格内容 -->
      <div class="table-content">
        <el-table
          v-loading="loading" :data="billList" border stripe highlight-current-row height="100%"
          @selection-change="handleSelectionChange" @sort-change="handleSortChange" ref="tableRef" class="main-table"
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

      <!-- 嵌入卡片底部的分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          :total="total"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </div>

    <!-- 4. 合计栏回归自然流：直接跟在表格卡片下方 -->
    <FooterSummary :items="listSummary" :more-items="listMoreSummary" class="natural-footer" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
  Search, Refresh, Plus, ArrowUp, ArrowDown, Document, Clock, Money, Finished,
  CaretTop, CaretBottom, Check, RefreshRight, MoreFilled
} from '@element-plus/icons-vue';
import { ElMessage, type TableInstance } from 'element-plus';
import FooterSummary, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import { useBillTemplateStore } from '@/store/modules/billTemplate';
import BillTemplateColumnSetting from './components/BillTemplateColumnSetting.vue';
import { buildTextFilters, compareNumber, matchTextFilter } from './components/tableHelper';

const router = useRouter();
const billTemplateStore = useBillTemplateStore();
const tableRef = ref<TableInstance>();
const loading = ref(false);
const showMoreSearch = ref(false);
const selectedRows = ref<any[]>([]);
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

interface BillListItem {
  billNo: string;
  billType: string;
  supplierName: string;
  totalAmount: string;
  netAmount: string;
  taxAmount: string;
  totalQty: number;
  status: string;
  statusName: string;
}

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
  pageNum: 1, pageSize: 20, billNo: '', supplier: '', status: '', billDate: [] as string[],
  userName: '', warehouse: '', createBy: ''
});

const billList = ref<BillListItem[]>([
  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  },  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  },  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  },{
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  },  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  },  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术',
    totalAmount: '128,500.00',
    netAmount: '113,716.81',
    taxAmount: '14,783.19',
    totalQty: 1280,
    status: '0',
    statusName: '未审核'
  },
  {
    billNo: 'PO-20240322-002',
    billType: '采购进货',
    supplierName: '小米通讯',
    totalAmount: '45,000.00',
    netAmount: '39,823.01',
    taxAmount: '5,176.99',
    totalQty: 620,
    status: '1',
    statusName: '待审核'
  },
  {
    billNo: 'PO-20240322-003',
    billType: '采购进货',
    supplierName: '苹果贸易',
    totalAmount: '22,000.00',
    netAmount: '19,469.03',
    taxAmount: '2,530.97',
    totalQty: 400,
    status: '2',
    statusName: '已审核'
  }
]);

/**
 * 生成单据编号筛选项。
 */
const billNoFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.billNo;
  });
});

/**
 * 生成单据类型筛选项。
 */
const billTypeFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.billType;
  });
});

/**
 * 生成供应商筛选项。
 */
const supplierFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.supplierName;
  });
});

/**
 * 生成状态筛选项。
 */
const statusFilters = computed(() => {
  return buildTextFilters(billList.value, (item) => {
    return item.statusName;
  });
});

/**
 * 计算当前列表总数。
 */
const total = computed(() => {
  return billList.value.length;
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
 * 模拟查询列表数据。
 */
const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
  }, 200);
};

/**
 * 重置查询条件并重新查询。
 */
const resetQuery = () => {
  queryParams.billNo = '';
  queryParams.supplier = '';
  queryParams.status = '';
  queryParams.billDate = [];
  handleQuery();
};

/**
 * 按单据编号筛选。
 */
const filterByBillNo = (value: string, row: BillListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.billNo;
  });
};

/**
 * 按单据类型筛选。
 */
const filterByBillType = (value: string, row: BillListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.billType;
  });
};

/**
 * 按供应商筛选。
 */
const filterBySupplier = (value: string, row: BillListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.supplierName;
  });
};

/**
 * 按状态筛选。
 */
const filterByStatus = (value: string, row: BillListItem) => {
  return matchTextFilter(value, row, (item) => {
    return item.statusName;
  });
};

/**
 * 同步表格勾选行。
 */
const handleSelectionChange = (val: any[]) => {
  selectedRows.value = val;
};

/**
 * 按金额排序。
 */
const sortByTotalAmount = (left: BillListItem, right: BillListItem) => {
  return compareNumber(left.totalAmount, right.totalAmount);
};

/**
 * 按状态值排序。
 */
const sortByStatus = (left: BillListItem, right: BillListItem) => {
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
  return ({ '0': 'info', '1': 'warning', '2': 'success', '3': 'danger' }[status] || 'info');
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
const handleView = (row: any) => {
  router.push({ path: '/purchase/bill-template/detail', query: { id: row.billNo } });
};

/**
 * 跳转到单据编辑页。
 */
const handleEdit = (row: any) => {
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
const handleRowMore = (cmd: string, row: any) => {
  ElMessage.info(`操作 ${cmd} 对于 ${row.billNo}`);
};

/**
 * 适配下拉菜单的命令回调。
 */
const handleRowMoreCommand = (cmd: string, row: any) => {
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
onMounted(() => {
  handleQuery();
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
  .stat-footer { position: absolute; bottom: 8px; right: 16px; font-size: 12px; color: #bfbfbf; .trend-up { color: #52c41a; } }
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
