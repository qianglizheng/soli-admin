<template>
  <div v-loading="pageLoading" class="app-container purchase-order-detail">
    <BillDetailHeaderCard
      title="采购订单"
      :billNo="billInfo.billNo"
      :statusName="billInfo.statusName"
      :statusType="getStatusType(billInfo.status)"
      :show-actions="showHeaderActions"
      :permissions="headerActionPermissions"
      :save-options="saveOptions"
      @back="handleBack"
      @save="handleSave"
      @audit="handleAudit"
      @print="handlePrint"
      @more-action="handleMoreAction"
    >
      <template #extra>
        <div class="detail-extra-actions">
          <el-button
            v-if="showStandaloneSubmit"
            type="warning"
            :disabled="buttonConfigMap.submit.disabled || submitLoading"
            :loading="actionLoading === 'submit'"
            @click="handleSubmit"
          >
            {{ buttonConfigMap.submit.label }}
          </el-button>
          <el-button
            v-if="buttonConfigMap.ship.visible"
            type="success"
            plain
            :disabled="buttonConfigMap.ship.disabled || submitLoading"
            :loading="actionLoading === 'ship'"
            @click="handleStateAction('ship')"
          >
            {{ buttonConfigMap.ship.label }}
          </el-button>
          <el-button
            v-if="buttonConfigMap.complete.visible"
            type="success"
            plain
            :disabled="buttonConfigMap.complete.disabled || submitLoading"
            :loading="actionLoading === 'complete'"
            @click="handleStateAction('complete')"
          >
            {{ buttonConfigMap.complete.label }}
          </el-button>
        </div>
      </template>
    </BillDetailHeaderCard>

    <BillDetailFormCard v-if="showHeaderCard" v-model="headerExpanded" title="单头信息">
      <el-tabs v-model="activeHeaderTab" class="custom-plain-tabs">
        <el-tab-pane v-if="showBasicHeaderTab" label="基础信息" name="basic">
          <PurchaseOrderBasicForm
            ref="basicFormRef"
            :model="billInfo"
            :permissions="basicFormPermissions"
            :show-placeholders="!billInfo.id"
          />
        </el-tab-pane>
        <el-tab-pane v-if="showAuditHeaderTab" label="审核信息" name="audit">
          <PurchaseOrderAuditInfo
            :model="billInfo"
            :permissions="auditInfoPermissions"
            :description-text="auditDescriptionText"
          />
        </el-tab-pane>
      </el-tabs>
    </BillDetailFormCard>

    <BillDetailTableCard v-if="showDetailCard">
      <el-tabs v-model="activeDetailTab" class="tech-tabs-fill">
        <el-tab-pane v-if="showItemsTab" label="物料明细清单" name="items">
          <PurchaseOrderItemsTab v-model="itemDetails" :permissions="detailPermissions" />
        </el-tab-pane>
        <el-tab-pane v-if="showSourceDetailTab" :label="`来源单据 (${sourceBillList.length})`" name="source">
          <PurchaseOrderSourceTab :rows="sourceBillList" :permissions="sourcePermissions" />
        </el-tab-pane>
        <el-tab-pane v-if="showAttachmentDetailTab" :label="`附件管理 (${attachmentList.length})`" name="files">
          <PurchaseOrderAttachmentTab :rows="attachmentList" :permissions="attachmentPermissions" />
        </el-tab-pane>
      </el-tabs>
    </BillDetailTableCard>

    <FooterSummaryCard :items="summaryData" :more-items="moreSummaryData" class="natural-footer" />

    <el-dialog v-model="logVisible" title="单据操作日志" width="600px" destroy-on-close align-center>
      <div class="log-timeline-container">
        <el-empty v-if="!activities.length" description="暂无操作日志" />
        <el-timeline v-else>
          <el-timeline-item
            v-for="item in activities"
            :key="item.id"
            :type="item.type"
            :timestamp="item.timestamp"
          >
            <div class="log-item-content">
              <span class="operator">{{ item.operator }}</span>
              <p class="content">{{ item.content }}</p>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import type {
  BillButtonPermissionItem,
  BillFieldPermissionItem,
  BillPermissionSet
} from '@/components/Bill/billPermission';
import { createBillPermissionAccessor } from '@/components/Bill/billPermission';
import BillDetailFormCard from '@/components/Bill/BillDetailFormCard.vue';
import BillDetailHeaderCard from '@/components/Bill/BillDetailHeaderCard.vue';
import BillDetailTableCard from '@/components/Bill/BillDetailTableCard.vue';
import FooterSummaryCard, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import type {
  PurchaseOrderActivity,
  PurchaseOrderAttachment,
  PurchaseOrderDetail,
  PurchaseOrderHeader,
  PurchaseOrderItem,
  PurchaseOrderSavePayload,
  PurchaseOrderSourceBill
} from '@/api/purchaseOrder';
import {
  createPurchaseOrder,
  executePurchaseOrderAction,
  getPurchaseOrderContext,
  getPurchaseOrderDetail,
  updatePurchaseOrder
} from '@/api/purchaseOrder';
import { usePurchaseOrderStore } from '@/store/modules/purchaseOrder';
import { buildResolvedButtonConfigMap, buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import {
  calcPurchaseOrderItemAmounts,
  createDefaultPurchaseOrderHeader,
  createDefaultPurchaseOrderItem,
  getStatusType
} from './purchaseOrderShared';
import PurchaseOrderAttachmentTab from './components/PurchaseOrderAttachmentTab.vue';
import PurchaseOrderAuditInfo from './components/PurchaseOrderAuditInfo.vue';
import PurchaseOrderBasicForm from './components/PurchaseOrderBasicForm.vue';
import PurchaseOrderItemsTab from './components/PurchaseOrderItemsTab.vue';
import PurchaseOrderSourceTab from './components/PurchaseOrderSourceTab.vue';

type HeaderFieldCode =
  | 'billDate'
  | 'supplierId'
  | 'settleType'
  | 'warehouseId'
  | 'userName'
  | 'currency'
  | 'remark'
  | 'createByName'
  | 'statusName';
type DetailFieldCode = 'itemCode' | 'itemName' | 'spec' | 'unit' | 'qty' | 'priceExcl' | 'taxRate' | 'taxAmount' | 'totalAmount';
type SourceFieldCode = 'sourceBillNo' | 'sourceType' | 'supplierName' | 'billDate' | 'totalAmount' | 'status' | 'remark';
type AttachmentFieldCode = 'fileName' | 'fileType' | 'fileSize' | 'uploadUser' | 'uploadTime' | 'remark';
type ContextButtonCode = 'save' | 'submit' | 'audit' | 'ship' | 'complete' | 'add' | 'delete';

interface PurchaseOrderBasicFormExpose {
  validate: () => Promise<void>;
}

const route = useRoute();
const router = useRouter();
const purchaseOrderStore = usePurchaseOrderStore();
const basicFormRef = ref<PurchaseOrderBasicFormExpose>();
const context = ref<ModuleContext | null>(null);
const pageLoading = ref(false);
const submitLoading = ref(false);
const actionLoading = ref('');
const activeHeaderTab = ref('basic');
const activeDetailTab = ref('items');
const headerExpanded = ref(true);
const logVisible = ref(false);

const billInfo = reactive<PurchaseOrderHeader>(createDefaultPurchaseOrderHeader());
const itemDetails = ref<PurchaseOrderItem[]>([createDefaultPurchaseOrderItem()]);
const sourceBillList = ref<PurchaseOrderSourceBill[]>([]);
const attachmentList = ref<PurchaseOrderAttachment[]>([]);
const activities = ref<PurchaseOrderActivity[]>([]);

const headerFieldFallbackMap: Record<HeaderFieldCode, { label: string; placeholder?: string; required?: boolean; visible?: boolean; editable?: boolean }> = {
  billDate: { label: '单据日期', placeholder: '请选择单据日期', required: true },
  supplierId: { label: '供应商', placeholder: '请选择供应商', required: true },
  settleType: { label: '结算方式', placeholder: '请选择结算方式', required: true },
  warehouseId: { label: '仓库', placeholder: '请选择仓库', required: true },
  userName: { label: '业务员', placeholder: '请输入业务员', required: true },
  currency: { label: '币种', placeholder: '请选择币种', required: true },
  remark: { label: '备注', placeholder: '请输入备注' },
  createByName: { label: '制单人', editable: false },
  statusName: { label: '状态', editable: false }
};

const detailFieldFallbackMap: Record<DetailFieldCode, { label: string; required?: boolean; visible?: boolean; editable?: boolean }> = {
  itemCode: { label: '物料编码', required: true },
  itemName: { label: '物料名称', required: true },
  spec: { label: '规格型号' },
  unit: { label: '单位' },
  qty: { label: '数量', required: true },
  priceExcl: { label: '不含税单价', required: true },
  taxRate: { label: '税率', required: true },
  taxAmount: { label: '税额', editable: false },
  totalAmount: { label: '价税合计', editable: false }
};

const sourceFieldFallbackMap: Record<SourceFieldCode, { label: string; visible?: boolean }> = {
  sourceBillNo: { label: '来源单号' },
  sourceType: { label: '来源类型' },
  supplierName: { label: '供应商' },
  billDate: { label: '单据日期' },
  totalAmount: { label: '价税合计' },
  status: { label: '状态' },
  remark: { label: '备注' }
};

const attachmentFieldFallbackMap: Record<AttachmentFieldCode, { label: string; visible?: boolean }> = {
  fileName: { label: '文件名称' },
  fileType: { label: '文件类型' },
  fileSize: { label: '文件大小' },
  uploadUser: { label: '上传人' },
  uploadTime: { label: '上传时间' },
  remark: { label: '备注' }
};

const buttonFallbackMap: Record<ContextButtonCode, { label: string; visible?: boolean; disabled?: boolean }> = {
  save: { label: '保存' },
  submit: { label: '提交' },
  audit: { label: '审核' },
  ship: { label: '发运' },
  complete: { label: '完成' },
  add: { label: '新增行' },
  delete: { label: '删除行' }
};

const currentOrderId = computed(() => {
  const id = Number(route.query.id);
  return Number.isFinite(id) && id > 0 ? id : 0;
});

const isCreateMode = computed(() => {
  return route.query.mode === 'create' && !currentOrderId.value;
});

const headerFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'header', headerFieldFallbackMap);
});

const detailFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'detail', detailFieldFallbackMap);
});

const sourceFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'source', sourceFieldFallbackMap);
});

const attachmentFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'attachment', attachmentFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(context.value?.fieldConfigs || {}, buttonFallbackMap);
});

const buildFieldPermissionItems = <T extends string>(fieldKeys: T[], configMap: Record<T, { label: string; visible: boolean; editable: boolean }>) => {
  return fieldKeys.reduce<Record<string, BillFieldPermissionItem>>((result, fieldKey) => {
    const fieldConfig = configMap[fieldKey];
    result[fieldKey] = {
      editable: fieldConfig.editable,
      label: fieldConfig.label,
      visible: fieldConfig.visible
    };
    return result;
  }, {});
};

const buildButtonPermissionItems = (
  buttonKeys: ContextButtonCode[],
  extraButtons: Record<string, BillButtonPermissionItem> = {}
) => {
  const buttons = { ...extraButtons };
  buttonKeys.forEach((buttonKey) => {
    const buttonConfig = buttonConfigMap.value[buttonKey];
    buttons[buttonKey] = {
      disabled: buttonConfig.disabled,
      label: buttonConfig.label,
      visible: buttonConfig.visible
    };
  });
  return buttons;
};

const basicFormPermissions = computed<BillPermissionSet>(() => {
  return {
    buttons: {},
    fields: buildFieldPermissionItems(
      ['billDate', 'supplierId', 'settleType', 'warehouseId', 'userName', 'currency', 'remark'],
      headerFieldConfigMap.value
    )
  };
});

