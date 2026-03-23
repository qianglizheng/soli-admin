<template>
  <el-popover v-if="permissionAccess.isButtonVisible('columnSetting')" placement="bottom-end" trigger="click" :width="220">
    <template #reference>
      <el-button size="small" icon="Operation" :disabled="permissionAccess.isButtonReadonly('columnSetting')">
        {{ permissionAccess.getButtonLabel('columnSetting') }}
      </el-button>
    </template>
    <div class="column-setting-panel">
      <div class="column-setting-panel__header">
        <span>列显示设置</span>
        <el-button link type="primary" :disabled="permissionAccess.isButtonReadonly('columnSetting')" @click="handleReset">重置</el-button>
      </div>
      <el-checkbox-group v-model="innerValue" class="column-setting-panel__group">
        <el-checkbox
          v-for="column in columns"
          :key="column.key"
          :label="column.key"
          :disabled="permissionAccess.isButtonReadonly('columnSetting')"
        >
          {{ column.label }}
        </el-checkbox>
      </el-checkbox-group>
    </div>
  </el-popover>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import {
  createBillPermissionAccessor,
  type BillPermissionSource
} from '@/components/Bill/billPermission';

interface ColumnSettingItem {
  key: string;
  label: string;
}

const props = defineProps<{
  modelValue: string[];
  columns: ColumnSettingItem[];
  defaultKeys?: string[];
  permissions?: BillPermissionSource;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>();

/**
 * 在列勾选变化时同步回传字符串数组。
 */
const innerValue = computed({
  get: () => {
    return props.modelValue;
  },
  set: (value: Array<string | number | boolean>) => {
    emit('update:modelValue', value.map((item) => {
      return String(item);
    }));
  }
});
const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  buttonLabels: {
    columnSetting: '列显示'
  }
});

/**
 * 将列显示项重置为默认可见项。
 */
const handleReset = () => {
  const nextKeys = props.defaultKeys?.length ? [...props.defaultKeys] : props.columns.map((item) => {
    return item.key;
  });
  emit('update:modelValue', nextKeys);
};
</script>

<style scoped lang="scss">
.column-setting-panel__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.column-setting-panel__group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>
