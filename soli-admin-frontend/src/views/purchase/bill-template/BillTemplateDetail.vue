<template>
  <div class="app-container">
    <!-- 1. 顶部操作栏 -->
    <BillDetailHeaderCard
      title="采购订单"
      :billNo="billInfo.billNo"
      :statusName="billInfo.statusName"
      :statusType="getStatusType(billInfo.status)"
      :show-actions="showHeaderActions"
      :permissions="standardPermissions"
      @back="handleBack"
      @save="handleSave"
      @audit="handleAudit"
      @more-action="handleMoreAction"
    >
    </BillDetailHeaderCard>

    <!-- 2. 单头信息 -->
    <BillDetailFormCard v-if="showHeaderCard" v-model="headerExpanded" title="单头信息">
      <el-tabs v-model="activeHeaderTab" class="custom-plain-tabs">
        <el-tab-pane v-if="showBasicHeaderTab" label="基础信息" name="basic">
          <BillHeaderBasicForm :model="billInfo" :permissions="standardPermissions" />
        </el-tab-pane>
        <el-tab-pane v-if="showAuditHeaderTab" label="审核信息" name="audit">
          <BillHeaderAuditInfo :model="billInfo" :permissions="standardPermissions" />
        </el-tab-pane>
      </el-tabs>
    </BillDetailFormCard>

    <!-- 3. 明细信息 -->
    <BillDetailTableCard v-if="showDetailCard">
      <el-tabs v-model="activeDetailTab" class="tech-tabs-fill">
        <el-tab-pane v-if="showGoodsDetailTab" label="物料明细清单" name="items">
          <BillTemplateItemsTab v-model="itemDetails" :permissions="goodsDetailPermissions" />
        </el-tab-pane>
        <el-tab-pane v-if="showSourceDetailTab" :label="`来源单据 (${sourceBillList.length})`" name="source">
          <BillTemplateSourceTab :rows="sourceBillList" :permissions="sourceDetailPermissions" />
        </el-tab-pane>
        <el-tab-pane v-if="showAttachmentDetailTab" :label="`附件管理 (${attachmentList.length})`" name="files">
          <BillTemplateAttachmentTab :rows="attachmentList" :permissions="attachmentDetailPermissions" />
        </el-tab-pane>
      </el-tabs>
    </BillDetailTableCard>

    <!-- 4. 合计栏回归自然流：直接跟在表格卡片下方 -->
    <FooterSummaryCard :items="summaryData" :more-items="moreSummaryData" class="natural-footer" />

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
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { Box } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

import BillDetailFormCard from '@/components/Bill/BillDetailFormCard.vue';
import BillDetailHeaderCard from '@/components/Bill/BillDetailHeaderCard.vue';
import FooterSummaryCard, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import BillDetailTableCard from '@/components/Bill/BillDetailTableCard.vue';
import {
  createEmptyBillPagePermissions,
  createEmptyBillPermissionSet,
  hasAnyVisiblePermission
} from '@/components/Bill/billPermission';
import { useBillTemplateStore } from '@/store/modules/billTemplate';
import { fetchBillTemplatePagePermissions } from './billPermissionMock';
import BillHeaderBasicForm from './components/BillHeaderBasicForm.vue';
import BillHeaderAuditInfo from './components/BillHeaderAuditInfo.vue';
import BillTemplateItemsTab from './components/BillTemplateItemsTab.vue';
import BillTemplateSourceTab from './components/BillTemplateSourceTab.vue';
import BillTemplateAttachmentTab from './components/BillTemplateAttachmentTab.vue';

const route = useRoute();
const router = useRouter();
const billTemplateStore = useBillTemplateStore();

const getSafeVal = (val: any) => {
  const n = parseFloat(val);
  return isNaN(n) ? 0 : n;
};

const activeHeaderTab = ref('basic');
const activeDetailTab = ref('items');
const headerExpanded = ref(true);
const logVisible = ref(false);
const pagePermissions = ref(createEmptyBillPagePermissions());
const emptyPermissionSet = createEmptyBillPermissionSet();
/**
 * 当前页标准单头权限。
 */
const standardPermissions = computed(() => {
  return pagePermissions.value.header.standard || emptyPermissionSet;
});

/**
 * 当前页物料明细权限。
 */
const goodsDetailPermissions = computed(() => {
  return pagePermissions.value.detail.goodsDetail || emptyPermissionSet;
});