const auditInfoPermissions = computed<BillPermissionSet>(() => {
  const fields = buildFieldPermissionItems(['createByName', 'statusName'], headerFieldConfigMap.value);
  fields.descriptionText = {
    editable: false,
    label: '说明',
    visible: true
  };
  return {
    buttons: {},
    fields
  };
});

const headerActionPermissions = computed<BillPermissionSet>(() => {
  return {
    fields: {},
    buttons: buildButtonPermissionItems(['save', 'audit'], {
      copy: {
        disabled: false,
        label: '复制单据',
        visible: !!billInfo.id
      },
      log: {
        disabled: false,
        label: '操作日志',
        visible: !!billInfo.id
      },
      print: {
        disabled: false,
        label: '打印',
        visible: !!billInfo.id
      },
      void: {
        disabled: false,
        label: '作废单据',
        visible: !!billInfo.id
      }
    })
  };
});

const detailPermissions = computed<BillPermissionSet>(() => {
  return {
    fields: buildFieldPermissionItems(
      ['itemCode', 'itemName', 'spec', 'unit', 'qty', 'priceExcl', 'taxRate', 'taxAmount', 'totalAmount'],
      detailFieldConfigMap.value
    ),
    buttons: buildButtonPermissionItems(['add', 'delete'], {
      columnSetting: {
        disabled: false,
        label: '列显示',
        visible: true
      },
      copy: {
        disabled: false,
        label: '复制行',
        visible: true
      },
      export: {
        disabled: false,
        label: '导出',
        visible: true
      },
      import: {
        disabled: false,
        label: '导入',
        visible: true
      },
      selectMaterial: {
        disabled: false,
        label: '选择物料',
        visible: true
      }
    })
  };
});

