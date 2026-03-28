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
          :disabled="!fieldConfigMap.password.editable"
          :placeholder="fieldConfigMap.password.placeholder"
          show-password
          type="password"
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
        :loading="submitting"
        :disabled="actionButton.disabled"
        type="primary"
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

type UserFieldCode = 'username' | 'password' | 'nickname' | 'email' | 'phone' | 'sex' | 'type' | 'status';
type UserButtonCode = 'create' | 'modify';
type ModuleFieldContext = ModuleContext['headerTabs'][number]['fields'][number];
type ModuleButtonContext = ModuleContext['buttons']['listToolbar'][string];

interface ResolvedFieldConfig {
  label: string;
  placeholder: string;
  visible: boolean;
  editable: boolean;
  required: boolean;
}

interface ResolvedButtonConfig {
  label: string;
  visible: boolean;
  disabled: boolean;
}

export interface UserFormModel {
  id?: number;
  username: string;
  password: string;
  nickname: string;
  email: string;
  phone: string;
  sex: string;
  type: string;
  status: string;
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

const userFieldFallbackMap: Record<UserFieldCode, ResolvedFieldConfig> = {
  username: { editable: true, label: '用户名称', placeholder: '请输入用户名称', required: true, visible: true },
  password: { editable: true, label: '用户密码', placeholder: '请输入用户密码', required: true, visible: true },
  nickname: { editable: true, label: '用户昵称', placeholder: '请输入用户昵称', required: false, visible: true },
  email: { editable: true, label: '用户邮箱', placeholder: '请输入用户邮箱', required: false, visible: true },
  phone: { editable: true, label: '手机号码', placeholder: '请输入手机号码', required: false, visible: true },
  sex: { editable: true, label: '性别', placeholder: '请选择性别', required: false, visible: true },
  type: { editable: true, label: '账号类型', placeholder: '请选择账号类型', required: false, visible: true },
  status: { editable: true, label: '状态', placeholder: '请选择用户状态', required: false, visible: true }
};

const userButtonFallbackMap: Record<UserButtonCode, ResolvedButtonConfig> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true }
};

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

const buildFieldContextMap = (context: ModuleContext | null) => {
  const result: Partial<Record<UserFieldCode, ModuleFieldContext>> = {};
  if (!context) {
    return result;
  }
  [...context.headerTabs, ...context.detailTabs].forEach((tab) => {
    tab.fields.forEach((field) => {
      if (field.fieldCode in userFieldFallbackMap) {
        result[field.fieldCode as UserFieldCode] = field;
      }
    });
  });
  return result;
};

const buildButtonContextMap = (context: ModuleContext | null) => {
  const result: Partial<Record<UserButtonCode, ModuleButtonContext>> = {};
  if (!context?.buttons) {
    return result;
  }
  const buttonGroups = [
    context.buttons.listToolbar,
    context.buttons.listRow,
    context.buttons.headerToolbar,
    context.buttons.detailRow
  ];
  buttonGroups.forEach((buttonGroup) => {
    Object.values(buttonGroup || {}).forEach((button) => {
      if (button.buttonCode in userButtonFallbackMap) {
        result[button.buttonCode as UserButtonCode] = button;
      }
    });
  });
  return result;
};

const fieldConfigMap = computed(() => {
  const fieldMap = buildFieldContextMap(props.context || null);
  return (Object.keys(userFieldFallbackMap) as UserFieldCode[]).reduce<Record<UserFieldCode, ResolvedFieldConfig>>((result, fieldCode) => {
    const fallback = userFieldFallbackMap[fieldCode];
    const field = fieldMap[fieldCode];
    result[fieldCode] = {
      editable: field?.editable ?? fallback.editable,
      label: field?.label || field?.displayTitle || field?.defaultTitle || fallback.label,
      placeholder: field?.placeholder || fallback.placeholder,
      required: field?.required ?? fallback.required,
      visible: field?.visible ?? fallback.visible
    };
    return result;
  }, {} as Record<UserFieldCode, ResolvedFieldConfig>);
});

const actionButton = computed(() => {
  const buttonMap = buildButtonContextMap(props.context || null);
  const buttonCode: UserButtonCode = isEdit.value ? 'modify' : 'create';
  const fallback = userButtonFallbackMap[buttonCode];
  const button = buttonMap[buttonCode];

  return {
    disabled: button?.disabled ?? fallback.disabled,
    label: button?.label || button?.defaultTitle || fallback.label,
    visible: button?.visible ?? fallback.visible
  };
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
    nextTick(() => formRef.value?.clearValidate());
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
