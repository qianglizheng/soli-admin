<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item v-if="fieldConfigMap.username.visible" :label="fieldConfigMap.username.label" prop="username">
        <el-input
          v-model="form.username"
          :disabled="isEdit || !fieldConfigMap.username.editable"
          :placeholder="fieldConfigMap.username.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="!isEdit && fieldConfigMap.password.visible" :label="fieldConfigMap.password.label" prop="password">
        <el-input
          v-model="form.password"
          show-password
          type="password"
          :disabled="!fieldConfigMap.password.editable"
          :placeholder="fieldConfigMap.password.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.nickname.visible" :label="fieldConfigMap.nickname.label" prop="nickname">
        <el-input
          v-model="form.nickname"
          :disabled="!fieldConfigMap.nickname.editable"
          :placeholder="fieldConfigMap.nickname.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.email.visible" :label="fieldConfigMap.email.label" prop="email">
        <el-input
          v-model="form.email"
          :disabled="!fieldConfigMap.email.editable"
          :placeholder="fieldConfigMap.email.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.phone.visible" :label="fieldConfigMap.phone.label" prop="phone">
        <el-input
          v-model="form.phone"
          :disabled="!fieldConfigMap.phone.editable"
          :placeholder="fieldConfigMap.phone.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.sex.visible" :label="fieldConfigMap.sex.label" prop="sex">
        <el-radio-group v-model="form.sex" :disabled="!fieldConfigMap.sex.editable">
          <el-radio value="0">男</el-radio>
          <el-radio value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.type.visible" :label="fieldConfigMap.type.label" prop="type">
        <el-radio-group v-model="form.type" :disabled="!fieldConfigMap.type.editable">
          <el-radio value="0">超级管理员</el-radio>
          <el-radio value="1">普通用户</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.status.visible" :label="fieldConfigMap.status.label" prop="status">
        <el-radio-group v-model="form.status" :disabled="!fieldConfigMap.status.editable">
          <el-radio value="0">正常</el-radio>
          <el-radio value="1">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        v-if="actionButton.visible"
        type="primary"
        :disabled="actionButton.disabled"
        :loading="submitting"
        @click="handleSubmit"
      >
        {{ actionButton.label }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, nextTick, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import type {
  NormalDisableStatusCode,
  UserSexCode,
  UserTypeCode
} from '@/types/enums';
import { buildResolvedButtonConfigMap, buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import {
  USER_FORM_COMPONENT,
  userButtonFallbackMap,
  userFieldFallbackMap,
  type UserButtonCode,
  type UserFieldCode
} from '../moduleConfig';

export interface UserFormModel {
  id?: number;
  username: string;
  password: string;
  nickname: string;
  email: string;
  phone: string;
  sex: UserSexCode;
  type: UserTypeCode;
  status: NormalDisableStatusCode;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  context?: ModuleContext | null;
  initialData?: Partial<UserFormModel>;
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  context: null,
  initialData: () => ({}),
  submitting: false
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  submit: [value: UserFormModel];
  cancel: [];
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const isEdit = computed(() => props.mode === 'edit');
const dialogTitle = computed(() => (isEdit.value ? '修改用户' : '新增用户'));
const formRef = ref<FormInstance>();

const createDefaultForm = (): UserFormModel => ({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  sex: '0',
  type: '1',
  status: '0'
});

const form = reactive<UserFormModel>(createDefaultForm());

const fieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(props.context?.fieldConfigs || {}, USER_FORM_COMPONENT, userFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(props.context?.fieldConfigs || {}, userButtonFallbackMap);
});

const actionButton = computed(() => {
  const buttonCode: UserButtonCode = isEdit.value ? 'modify' : 'create';
  return buttonConfigMap.value[buttonCode];
});

const createRequiredRule = (fieldCode: UserFieldCode) => {
  return {
    trigger: ['blur', 'change'],
    validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
      const fieldConfig = fieldConfigMap.value[fieldCode];
      const shouldValidate = fieldConfig.visible
        && fieldConfig.required
        && fieldConfig.editable
        && (!isEdit.value || fieldCode !== 'password');
      if (!shouldValidate) {
        callback();
        return;
      }
      if (!String(value || '').trim()) {
        callback(new Error(fieldConfig.placeholder || `请输入${fieldConfig.label}`));
        return;
      }
      callback();
    }
  };
};

const rules = computed<FormRules<UserFormModel>>(() => ({
  password: [createRequiredRule('password')],
  username: [createRequiredRule('username')]
}));

const resetForm = () => {
  Object.assign(form, createDefaultForm());
  formRef.value?.clearValidate();
};

watch(
  () => [props.modelValue, props.initialData, props.mode] as const,
  ([open, initialData]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), initialData || {});
    if (isEdit.value) {
      form.password = '';
    }
    nextTick(() => {
      formRef.value?.clearValidate();
    });
  },
  { immediate: true }
);

const handleCancel = () => {
  emit('cancel');
  visible.value = false;
};

const handleClosed = () => {
  resetForm();
};

const handleSubmit = async () => {
  if (!formRef.value || !actionButton.value.visible || actionButton.value.disabled) {
    return;
  }
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }
  emit('submit', { ...form });
};
</script>