const sourcePermissions = computed<BillPermissionSet>(() => {
  return {
    fields: buildFieldPermissionItems(
      ['sourceBillNo', 'sourceType', 'supplierName', 'billDate', 'totalAmount', 'status', 'remark'],
      sourceFieldConfigMap.value
    ),
    buttons: {
      columnSetting: {
        disabled: false,
        label: '列显示',
        visible: true
      }
    }
  };
});

const attachmentPermissions = computed<BillPermissionSet>(() => {
  return {
    fields: buildFieldPermissionItems(
      ['fileName', 'fileType', 'fileSize', 'uploadUser', 'uploadTime', 'remark'],
      attachmentFieldConfigMap.value
    ),
    buttons: {
      columnSetting: {
        disabled: false,
        label: '列显示',
        visible: true
      }
    }
  };
});

const headerActionAccess = createBillPermissionAccessor(() => headerActionPermissions.value);

const showHeaderActions = computed(() => {
  return headerActionAccess.hasVisibleButtons(['save', 'audit', 'print', 'log', 'copy', 'void']);
});

const showStandaloneSubmit = computed(() => {
  return buttonConfigMap.value.submit.visible && !buttonConfigMap.value.save.visible;
});

const saveOptions = computed(() => {
  const options = [
    { command: '0', label: '仅保存' }
  ];
  if (buttonConfigMap.value.submit.visible && !buttonConfigMap.value.submit.disabled) {
    options.push({ command: '1', label: '保存并提交' });
  }
  return options;
});