/**
 * 当前页来源单据权限。
 */
const sourceDetailPermissions = computed(() => {
  return pagePermissions.value.detail.sourceDetail || emptyPermissionSet;
});

/**
 * 当前页附件管理权限。
 */
const attachmentDetailPermissions = computed(() => {
  return pagePermissions.value.detail.attachmentDetail || emptyPermissionSet;
});
const headerBasicFieldKeys = ['billDate', 'supplierId', 'settleType', 'warehouseId', 'userName', 'currency', 'remark'];
const headerAuditFieldKeys = ['createByName', 'statusName', 'descriptionText'];
const goodsDetailFieldKeys = ['itemCode', 'itemName', 'spec', 'unit', 'qty', 'priceExcl', 'taxRate', 'taxAmount', 'totalAmount'];
const sourceDetailFieldKeys = ['sourceBillNo', 'sourceType', 'supplierName', 'billDate', 'totalAmount', 'status', 'remark'];
const attachmentDetailFieldKeys = ['fileName', 'fileType', 'fileSize', 'uploadUser', 'uploadTime', 'remark'];

/**
 * 判断头部操作区是否需要显示。
 */
const showHeaderActions = computed(() => {
  return hasAnyVisiblePermission(standardPermissions.value, 'buttons', ['save', 'audit', 'print', 'log', 'copy', 'void']);
});

/**
 * 判断基础信息 tab 是否需要显示。
 */
const showBasicHeaderTab = computed(() => {
  return hasAnyVisiblePermission(standardPermissions.value, 'fields', headerBasicFieldKeys);
});

/**
 * 判断审核信息 tab 是否需要显示。
 */
const showAuditHeaderTab = computed(() => {
  return hasAnyVisiblePermission(standardPermissions.value, 'fields', headerAuditFieldKeys);
});

/**
 * 判断单头卡片是否需要显示。
 */
const showHeaderCard = computed(() => {
  return showBasicHeaderTab.value || showAuditHeaderTab.value;
});

/**
 * 判断物料明细 tab 是否需要显示。
 */
const showGoodsDetailTab = computed(() => {
  return hasAnyVisiblePermission(goodsDetailPermissions.value, 'fields', goodsDetailFieldKeys);
});

/**
 * 判断来源单据 tab 是否需要显示。
 */
const showSourceDetailTab = computed(() => {
  return hasAnyVisiblePermission(sourceDetailPermissions.value, 'fields', sourceDetailFieldKeys);
});

/**
 * 判断附件管理 tab 是否需要显示。
 */
const showAttachmentDetailTab = computed(() => {
  return hasAnyVisiblePermission(attachmentDetailPermissions.value, 'fields', attachmentDetailFieldKeys);
});

/**
 * 判断明细卡片是否需要显示。
 */
const showDetailCard = computed(() => {
  return showGoodsDetailTab.value || showSourceDetailTab.value || showAttachmentDetailTab.value;
});

const billInfo = reactive({
  billNo: 'PO-20240322-001', billDate: '2024-03-22', status: '0', statusName: '未审核',
  supplierId: 1, settleType: '1', deptId: '1-1', userName: '王经理', warehouseId: 1, currency: 'CNY', remark: '', createByName: '张三'
});

const itemDetails = ref([
  { itemCode: 'HW-001', itemName: '华为 Mate 60 Pro', spec: '12GB+512GB', unit: '台', qty: 10, priceExcl: 5481.25, taxRate: 13, note: '' },
  { itemCode: 'XM-002', itemName: '小米 14 Ultra', spec: '16GB+1TB', unit: '台', qty: 5, priceExcl: 4698.10, taxRate: 13, note: '' }
]);

const sourceBillList = ref([
  {
    sourceBillNo: 'CGSQ-20240320-001',
    sourceType: '采购申请',
    supplierName: '华为技术有限公司',
    billDate: '2024-03-20',
    totalAmount: '68,500.00',
    status: '已审核',
    remark: '来源于月度采购申请'
  },
  {
    sourceBillNo: '询价单-20240318-002',
    sourceType: '询价单',
    supplierName: '小米通讯有限公司',
    billDate: '2024-03-18',
    totalAmount: '45,000.00',
    status: '已审核',
    remark: '比价后自动带入'
  }
]);

