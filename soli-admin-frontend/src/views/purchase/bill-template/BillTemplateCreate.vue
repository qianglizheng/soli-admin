<template>
  <div class="app-container create-page">
    <DetailHeader title="新建采购单" @back="handleBack" />

    <div v-if="showBasicCard" class="tech-card header-card">
      <div class="card-header">
        <div class="card-title">基础信息</div>
        <div class="card-desc">继续前请先完善必填项</div>
      </div>

      <BillHeaderBasicForm ref="basicFormRef" :model="formData" :permissions="standardPermissions" show-placeholders />
    </div>

    <div v-if="showAuditCard" class="tech-card header-card">
      <div class="card-header">
        <div class="card-title">审核信息</div>
        <div class="card-desc">新建后进入单据明细继续维护</div>
      </div>

      <BillHeaderAuditInfo :model="formData" :permissions="standardPermissions" description-text="继续后进入单据明细页" />
    </div>

    <div v-if="showActionCard" class="tech-card action-card">
      <div class="action-bar">
        <el-button
          v-if="permissionAccess.isButtonVisible('cancel')"
          :disabled="permissionAccess.isButtonReadonly('cancel')"
          @click="handleBack"
        >
          {{ permissionAccess.getButtonLabel('cancel') }}
        </el-button>
        <el-button
          v-if="permissionAccess.isButtonVisible('continue')"
          type="primary"
          :disabled="permissionAccess.isButtonReadonly('continue')"
          @click="handleContinue"
        >
          {{ permissionAccess.getButtonLabel('continue') }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import DetailHeader from '@/components/Bill/BillDetailHeaderCard.vue';
import {
  createBillPermissionAccessor,
  createEmptyBillPermissionConfig,
  createEmptyBillPagePermissions,
} from '@/components/Bill/billPermission';
import { useBillTemplateStore, type BillTemplateHeaderDraft } from '@/store/modules/billTemplate';
import { useUserStore } from '@/store/modules/user';
import { fetchBillTemplatePagePermissions } from './billPermissionMock';
import BillHeaderBasicForm from './components/BillHeaderBasicForm.vue';
import BillHeaderAuditInfo from './components/BillHeaderAuditInfo.vue';

const router = useRouter();
const userStore = useUserStore();
const billTemplateStore = useBillTemplateStore();
const basicFormRef = ref<{ validate: () => Promise<void> }>();
const pagePermissions = ref(createEmptyBillPagePermissions());
const emptyPermissionConfig = createEmptyBillPermissionConfig();
/**
 * 当前页标准单头权限。
 */
const standardPermissions = computed(() => {
  return pagePermissions.value.header.standard || emptyPermissionConfig;
});
const basicFieldKeys = ['billDate', 'supplierId', 'settleType', 'warehouseId', 'userName', 'currency', 'remark'];
const auditFieldKeys = ['createByName', 'statusName', 'descriptionText'];
const permissionAccess = createBillPermissionAccessor(() => standardPermissions.value, {
  buttonLabels: {
    cancel: '取消',
    continue: '继续'
  }
});

const formData = reactive<BillTemplateHeaderDraft>({
  ...billTemplateStore.headerDraft,
  createByName: billTemplateStore.headerDraft.createByName || userStore.name || '管理员'
});

/**
 * 判断基础信息卡片是否需要显示。
 */
const showBasicCard = computed(() => {
  return permissionAccess.hasVisibleFields(basicFieldKeys);
});

/**
 * 判断审核信息卡片是否需要显示。
 */
const showAuditCard = computed(() => {
  return permissionAccess.hasVisibleFields(auditFieldKeys);
});

/**
 * 判断底部操作区是否需要显示。
 */
const showActionCard = computed(() => {
  return permissionAccess.hasVisibleButtons(['cancel', 'continue']);
});

const cardValidators = [
  {
    title: '基础信息',
    validate: async () => {
      await basicFormRef.value?.validate();
    }
  },
  {
    title: '审核信息',
    validate: async () => {
      return true;
    }
  }
];

/**
 * 返回单据模板列表页。
 */
const handleBack = () => {
  router.push('/purchase/bill-template');
};

/**
 * 校验单头卡片并进入详情页。
 */
const handleContinue = async () => {
  if (!permissionAccess.isButtonVisible('continue') || permissionAccess.isButtonReadonly('continue')) {
    return;
  }
  for (const card of cardValidators) {
    try {
      await card.validate();
    } catch {
      ElMessage.warning(`请先完善${card.title}卡片中的必填项`);
      return;
    }
  }

  billTemplateStore.setHeaderDraft({
    ...formData,
    createByName: formData.createByName || userStore.name || '管理员',
    status: '0',
    statusName: '未审核'
  });

  router.push({
    path: '/purchase/bill-template/detail',
    query: { mode: 'create' }
  });
};

/**
 * 加载新建页权限，按当前单据状态获取 mock 权限数据。
 */
onMounted(async () => {
  pagePermissions.value = await fetchBillTemplatePagePermissions('create', formData.status);
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
