<template>
  <div class="app-container purchase-order-editor">
    <div class="tech-card editor-header">
      <el-page-header class="custom-header" @back="handleBack">
        <template #content>
          <div class="header-info">
            <span class="title-text">{{ pageTitle }}</span>
            <span v-if="header.billNo" class="bill-no">{{ header.billNo }}</span>
            <el-tag size="small" effect="plain" :type="getStatusType(header.status)">{{ header.statusName }}</el-tag>
          </div>
        </template>
        <template #extra>
          <div class="header-actions">
            <el-button
              v-if="buttonConfigMap.save.visible"
              type="primary"
              :disabled="buttonConfigMap.save.disabled || submitLoading"
              :loading="actionLoading === 'save'"
              @click="handleSave"
            >
              {{ buttonConfigMap.save.label }}
            </el-button>
            <el-button
              v-if="buttonConfigMap.submit.visible"
              type="warning"
              :disabled="buttonConfigMap.submit.disabled || submitLoading"
              :loading="actionLoading === 'submit'"
              @click="handleSubmit"
            >
              {{ buttonConfigMap.submit.label }}
            </el-button>
            <el-button
              v-if="buttonConfigMap.audit.visible"
              type="success"
              :disabled="buttonConfigMap.audit.disabled || submitLoading"
              :loading="actionLoading === 'audit'"
              @click="handleStateAction('audit')"
            >
              {{ buttonConfigMap.audit.label }}
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
      </el-page-header>
    </div>

    <BillDetailFormCard v-if="showHeaderSection" v-model="headerExpanded" title="单头信息">
      <el-form ref="headerFormRef" :model="header" :rules="headerRules" label-width="90px" class="header-form">
        <el-row :gutter="20">
          <el-col v-if="headerFieldConfigMap.billDate.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.billDate.label" prop="billDate">
              <el-date-picker
                v-model="header.billDate"
                type="date"
                value-format="YYYY-MM-DD"
                :placeholder="headerFieldConfigMap.billDate.placeholder"
                :disabled="!headerFieldConfigMap.billDate.editable"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.supplierId.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.supplierId.label" prop="supplierId">
              <el-select
                v-model="header.supplierId"
                :placeholder="headerFieldConfigMap.supplierId.placeholder"
                :disabled="!headerFieldConfigMap.supplierId.editable"
                style="width: 100%"
              >
                <el-option v-for="item in supplierOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.settleType.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.settleType.label" prop="settleType">
              <el-select
                v-model="header.settleType"
                :placeholder="headerFieldConfigMap.settleType.placeholder"
                :disabled="!headerFieldConfigMap.settleType.editable"
                style="width: 100%"
              >
                <el-option v-for="item in settleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.warehouseId.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.warehouseId.label" prop="warehouseId">
              <el-select
                v-model="header.warehouseId"
                :placeholder="headerFieldConfigMap.warehouseId.placeholder"
                :disabled="!headerFieldConfigMap.warehouseId.editable"
                style="width: 100%"
              >
                <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.userName.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.userName.label" prop="userName">
              <el-input
                v-model="header.userName"
                :placeholder="headerFieldConfigMap.userName.placeholder"
                :disabled="!headerFieldConfigMap.userName.editable"
              />
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.currency.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.currency.label" prop="currency">
              <el-select
                v-model="header.currency"
                :placeholder="headerFieldConfigMap.currency.placeholder"
                :disabled="!headerFieldConfigMap.currency.editable"
                style="width: 100%"
              >
                <el-option v-for="item in currencyOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.createByName.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.createByName.label">
              <el-input :model-value="header.createByName || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.statusName.visible" :span="6">
            <el-form-item :label="headerFieldConfigMap.statusName.label">
              <el-tag :type="getStatusType(header.status)">{{ header.statusName }}</el-tag>
            </el-form-item>
          </el-col>
          <el-col v-if="headerFieldConfigMap.remark.visible" :span="24">
            <el-form-item :label="headerFieldConfigMap.remark.label" prop="remark">
              <el-input
                v-model="header.remark"
                type="textarea"
                :rows="3"
                :placeholder="headerFieldConfigMap.remark.placeholder"
                :disabled="!headerFieldConfigMap.remark.editable"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </BillDetailFormCard>

    <div v-if="showDetailSection" class="tech-card section-card">
      <div class="section-header">
        <div class="section-title">明细</div>
        <div class="section-actions">
          <el-button
            v-if="buttonConfigMap.add.visible"
            type="primary"
            plain
            icon="Plus"
            :disabled="buttonConfigMap.add.disabled"
            @click="handleAddItem"
          >
            {{ buttonConfigMap.add.label }}
          </el-button>
          <el-button
            v-if="buttonConfigMap.delete.visible"
            type="danger"
            plain
            icon="Delete"
            :disabled="buttonConfigMap.delete.disabled || !selectedItems.length"
            @click="handleDeleteItems"
          >
            {{ buttonConfigMap.delete.label }}
          </el-button>
        </div>
      </div>

      <el-table :data="items" border size="small" @selection-change="handleItemSelectionChange">
        <el-table-column v-if="buttonConfigMap.delete.visible" type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column v-if="detailFieldConfigMap.itemCode.visible" :label="detailFieldConfigMap.itemCode.label" min-width="140">
          <template #default="{ row }">
            <el-input v-model="row.itemCode" :disabled="!detailFieldConfigMap.itemCode.editable" />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.itemName.visible" :label="detailFieldConfigMap.itemName.label" min-width="180">
          <template #default="{ row }">
            <el-input v-model="row.itemName" :disabled="!detailFieldConfigMap.itemName.editable" />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.spec.visible" :label="detailFieldConfigMap.spec.label" min-width="140">
          <template #default="{ row }">
            <el-input v-model="row.spec" :disabled="!detailFieldConfigMap.spec.editable" />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.unit.visible" :label="detailFieldConfigMap.unit.label" width="100">
          <template #default="{ row }">
            <el-input v-model="row.unit" :disabled="!detailFieldConfigMap.unit.editable" />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.qty.visible" :label="detailFieldConfigMap.qty.label" width="120" align="right">
          <template #default="{ row }">
            <el-input-number
              v-model="row.qty"
              :controls="false"
              :disabled="!detailFieldConfigMap.qty.editable"
              style="width: 100%"
              @change="syncItemAmount(row)"
            />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.priceExcl.visible" :label="detailFieldConfigMap.priceExcl.label" width="140" align="right">
          <template #default="{ row }">
            <el-input-number
              v-model="row.priceExcl"
              :controls="false"
              :precision="4"
              :disabled="!detailFieldConfigMap.priceExcl.editable"
              style="width: 100%"
              @change="syncItemAmount(row)"
            />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.taxRate.visible" :label="detailFieldConfigMap.taxRate.label" width="120" align="right">
          <template #default="{ row }">
            <el-input-number
              v-model="row.taxRate"
              :controls="false"
              :disabled="!detailFieldConfigMap.taxRate.editable"
              style="width: 100%"
              @change="syncItemAmount(row)"
            />
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.taxAmount.visible" :label="detailFieldConfigMap.taxAmount.label" width="120" align="right">
          <template #default="{ row }">
            <span class="amount-text">{{ formatAmount(row.taxAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="detailFieldConfigMap.totalAmount.visible" :label="detailFieldConfigMap.totalAmount.label" width="140" align="right">
          <template #default="{ row }">
            <span class="amount-text total-amount">{{ formatAmount(row.totalAmount) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="showSourceSection" class="tech-card section-card">
      <div class="section-header">
        <div class="section-title">来源单据</div>
      </div>
      <el-table :data="sourceBills" border size="small">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column v-if="sourceFieldConfigMap.sourceBillNo.visible" :label="sourceFieldConfigMap.sourceBillNo.label" prop="sourceBillNo" min-width="160" />
        <el-table-column v-if="sourceFieldConfigMap.sourceType.visible" :label="sourceFieldConfigMap.sourceType.label" prop="sourceType" width="120" />
        <el-table-column v-if="sourceFieldConfigMap.supplierName.visible" :label="sourceFieldConfigMap.supplierName.label" prop="supplierName" min-width="180" />
        <el-table-column v-if="sourceFieldConfigMap.billDate.visible" :label="sourceFieldConfigMap.billDate.label" prop="billDate" width="120" />
        <el-table-column v-if="sourceFieldConfigMap.totalAmount.visible" :label="sourceFieldConfigMap.totalAmount.label" width="140" align="right">
          <template #default="{ row }">
            <span class="amount-text">{{ formatAmount(row.totalAmount) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="sourceFieldConfigMap.status.visible" :label="sourceFieldConfigMap.status.label" prop="status" width="120" />
        <el-table-column v-if="sourceFieldConfigMap.remark.visible" :label="sourceFieldConfigMap.remark.label" prop="remark" min-width="160" />
      </el-table>
    </div>

    <div v-if="showAttachmentSection" class="tech-card section-card">
      <div class="section-header">
        <div class="section-title">附件</div>
      </div>
      <el-table :data="attachments" border size="small">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column v-if="attachmentFieldConfigMap.fileName.visible" :label="attachmentFieldConfigMap.fileName.label" prop="fileName" min-width="220" />
        <el-table-column v-if="attachmentFieldConfigMap.fileType.visible" :label="attachmentFieldConfigMap.fileType.label" prop="fileType" width="120" />
        <el-table-column v-if="attachmentFieldConfigMap.fileSize.visible" :label="attachmentFieldConfigMap.fileSize.label" prop="fileSize" width="120" />
        <el-table-column v-if="attachmentFieldConfigMap.uploadUser.visible" :label="attachmentFieldConfigMap.uploadUser.label" prop="uploadUser" width="120" />
        <el-table-column v-if="attachmentFieldConfigMap.uploadTime.visible" :label="attachmentFieldConfigMap.uploadTime.label" prop="uploadTime" width="180" />
        <el-table-column v-if="attachmentFieldConfigMap.remark.visible" :label="attachmentFieldConfigMap.remark.label" prop="remark" min-width="160" />
      </el-table>
    </div>

    <FooterSummaryCard :items="summaryItems" :more-items="moreSummaryItems" class="summary-card" />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import type {
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
import FooterSummaryCard, { type SummaryItem } from '@/components/Bill/FooterSummaryCard.vue';
import BillDetailFormCard from '@/components/Bill/BillDetailFormCard.vue';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap
} from '@/utils/moduleContext';
import {
  calcPurchaseOrderItemAmounts,
  createDefaultPurchaseOrderHeader,
  createDefaultPurchaseOrderItem,
  currencyOptions,
  getStatusType,
  settleTypeOptions,
  supplierOptions,
  warehouseOptions
} from '../purchaseOrderShared';

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
type EditorButtonCode = 'save' | 'submit' | 'audit' | 'ship' | 'complete' | 'add' | 'delete';

interface Props {
  mode: 'create' | 'detail';
}

const props = defineProps<Props>();
const route = useRoute();
const router = useRouter();
const headerFormRef = ref<FormInstance>();
const headerExpanded = ref(true);
const context = ref<ModuleContext | null>(null);
const submitLoading = ref(false);
const actionLoading = ref('');
const selectedItems = ref<PurchaseOrderItem[]>([]);
const sourceBills = ref<PurchaseOrderSourceBill[]>([]);
const attachments = ref<PurchaseOrderAttachment[]>([]);
const header = reactive<PurchaseOrderHeader>(createDefaultPurchaseOrderHeader());
const items = ref<PurchaseOrderItem[]>([createDefaultPurchaseOrderItem()]);

const pageTitle = computed(() => {
  return props.mode === 'create' ? '新建采购订单' : '采购订单';
});

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

const buttonFallbackMap: Record<EditorButtonCode, { label: string; visible?: boolean; disabled?: boolean }> = {
  save: { label: '保存' },
  submit: { label: '提交' },
  audit: { label: '审核' },
  ship: { label: '发运' },
  complete: { label: '完成' },
  add: { label: '新增行' },
  delete: { label: '删除行' }
};

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

const showHeaderSection = computed(() => {
  return Object.values(headerFieldConfigMap.value).some((item) => item.visible);
});

const showDetailSection = computed(() => {
  return Object.values(detailFieldConfigMap.value).some((item) => item.visible);
});

const showSourceSection = computed(() => {
  return sourceBills.value.length > 0 && Object.values(sourceFieldConfigMap.value).some((item) => item.visible);
});

const showAttachmentSection = computed(() => {
  return attachments.value.length > 0 && Object.values(attachmentFieldConfigMap.value).some((item) => item.visible);
});

const headerRules = computed<FormRules>(() => {
  const rules: FormRules = {};
  (Object.keys(headerFieldFallbackMap) as HeaderFieldCode[]).forEach((fieldCode) => {
    const fieldConfig = headerFieldConfigMap.value[fieldCode];
    if (!fieldConfig.visible || !fieldConfig.editable || !fieldConfig.required) {
      return;
    }
    const message = fieldCode === 'remark'
      ? `请输入${fieldConfig.label}`
      : (fieldCode === 'userName' ? `请输入${fieldConfig.label}` : `请选择${fieldConfig.label}`);
    rules[fieldCode] = [{
      required: true,
      message,
      trigger: fieldCode === 'userName' || fieldCode === 'remark' ? 'blur' : 'change'
    }];
  });
  return rules;
});

const summaryItems = computed<SummaryItem[]>(() => {
  const totalQty = items.value.reduce((sum, item) => {
    return sum + Number(item.qty || 0);
  }, 0);
  const totalAmount = items.value.reduce((sum, item) => {
    return sum + Number(item.totalAmount || 0);
  }, 0);
  return [
    { label: '价税合计', value: formatAmount(totalAmount), type: 'amount', isMoney: true },
    { label: '合计数量', value: totalQty.toFixed(2) }
  ];
});

const moreSummaryItems = computed<SummaryItem[]>(() => {
  const netAmount = items.value.reduce((sum, item) => {
    return sum + Number(item.qty || 0) * Number(item.priceExcl || 0);
  }, 0);
  const taxAmount = items.value.reduce((sum, item) => {
    return sum + Number(item.taxAmount || 0);
  }, 0);
  return [
    { label: '不含税金额', value: formatAmount(netAmount), isMoney: true },
    { label: '总税额', value: formatAmount(taxAmount), isMoney: true },
    { label: '明细行数', value: String(items.value.length) }
  ];
});

const handleBack = () => {
  router.push('/purchase/order');
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
  items.value.forEach((item) => {
    syncItemAmount(item);
  });
};

const handleItemSelectionChange = (selection: PurchaseOrderItem[]) => {
  selectedItems.value = selection;
};

const handleAddItem = () => {
  if (!buttonConfigMap.value.add.visible || buttonConfigMap.value.add.disabled) {
    return;
  }
  items.value = [...items.value, createDefaultPurchaseOrderItem()];
};

const handleDeleteItems = () => {
  if (!buttonConfigMap.value.delete.visible || buttonConfigMap.value.delete.disabled || !selectedItems.value.length) {
    return;
  }
  items.value = items.value.filter((item) => !selectedItems.value.includes(item));
  selectedItems.value = [];
};

const resetEditorData = () => {
  Object.assign(header, createDefaultPurchaseOrderHeader());
  items.value = [createDefaultPurchaseOrderItem()];
  sourceBills.value = [];
  attachments.value = [];
  selectedItems.value = [];
};

const applyDetailData = (detail: PurchaseOrderDetail) => {
  Object.assign(header, createDefaultPurchaseOrderHeader(), detail.header);
  items.value = (detail.items || []).length ? detail.items.map((item) => ({ ...item })) : [createDefaultPurchaseOrderItem()];
  sourceBills.value = (detail.sourceBills || []).map((item) => ({ ...item }));
  attachments.value = (detail.attachments || []).map((item) => ({ ...item }));
  syncAllItemAmounts();
};

const loadContext = async (stateCode?: string) => {
  const res = await getPurchaseOrderContext(stateCode);
  context.value = res.data;
};

const loadDetail = async () => {
  const id = Number(route.query.id);
  if (!id) {
    ElMessage.error('采购订单 ID 不存在');
    router.push('/purchase/order');
    return;
  }
  const res = await getPurchaseOrderDetail(id);
  applyDetailData(res.data);
  await loadContext(header.status);
};

const buildSavePayload = (): PurchaseOrderSavePayload => {
  return {
    id: header.id,
    billDate: header.billDate,
    supplierId: header.supplierId,
    settleType: header.settleType,
    warehouseId: header.warehouseId,
    userName: header.userName,
    currency: header.currency,
    remark: header.remark,
    items: items.value.map((item) => ({
      id: item.id,
      itemCode: item.itemCode,
      itemName: item.itemName,
      spec: item.spec,
      unit: item.unit,
      qty: item.qty,
      priceExcl: item.priceExcl,
      taxRate: item.taxRate,
      note: item.note
    }))
  };
};

const validateItems = () => {
  if (!items.value.length) {
    throw new Error('采购订单明细不能为空');
  }
  items.value.forEach((item, index) => {
    const prefix = `第 ${index + 1} 行`;
    if (detailFieldConfigMap.value.itemCode.visible && detailFieldConfigMap.value.itemCode.required && !item.itemCode) {
      throw new Error(`${prefix}物料编码不能为空`);
    }
    if (detailFieldConfigMap.value.itemName.visible && detailFieldConfigMap.value.itemName.required && !item.itemName) {
      throw new Error(`${prefix}物料名称不能为空`);
    }
    if (detailFieldConfigMap.value.qty.visible && detailFieldConfigMap.value.qty.required && (item.qty === null || item.qty === undefined)) {
      throw new Error(`${prefix}数量不能为空`);
    }
    if (detailFieldConfigMap.value.priceExcl.visible && detailFieldConfigMap.value.priceExcl.required && (item.priceExcl === null || item.priceExcl === undefined)) {
      throw new Error(`${prefix}不含税单价不能为空`);
    }
    if (detailFieldConfigMap.value.taxRate.visible && detailFieldConfigMap.value.taxRate.required && (item.taxRate === null || item.taxRate === undefined)) {
      throw new Error(`${prefix}税率不能为空`);
    }
  });
};

const persistOrder = async () => {
  await headerFormRef.value?.validate();
  validateItems();
  syncAllItemAmounts();

  const payload = buildSavePayload();
  if (header.id) {
    await updatePurchaseOrder(payload as PurchaseOrderSavePayload & { id: number });
    return header.id;
  }

  const res = await createPurchaseOrder(payload);
  header.id = res.data;
  return res.data;
};

const handleSave = async () => {
  actionLoading.value = 'save';
  submitLoading.value = true;
  try {
    const orderId = await persistOrder();
    ElMessage.success('保存成功');
    if (props.mode === 'create') {
      await router.replace({ path: '/purchase/order/detail', query: { id: orderId } });
    }
    await loadDetail();
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
    if (props.mode === 'create') {
      await router.replace({ path: '/purchase/order/detail', query: { id: orderId } });
    }
    await loadDetail();
  } finally {
    actionLoading.value = '';
    submitLoading.value = false;
  }
};

const handleStateAction = async (actionCode: 'audit' | 'ship' | 'complete') => {
  if (!header.id) {
    return;
  }
  actionLoading.value = actionCode;
  submitLoading.value = true;
  try {
    await executePurchaseOrderAction(header.id, actionCode);
    ElMessage.success('操作成功');
    await loadDetail();
  } finally {
    actionLoading.value = '';
    submitLoading.value = false;
  }
};

onMounted(async () => {
  if (props.mode === 'create') {
    resetEditorData();
    await loadContext(header.status);
    syncAllItemAmounts();
    return;
  }
  await loadDetail();
});
</script>

<style scoped lang="scss">
.purchase-order-editor {
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: 100%;
  min-height: 0;
}

.editor-header {
  padding: 6px 16px !important;
}

.custom-header {
  width: 100%;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-text {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.bill-no {
  padding: 2px 8px;
  border: 1px solid var(--app-border-color);
  border-radius: 4px;
  background: var(--app-bg-color);
  font-family: 'Consolas', monospace;
  font-size: 12px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-form {
  padding-top: 16px;
}

.section-card {
  padding: 0 !important;
  overflow: hidden;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
}

.section-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.section-actions {
  display: flex;
  gap: 8px;
}

.amount-text {
  font-family: 'Consolas', monospace;
}

.total-amount {
  color: #cf1322;
  font-weight: 700;
}

:deep(.summary-card) {
  flex-shrink: 0;
  background: #fff;
  border-radius: 4px;
  border: 1px solid var(--app-border-color);
  .footer-summary-container {
    border-top: none;
  }
}
</style>