const attachmentList = ref([
  {
    fileName: '采购合同-华为Mate60Pro.pdf',
    fileType: 'PDF',
    fileSize: '1.8 MB',
    uploadUser: '张三',
    uploadTime: '2024-03-22 09:12:00',
    remark: '主合同文件'
  },
  {
    fileName: '供应商报价单-20240322.xlsx',
    fileType: 'XLSX',
    fileSize: '356 KB',
    uploadUser: '王经理',
    uploadTime: '2024-03-22 10:08:00',
    remark: '附件报价明细'
  }
]);

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

/**
 * 将新建页暂存的单头信息带入详情页。
 */
const applyHeaderDraft = () => {
  const draft = billTemplateStore.headerDraft;
  billInfo.billDate = draft.billDate || billInfo.billDate;
  billInfo.supplierId = draft.supplierId ?? billInfo.supplierId;
  billInfo.settleType = draft.settleType || billInfo.settleType;
  billInfo.deptId = draft.deptId || billInfo.deptId;
  billInfo.userName = draft.userName || billInfo.userName;
  billInfo.warehouseId = draft.warehouseId ?? billInfo.warehouseId;
  billInfo.currency = draft.currency || billInfo.currency;
  billInfo.remark = draft.remark || '';
  billInfo.status = draft.status || billInfo.status;
  billInfo.statusName = draft.statusName || billInfo.statusName;
  billInfo.createByName = draft.createByName || billInfo.createByName;
};

/**
 * 按当前单据状态重新加载权限数据。
 */
const loadPagePermissions = async () => {
  pagePermissions.value = await fetchBillTemplatePagePermissions('detail', billInfo.status);
};

/**
 * 返回单据模板列表页。
 */
const handleBack = () => {
  router.push('/purchase/bill-template');
};

/**
 * 处理保存动作，这里保留 mock 提示。
 */
const handleSave = (status: any) => {
  ElMessage.success('操作成功');
};

/**
 * 处理审核动作，这里保留 mock 提示。
 */
const handleAudit = () => {
  ElMessage.success('审核通过');
};

/**
 * 处理更多操作命令。
 */
const handleMoreAction = (cmd: string) => {
  if (cmd === 'log') {
    logVisible.value = true;
    return;
  }
  ElMessage.info(`执行操作: ${cmd}`);
};

/**
 * 解析单据状态标签类型。
 */
const getStatusType = (status: string) => {
  return ({ '0': 'info', '1': 'warning', '2': 'success' }[status] || 'info');
};

/**
 * 当单头 tab 权限变化时，自动切换到第一个可见 tab。
 */
watch([showBasicHeaderTab, showAuditHeaderTab], ([basicVisible, auditVisible]) => {
  if (activeHeaderTab.value === 'basic' && !basicVisible && auditVisible) {
    activeHeaderTab.value = 'audit';
  }
  if (activeHeaderTab.value === 'audit' && !auditVisible && basicVisible) {
    activeHeaderTab.value = 'basic';
  }
}, { immediate: true });

/**
 * 当明细 tab 权限变化时，自动切换到第一个可见 tab。
 */
watch([showGoodsDetailTab, showSourceDetailTab, showAttachmentDetailTab], ([goodsVisible, sourceVisible, attachmentVisible]) => {
  if (activeDetailTab.value === 'items' && !goodsVisible) {
    activeDetailTab.value = sourceVisible ? 'source' : (attachmentVisible ? 'files' : 'items');
  }
  if (activeDetailTab.value === 'source' && !sourceVisible) {
    activeDetailTab.value = goodsVisible ? 'items' : (attachmentVisible ? 'files' : 'source');
  }
  if (activeDetailTab.value === 'files' && !attachmentVisible) {
    activeDetailTab.value = goodsVisible ? 'items' : (sourceVisible ? 'source' : 'files');
  }
}, { immediate: true });

/**
 * 当单据状态变化时，按状态重新加载权限。
 */
watch(() => billInfo.status, async () => {
  await loadPagePermissions();
});

/**
 * 初始化详情页数据和权限。
 */
onMounted(async () => {
  if (route.query.mode === 'create') {
    applyHeaderDraft();
  }
  await loadPagePermissions();
});
</script>

<style scoped lang="scss">
.app-container {
  padding: 0; background-color: transparent; display: flex; flex-direction: column; gap: 8px; height: 100%;
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

.log-timeline-container { padding: 10px 20px; }
.log-item-content {
  .operator { font-size: 13px; color: var(--app-primary); font-weight: 600; }
  .content { margin: 4px 0 0; font-size: 13px; color: #595959; }
}
</style>
