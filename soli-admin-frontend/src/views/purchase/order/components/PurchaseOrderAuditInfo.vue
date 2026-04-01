<template>
  <el-descriptions v-if="hasVisibleFields" :column="3" border size="small">
    <el-descriptions-item
      v-if="permissionAccess.isFieldVisible('createByName')"
      :label="permissionAccess.getFieldLabel('createByName')"
    >
      {{ model.createByName || '-' }}
    </el-descriptions-item>
    <el-descriptions-item
      v-if="permissionAccess.isFieldVisible('statusName')"
      :label="permissionAccess.getFieldLabel('statusName')"
    >
      <el-tag size="small" :type="tagType">{{ model.statusName || '-' }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item
      v-if="descriptionText && permissionAccess.isFieldVisible('descriptionText')"
      :label="permissionAccess.getFieldLabel('descriptionText')"
    >
      {{ descriptionText }}
    </el-descriptions-item>
  </el-descriptions>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import type { PurchaseOrderStatusCode } from '@/api/purchaseOrder';
import {
  createBillPermissionAccessor,
  type BillPermissionSource
} from '@/components/Bill/billPermission';
import { getStatusType } from '../purchaseOrderShared';

interface PurchaseOrderAuditInfoModel {
  createByName: string;
  status: PurchaseOrderStatusCode;
  statusName: string;
}

interface Props {
  model: PurchaseOrderAuditInfoModel;
  descriptionText?: string;
  permissions?: BillPermissionSource;
}

const props = defineProps<Props>();
const auditFieldKeys = ['createByName', 'statusName', 'descriptionText'];
const defaultFieldLabels = {
  createByName: '制单人',
  statusName: '状态',
  descriptionText: '说明'
} as const;

const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

const tagType = computed(() => {
  return getStatusType(props.model.status);
});

const hasVisibleFields = computed(() => {
  return permissionAccess.hasVisibleFields(auditFieldKeys);
});
</script>
