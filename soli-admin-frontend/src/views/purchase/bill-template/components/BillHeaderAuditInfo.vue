<template>
  <el-descriptions v-if="hasVisibleFields" :column="3" border size="small">
    <el-descriptions-item v-if="isFieldVisible('createByName')" :label="getFieldTitle('createByName')">{{ model.createByName }}</el-descriptions-item>
    <el-descriptions-item v-if="isFieldVisible('statusName')" :label="getFieldTitle('statusName')">
      <el-tag size="small" :type="tagType">{{ model.statusName }}</el-tag>
    </el-descriptions-item>
    <el-descriptions-item v-if="descriptionText && isFieldVisible('descriptionText')" :label="getFieldTitle('descriptionText')">{{ descriptionText }}</el-descriptions-item>
  </el-descriptions>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import {
  getFieldLabel,
  hasAnyVisiblePermission,
  isPermissionVisible,
  type BillPermissionSet
} from '@/components/Bill/billPermission';

interface BillHeaderAuditInfoModel {
  createByName: string;
  status: string;
  statusName: string;
}

interface Props {
  model: BillHeaderAuditInfoModel;
  descriptionText?: string;
  permissions?: BillPermissionSet;
}

const props = defineProps<Props>();
const auditFieldKeys = ['createByName', 'statusName', 'descriptionText'];
const defaultFieldLabels = {
  createByName: '制单人',
  statusName: '审核状态',
  descriptionText: '说明'
} as const;

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
 * 判断审核字段是否允许显示。
 */
const isFieldVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'fields', key);
};

/**
 * 读取审核字段标题，优先使用权限配置中的自定义标题。
 */
const getFieldTitle = (key: keyof typeof defaultFieldLabels) => {
  return getFieldLabel(props.permissions, key, defaultFieldLabels[key]);
};

/**
 * 判断审核信息区域是否还存在可见字段。
 */
const hasVisibleFields = computed(() => {
  return hasAnyVisiblePermission(props.permissions, 'fields', auditFieldKeys);
});
</script>
