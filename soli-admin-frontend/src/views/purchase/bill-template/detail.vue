<template>
  <div class="app-container">
    <!-- 1. 顶部操作栏 -->
    <DetailHeader
      title="采购订单"
      :billNo="billInfo.billNo"
      :statusName="billInfo.statusName"
      :statusType="getStatusType(billInfo.status)"
      @back="handleBack"
    >
      <template #extra>
        <div class="header-main-actions">
          <el-dropdown split-button type="primary" @click="handleSave('0')" @command="handleSave" v-hasPermi="['purchase:bill:save']">
            <el-icon><DocumentChecked /></el-icon>&nbsp;保存
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="0">保存为未审核 (暂存)</el-dropdown-item>
                <el-dropdown-item command="1">保存并提交审核</el-dropdown-item>
                <el-dropdown-item command="2">保存并直接过账</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button icon="Check" @click="handleAudit" style="margin-left: 12px" v-hasPermi="['purchase:bill:audit']">审核通过</el-button>
          <el-button icon="Printer" v-hasPermi="['purchase:bill:print']">打印</el-button>
          <el-dropdown trigger="click" @command="handleMoreAction" style="margin-left: 12px">
            <el-button icon="MoreFilled">更多操作</el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="log" icon="Files" v-hasPermi="['purchase:bill:log']">操作日志</el-dropdown-item>
                <el-dropdown-item command="copy" icon="CopyDocument" v-hasPermi="['purchase:bill:add']">复制单据</el-dropdown-item>
                <el-divider style="margin: 4px 0" />
                <el-dropdown-item command="void" icon="CircleClose" type="danger" v-hasPermi="['purchase:bill:void']">作废单据</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>
    </DetailHeader>

    <!-- 2. 单据头部信息 -->
    <div class="tech-card">
      <el-tabs v-model="activeHeaderTab" class="custom-plain-tabs">
        <el-tab-pane label="基础信息" name="basic">
          <el-form :model="billInfo" label-width="90px" size="small" class="compact-form">
            <el-row :gutter="24">
              <el-col :span="6">
                <el-form-item label="单据日期">
                  <el-date-picker v-model="billInfo.billDate" type="date" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="供应商">
                  <el-select v-model="billInfo.supplierId" style="width: 100%">
                    <el-option label="华为技术有限公司" :value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="结算方式">
                  <el-select v-model="billInfo.settleType" style="width: 100%">
                    <el-option label="电汇" value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="仓库">
                  <el-select v-model="billInfo.warehouseId" style="width: 100%">
                    <el-option label="深圳一号仓" :value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="业务员">
                  <el-input v-model="billInfo.userName" />
                </el-form-item>
              </el-col>
              <el-col :span="6">
                <el-form-item label="币种">
                  <el-select v-model="billInfo.currency" style="width: 100%">
                    <el-option label="人民币 (CNY)" value="CNY" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="备注">
                  <el-input v-model="billInfo.remark" placeholder="备注..." />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="审核信息" name="audit">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="制单人">张三</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <el-tag size="small" :type="billInfo.status === '1' ? 'success' : 'info'">{{ billInfo.statusName }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 3. 明细信息与合计栏包装：解决全局布局冲突的关键 -->
    <div class="tech-card detail-table-card">
      <el-tabs v-model="activeDetailTab" class="tech-tabs-fill">
        <el-tab-pane label="物料明细清单" name="items">
          <div class="tab-pane-content">
            <BillDetailTable
              ref="billTableRef"
              v-model="itemDetails"
              @add="addItem"
              :permissionAdd="['purchase:bill:add']"
              :permissionImport="['purchase:bill:import']"
              :permissionExport="['purchase:bill:export']"
            >
              <template #toolbar-right>
                <el-checkbox v-model="showTax" label="显示税额" />
              </template>

              <el-table-column label="物料编码" prop="itemCode" width="130" fixed="left">
                <template #default="scope">
                  <el-input v-model="scope.row.itemCode" size="small" readonly>
                    <template #append><el-button icon="Search" @click="showMaterialSelect(scope.$index)" /></template>
                  </el-input>
                </template>
              </el-table-column>
              <el-table-column label="物料名称" prop="itemName" min-width="180" show-overflow-tooltip />
              <el-table-column label="规格型号" prop="spec" width="120" show-overflow-tooltip />
              <el-table-column label="单位" prop="unit" width="60" align="center" />
              <el-table-column label="数量" prop="qty" width="100" align="right">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.qty"
                    :controls="false"
                    size="small"
                    style="width: 100%"
                    @focus="handleFocus(scope.row, 'qty')"
                    @blur="handleBlur(scope.row, 'qty')"
                  />
                </template>
              </el-table-column>
              <el-table-column label="不含税单价" prop="priceExcl" width="110" align="right">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.priceExcl"
                    :controls="false"
                    :precision="4"
                    size="small"
                    style="width: 100%"
                    @focus="handleFocus(scope.row, 'priceExcl')"
                    @blur="handleBlur(scope.row, 'priceExcl')"
                  />
                </template>
              </el-table-column>
              <el-table-column label="税率%" prop="taxRate" width="80" align="right">
                <template #default="scope">
                  <el-input-number
                    v-model="scope.row.taxRate"
                    :controls="false"
                    size="small"
                    style="width: 100%"
                    @focus="handleFocus(scope.row, 'taxRate')"
                    @blur="handleBlur(scope.row, 'taxRate')"
                  />
                </template>
              </el-table-column>

              <el-table-column v-if="showTax" label="税额" width="100" align="right">
                <template #default="scope">
                  <span class="tax-amount">¥ {{ calcTaxAmount(scope.row) }}</span>
                </template>
              </el-table-column>

              <el-table-column label="价税合计" width="120" align="right" fixed="right">
                <template #default="scope">
                  <span class="total-amount">¥ {{ calcTotalAmount(scope.row) }}</span>
                </template>
              </el-table-column>
            </BillDetailTable>
          </div>
        </el-tab-pane>
        <el-tab-pane label="附件管理 (0)" name="files">
          <div class="tab-pane-content"><el-empty description="暂无附件" /></div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 4. 合计栏回归自然流：直接跟在表格卡片下方 -->
    <FooterSummary :items="summaryData" :more-items="moreSummaryData" class="natural-footer" />

    <!-- 4. 操作日志弹窗 -->
    <el-dialog v-model="logVisible" title="单据操作日志" width="600px" destroy-on-close align-center>
      <div class="log-timeline-container">
        <el-timeline>
          <el-timeline-item v-for="(activity, index) in activities" :key="index" :type="activity.type" :timestamp="activity.timestamp">
            <div class="log-item-content">
              <span class="operator">{{ activity.operator }}</span>
              <p class="content">{{ activity.content }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import {
  DocumentChecked, Check, Printer, CopyDocument, MoreFilled, InfoFilled,
  Search, Files, Box
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

import DetailHeader from '@/components/Business/DetailHeader.vue';
import FooterSummary, { type SummaryItem } from '@/components/Business/FooterSummary.vue';
import BillDetailTable from '@/components/Business/BillDetailTable.vue';

const router = useRouter();
const billTableRef = ref();

const getSafeVal = (val: any) => {
  const n = parseFloat(val);
  return isNaN(n) ? 0 : n;
};

const activeHeaderTab = ref('basic');
const activeDetailTab = ref('items');
const showTax = ref(true);
const logVisible = ref(false);

const billInfo = reactive({
  billNo: 'PO-20240322-001', billDate: '2024-03-22', status: '0', statusName: '未审核',
  supplierId: 1, settleType: '1', deptId: '1-1', userName: '王经理', warehouseId: 1, currency: 'CNY', remark: ''
});

const itemDetails = ref([
  { itemCode: 'HW-001', itemName: '华为 Mate 60 Pro', spec: '12GB+512GB', unit: '台', qty: 10, priceExcl: 5481.25, taxRate: 13, note: '' },
  { itemCode: 'XM-002', itemName: '小米 14 Ultra', spec: '16GB+1TB', unit: '台', qty: 5, priceExcl: 4698.10, taxRate: 13, note: '' }
]);

const originalValues = new WeakMap<any, Record<string, any>>();

const handleFocus = (row: any, field: string) => {
  if (!originalValues.has(row)) originalValues.set(row, {});
  originalValues.get(row)![field] = row[field];
  row[field] = null;
};

const handleBlur = (row: any, field: string) => {
  if (row[field] === null || row[field] === undefined) {
    const original = originalValues.get(row)?.[field];
    row[field] = (original !== undefined && original !== null) ? original : 0;
  }
};

const calcTaxAmount = (row: any) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (rate / 100)).toFixed(2);
};

const calcTotalAmount = (row: any) => {
  const qty = getSafeVal(row.qty);
  const price = getSafeVal(row.priceExcl);
  const rate = getSafeVal(row.taxRate);
  return (qty * price * (1 + rate / 100)).toLocaleString(undefined, { minimumFractionDigits: 2 });
};

const summaryData = computed<SummaryItem[]>(() => {
  let totalAmount = 0;
  let totalQty = 0;
  itemDetails.value.forEach(item => {
    const q = getSafeVal(item.qty);
    const p = getSafeVal(item.priceExcl);
    const r = getSafeVal(item.taxRate);
    totalAmount += (q * p * (1 + r / 100));
    totalQty += q;
  });
  return [
    { label: '价税合计', value: totalAmount.toLocaleString(undefined, {minimumFractionDigits: 2}), type: 'amount', isMoney: true },
    { label: '合计数量', value: totalQty.toFixed(2), icon: Box }
  ];
});

const moreSummaryData = computed<SummaryItem[]>(() => {
  let netAmount = 0;
  let taxAmount = 0;
  itemDetails.value.forEach(item => {
    const q = getSafeVal(item.qty);
    const p = getSafeVal(item.priceExcl);
    const r = getSafeVal(item.taxRate);
    netAmount += (q * p);
    taxAmount += (q * p * (r / 100));
  });
  return [
    { label: '不含税金额', value: netAmount.toLocaleString(), isMoney: true },
    { label: '总税额', value: taxAmount.toLocaleString(), isMoney: true },
    { label: '单据总笔数', value: itemDetails.value.length }
  ];
});

const activities = [
  { content: '创建采购订单', timestamp: '2024-03-22 09:00:00', operator: '管理员', type: 'primary' },
  { content: '修改了单据明细', timestamp: '2024-03-22 10:30:00', operator: '王经理' }
];

const handleBack = () => router.back();
const handleSave = (status: any) => ElMessage.success('操作成功');
const handleAudit = () => ElMessage.success('审核通过');
const addItem = () => {
  itemDetails.value.push({ itemCode: '', itemName: '新物料', spec: '', unit: '', qty: 0, priceExcl: 0, taxRate: 13, note: '' });
  billTableRef.value?.scrollToBottom();
};
const handleMoreAction = (cmd: string) => {
  if (cmd === 'log') logVisible.value = true;
  else ElMessage.info(`执行操作: ${cmd}`);
};
const getStatusType = (status: string) => ({ '0': 'info', '1': 'warning', '2': 'success' }[status] || 'info');
const showMaterialSelect = (index: number) => ElMessage.info('打开物料选择对话框');
</script>

<style scoped lang="scss">
.app-container {
  padding: 0; background-color: transparent; display: flex; flex-direction: column; gap: 8px; height: 100%;
}

.header-main-actions { display: flex; align-items: center; gap: 8px; }

.detail-table-card {
  flex: 1; display: flex; flex-direction: column; padding: 0 !important; overflow: hidden;

  :deep(.el-tabs) { flex: 1; display: flex; flex-direction: column; min-height: 0; }
  :deep(.el-tabs__header) { margin: 0; background: #fafafa; padding: 0 16px; border-bottom: 1px solid #f0f0f0; }
  :deep(.el-tabs__content) { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
  :deep(.el-tab-pane) { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
}

.tab-pane-content {
  flex: 1; display: flex; flex-direction: column; padding: 0; min-height: 0;
}

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

.total-amount { font-family: 'Consolas', monospace; font-weight: 700; color: #cf1322; }
.tax-amount { color: #8c8c8c; font-size: 11px; }

.log-timeline-container { padding: 10px 20px; }
.log-item-content {
  .operator { font-size: 13px; color: var(--app-primary); font-weight: 600; }
  .content { margin: 4px 0 0; font-size: 13px; color: #595959; }
}
</style>
