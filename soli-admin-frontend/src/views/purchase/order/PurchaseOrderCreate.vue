<template>
  <div class="app-container create-page">
    <BillDetailHeaderCard title="新建采购订单" @back="handleBack" />

    <div v-if="showBasicCard" class="tech-card header-card">
      <div class="card-header">
        <div class="card-title">基础信息</div>
        <div class="card-desc">继续前请先完善必填项</div>
      </div>

      <PurchaseOrderBasicForm
        ref="basicFormRef"
        :model="formData"
        :permissions="standardPermissions"
        show-placeholders
      />
    </div>

    <div v-if="showAuditCard" class="tech-card header-card">
      <div class="card-header">
        <div class="card-title">审核信息</div>
        <div class="card-desc">继续后进入单据明细页</div>
      </div>

      <PurchaseOrderAuditInfo
        :model="formData"
        :permissions="auditPermissions"
        description-text="继续后进入单据明细页"
      />
    </div>

    <div v-if="showActionCard" class="tech-card action-card">
      <div class="action-bar">
        <el-button
          v-if="actionPermissionAccess.isButtonVisible('cancel')"
          :disabled="actionPermissionAccess.isButtonReadonly('cancel')"
          @click="handleBack"
        >
          {{ actionPermissionAccess.getButtonLabel('cancel') }}
        </el-button>
        <el-button
          v-if="actionPermissionAccess.isButtonVisible('continue')"
          type="primary"
          :disabled="actionPermissionAccess.isButtonReadonly('continue')"
          @click="handleContinue"
        >
          {{ actionPermissionAccess.getButtonLabel('continue') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import type {
  BillFieldPermissionItem,
  BillPermissionSet
} from '@/components/Bill/billPermission';
import {
  createBillPermissionAccessor,
  createEmptyBillPermissionSet
} from '@/components/Bill/billPermission';
import BillDetailHeaderCard from '@/components/Bill/BillDetailHeaderCard.vue';
import { getPurchaseOrderContext } from '@/api/purchaseOrder';
import { usePurchaseOrderStore } from '@/store/modules/purchaseOrder';
import { useUserStore } from '@/store/modules/user';
import { buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import PurchaseOrderAuditInfo from './components/PurchaseOrderAuditInfo.vue';
import PurchaseOrderBasicForm from './components/PurchaseOrderBasicForm.vue';
import type { PurchaseOrderHeaderDraft } from './purchaseOrderShared';

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

interface PurchaseOrderBasicFormExpose {
  validate: () => Promise<void>;
}

const router = useRouter();
const userStore = useUserStore();
const purchaseOrderStore = usePurchaseOrderStore();
const basicFormRef = ref<PurchaseOrderBasicFormExpose>();
const context = ref<ModuleContext | null>(null);

const formData = reactive<PurchaseOrderHeaderDraft>({
  ...purchaseOrderStore.headerDraft,
  createByName: purchaseOrderStore.headerDraft.createByName || userStore.name || '管理员'
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

const buildFieldPermissionItems = (fieldKeys: HeaderFieldCode[]) => {
  return fieldKeys.reduce<Record<string, BillFieldPermissionItem>>((result, fieldKey) => {
    const fieldConfig = headerFieldConfigMap.value[fieldKey];
    result[fieldKey] = {
      editable: fieldConfig.editable,
      label: fieldConfig.label,
      visible: fieldConfig.visible
    };
    return result;
  }, {});
};

const headerFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(context.value?.fieldConfigs || {}, 'header', headerFieldFallbackMap);
});

const standardPermissions = computed<BillPermissionSet>(() => {
  return {
    buttons: {},
    fields: buildFieldPermissionItems(['billDate', 'supplierId', 'settleType', 'warehouseId', 'userName', 'currency', 'remark'])
  };
});

const auditPermissions = computed<BillPermissionSet>(() => {
  const fields = buildFieldPermissionItems(['createByName', 'statusName']);
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

const actionPermissions = computed<BillPermissionSet>(() => {
  return {
    ...createEmptyBillPermissionSet(),
    buttons: {
      cancel: {
        disabled: false,
        label: '取消',
        visible: true
      },
      continue: {
        disabled: false,
        label: '继续',
        visible: true
      }
    }
  };
});

const actionPermissionAccess = createBillPermissionAccessor(() => actionPermissions.value, {
  buttonLabels: {
    cancel: '取消',
    continue: '继续'
  }
});

const showBasicCard = computed(() => {
  return Object.values(standardPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showAuditCard = computed(() => {
  return Object.values(auditPermissions.value.fields || {}).some((item) => {
    return item.visible !== false;
  });
});

const showActionCard = computed(() => {
  return actionPermissionAccess.hasVisibleButtons(['cancel', 'continue']);
});

const loadContext = async () => {
  const res = await getPurchaseOrderContext(formData.status);
  context.value = res.data;
};

const handleBack = () => {
  router.push('/purchase/order');
};

const handleContinue = async () => {
  if (!actionPermissionAccess.isButtonVisible('continue') || actionPermissionAccess.isButtonReadonly('continue')) {
    return;
  }
  try {
    await basicFormRef.value?.validate();
  } catch {
    ElMessage.warning('请先完善基础信息中的必填项');
    return;
  }

  purchaseOrderStore.setHeaderDraft({
    ...formData,
    createByName: formData.createByName || userStore.name || '管理员',
    status: 'unaudited',
    statusName: '未审核'
  });

  router.push({
    path: '/purchase/order/detail',
    query: { mode: 'create' }
  });
};

onMounted(async () => {
  await loadContext();
});
</script>

<style scoped lang="scss">
.create-page {
  display: flex;
  flex-direction: column;
  gap: 8px;
  height: 100%;
  min-height: 0;
  padding: 0;
  background-color: transparent;
  overflow: auto;
}

.header-card {
  flex-shrink: 0;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: var(--el-text-color-primary);
}

.card-desc {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.action-card {
  flex-shrink: 0;
}

.action-bar {
  display: flex;
  justify-content: center;
  gap: 12px;
}
</style>
