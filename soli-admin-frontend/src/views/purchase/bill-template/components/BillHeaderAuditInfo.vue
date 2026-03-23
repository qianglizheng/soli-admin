<template>
  <el-descriptions v-if="hasVisibleFields" :column="3" border size="small">
    <el-descriptions-item
      v-if="permissionAccess.isFieldVisible('createByName')"
      :label="permissionAccess.getFieldLabel('createByName')"
    >
      {{ model.createByName }}
    </el-descriptions-item>
    <el-descriptions-item
      v-if="permissionAccess.isFieldVisible('statusName')"
      :label="permissionAccess.getFieldLabel('statusName')"
    >
      <el-tag size="small" :type="tagType">{{ model.statusName }}</el-tag>
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
import {
  createBillPermissionAccessor,
  type BillPermissionSource
} from '@/components/Bill/billPermission';

interface BillHeaderAuditInfoModel {
  createByName: string;
  status: string;
  statusName: string;
}

interface Props {
  model: BillHeaderAuditInfoModel;
  descriptionText?: string;
  permissions?: BillPermissionSource;
}

const props = defineProps<Props>();
const auditFieldKeys = ['createByName', 'statusName', 'descriptionText'];
const defaultFieldLabels = {
  createByName: '制单人',
  statusName: '审核状态',
  descriptionText: '说明'
} as const;
const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

/**
 * 根据状态生成审核标签类型。
 */
const tagType = computed(() => {
  if (props.model.status === '1') {
    return 'success';
  }
  return 'info';
});

/**
 * 判断审核信息区域是否还存在可见字段。
 */
const hasVisibleFields = computed(() => {
  return permissionAccess.hasVisibleFields(auditFieldKeys);
});
</script>