const showBasicHeaderTab = computed(() => {
  return Object.values(basicFormPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showAuditHeaderTab = computed(() => {
  return Object.values(auditInfoPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showHeaderCard = computed(() => {
  return showBasicHeaderTab.value || showAuditHeaderTab.value;
});

const showItemsTab = computed(() => {
  return Object.values(detailPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showSourceDetailTab = computed(() => {
  return sourceBillList.value.length > 0 && Object.values(sourcePermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showAttachmentDetailTab = computed(() => {
  return attachmentList.value.length > 0 && Object.values(attachmentPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showDetailCard = computed(() => {
  return showItemsTab.value || showSourceDetailTab.value || showAttachmentDetailTab.value;
});

const auditDescriptionText = computed(() => {
  if (isCreateMode.value) {
    return '当前单据尚未保存，保存后进入正式流转状态';
  }
  return '当前状态与可执行操作由模块上下文和状态权限控制';
});

const getSafeVal = (value: unknown) => {
  const number = Number(value || 0);
  return Number.isFinite(number) ? number : 0;
};

const formatAmount = (value: number | null | undefined) => {
  return Number(value || 0).toLocaleString(undefined, {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

const syncItemAmount = (item: PurchaseOrderItem) => {
  const amount = calcPurchaseOrderItemAmounts(item);
  item.taxAmount = amount.taxAmount;
  item.totalAmount = amount.totalAmount;
};

const syncAllItemAmounts = () => {
  itemDetails.value.forEach((item) => {
    syncItemAmount(item);
  });
};

const summaryData = computed<SummaryItem[]>(() => {
  const totalAmount = itemDetails.value.reduce((sum, item) => {
    return sum + getSafeVal(item.totalAmount);
  }, 0);
  const totalQty = itemDetails.value.reduce((sum, item) => {
    return sum + getSafeVal(item.qty);
  }, 0);
  return [
    { label: '价税合计', value: formatAmount(totalAmount), type: 'amount', isMoney: true },
    { label: '合计数量', value: totalQty.toFixed(2) }
  ];
});

const moreSummaryData = computed<SummaryItem[]>(() => {
  const netAmount = itemDetails.value.reduce((sum, item) => {
    return sum + getSafeVal(item.qty) * getSafeVal(item.priceExcl);
  }, 0);
  const taxAmount = itemDetails.value.reduce((sum, item) => {
    return sum + getSafeVal(item.taxAmount);
  }, 0);
  return [
    { label: '不含税金额', value: formatAmount(netAmount), isMoney: true },
    { label: '总税额', value: formatAmount(taxAmount), isMoney: true },
    { label: '明细行数', value: itemDetails.value.length }
  ];
});

const resetPageData = () => {
  Object.assign(billInfo, createDefaultPurchaseOrderHeader());
  itemDetails.value = [createDefaultPurchaseOrderItem()];
  sourceBillList.value = [];
  attachmentList.value = [];
  activities.value = [];
  syncAllItemAmounts();
};

const applyDraftData = () => {
  const draft = purchaseOrderStore.headerDraft;
  Object.assign(billInfo, createDefaultPurchaseOrderHeader(), {
    billDate: draft.billDate,
    supplierId: draft.supplierId,
    settleType: draft.settleType,
    deptId: draft.deptId,
    userName: draft.userName,
    warehouseId: draft.warehouseId,
    currency: draft.currency,
    remark: draft.remark,
    status: draft.status,
    statusName: draft.statusName,
    createByName: draft.createByName
  });
  itemDetails.value = [createDefaultPurchaseOrderItem()];
  sourceBillList.value = [];
  attachmentList.value = [];
  activities.value = [];
  syncAllItemAmounts();
};

const applyDetailData = (detail: PurchaseOrderDetail) => {
  Object.assign(billInfo, createDefaultPurchaseOrderHeader(), detail.header);
  itemDetails.value = (detail.items || []).length
    ? detail.items.map((item) => {
      return { ...item };
    })
    : [createDefaultPurchaseOrderItem()];
  sourceBillList.value = (detail.sourceBills || []).map((item) => {
    return { ...item };
  });
  attachmentList.value = (detail.attachments || []).map((item) => {
    return { ...item };
  });
  activities.value = (detail.activities || []).map((item) => {
    return { ...item };
  });
  syncAllItemAmounts();
};

const loadContext = async (stateCode?: string) => {
  const res = await getPurchaseOrderContext(stateCode);
  context.value = res.data;
};

const loadDetail = async (id: number) => {
  const res = await getPurchaseOrderDetail(id);
  applyDetailData(res.data);
  await loadContext(billInfo.status);
};

const initPage = async () => {
  pageLoading.value = true;
  try {
    if (isCreateMode.value) {
      resetPageData();
      applyDraftData();
      await loadContext(billInfo.status);
      return;
    }
    if (currentOrderId.value) {
      await loadDetail(currentOrderId.value);
      return;
    }
    router.push('/purchase/order');
  } finally {
    pageLoading.value = false;
  }
};

const buildSavePayload = (): PurchaseOrderSavePayload => {
  return {
    id: billInfo.id,
    billDate: billInfo.billDate,
    supplierId: billInfo.supplierId,
    settleType: billInfo.settleType,
    warehouseId: billInfo.warehouseId,
    userName: billInfo.userName,
    currency: billInfo.currency,
    remark: billInfo.remark,
    items: itemDetails.value.map((item) => {
      return {
        id: item.id,
        itemCode: item.itemCode,
        itemName: item.itemName,
        spec: item.spec,
        unit: item.unit,
        qty: item.qty,
        priceExcl: item.priceExcl,
        taxRate: item.taxRate,
        note: item.note
      };
    })
  };
};

const validateItems = () => {
  if (!itemDetails.value.length) {
    throw new Error('采购订单明细不能为空');
  }
  itemDetails.value.forEach((item, index) => {
    const prefix = `第 ${index + 1} 行`;
    if (detailFieldConfigMap.value.itemCode.visible && !String(item.itemCode || '').trim()) {
      throw new Error(`${prefix}物料编码不能为空`);
    }
    if (detailFieldConfigMap.value.itemName.visible && !String(item.itemName || '').trim()) {
      throw new Error(`${prefix}物料名称不能为空`);
    }
    if (detailFieldConfigMap.value.qty.visible && (item.qty === null || item.qty === undefined)) {
      throw new Error(`${prefix}数量不能为空`);
    }
    if (detailFieldConfigMap.value.priceExcl.visible && (item.priceExcl === null || item.priceExcl === undefined)) {
      throw new Error(`${prefix}不含税单价不能为空`);
    }
    if (detailFieldConfigMap.value.taxRate.visible && (item.taxRate === null || item.taxRate === undefined)) {
      throw new Error(`${prefix}税率不能为空`);
    }
  });
};

const persistOrder = async () => {
  await basicFormRef.value?.validate();
  validateItems();
  syncAllItemAmounts();

  const payload = buildSavePayload();
  if (billInfo.id) {
    await updatePurchaseOrder(payload as PurchaseOrderSavePayload & { id: number });
    return billInfo.id;
  }

  const res = await createPurchaseOrder(payload);
  billInfo.id = res.data;
  purchaseOrderStore.clearHeaderDraft();
  return res.data;
};

const handleBack = () => {
  router.push('/purchase/order');
};

const handleSave = async (command: string) => {
  actionLoading.value = command === '1' ? 'submit' : 'save';
  submitLoading.value = true;
  try {
    const orderId = await persistOrder();
    if (command === '1') {
      await executePurchaseOrderAction(orderId, 'submit');
      ElMessage.success('保存并提交成功');
    } else {
      ElMessage.success('保存成功');
    }
    if (isCreateMode.value) {
      await router.replace({ path: '/purchase/order/detail', query: { id: orderId } });
      return;
    }
    await loadDetail(orderId);
  } finally {
    actionLoading.value = '';
    submitLoading.value = false;
  }
};

const handleSubmit = async () => {
  actionLoading.value = 'submit';
  submitLoading.value = true;
  try {
    const orderId = await persistOrder();
    await executePurchaseOrderAction(orderId, 'submit');
    ElMessage.success('提交成功');
    if (isCreateMode.value) {
      await router.replace({ path: '/purchase/order/detail', query: { id: orderId } });
      return;
    }
    await loadDetail(orderId);
  } finally {
    actionLoading.value = '';
    submitLoading.value = false;
  }
};

const handleStateAction = async (actionCode: 'audit' | 'ship' | 'complete') => {
  if (!billInfo.id) {
    return;
  }
  actionLoading.value = actionCode;
  submitLoading.value = true;
  try {
    await executePurchaseOrderAction(billInfo.id, actionCode);
    ElMessage.success('操作成功');
    await loadDetail(billInfo.id);
  } finally {
    actionLoading.value = '';
    submitLoading.value = false;
  }
};

const handleAudit = async () => {
  await handleStateAction('audit');
};

const handlePrint = () => {
  ElMessage.info('打印功能待接入');
};

const handleMoreAction = (command: string) => {
  if (command === 'log') {
    logVisible.value = true;
    return;
  }
  if (command === 'copy') {
    ElMessage.info('复制单据功能待接入');
    return;
  }
  if (command === 'void') {
    ElMessage.info('作废单据功能待接入');
  }
};

watch([() => route.query.id, () => route.query.mode], async () => {
  await initPage();
}, { immediate: true });

watch([showBasicHeaderTab, showAuditHeaderTab], ([basicVisible, auditVisible]) => {
  if (activeHeaderTab.value === 'basic' && !basicVisible && auditVisible) {
    activeHeaderTab.value = 'audit';
  }
  if (activeHeaderTab.value === 'audit' && !auditVisible && basicVisible) {
    activeHeaderTab.value = 'basic';
  }
}, { immediate: true });

watch([showItemsTab, showSourceDetailTab, showAttachmentDetailTab], ([itemsVisible, sourceVisible, attachmentVisible]) => {
  if (activeDetailTab.value === 'items' && !itemsVisible) {
    activeDetailTab.value = sourceVisible ? 'source' : (attachmentVisible ? 'files' : 'items');
  }
  if (activeDetailTab.value === 'source' && !sourceVisible) {
    activeDetailTab.value = itemsVisible ? 'items' : (attachmentVisible ? 'files' : 'source');
  }
  if (activeDetailTab.value === 'files' && !attachmentVisible) {
    activeDetailTab.value = itemsVisible ? 'items' : (sourceVisible ? 'source' : 'files');
  }
}, { immediate: true });
</script>

<style scoped lang="scss">
.purchase-order-detail {
  padding: 0;
  background-color: transparent;
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: 100%;
}

.detail-extra-actions {
  display: flex;
  align-items: center;
  gap: 8px;
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

.log-timeline-container {
  padding: 10px 20px;
}

.log-item-content {
  .operator {
    font-size: 13px;
    color: var(--app-primary);
    font-weight: 600;
  }

  .content {
    margin: 4px 0 0;
    font-size: 13px;
    color: #595959;
  }
}
</style>
