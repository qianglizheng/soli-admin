<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="600px" destroy-on-close @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item v-if="fieldConfigMap.configName.visible" :label="fieldConfigMap.configName.label" prop="configName">
        <el-input
          v-model="form.configName"
          :disabled="!fieldConfigMap.configName.editable"
          :placeholder="fieldConfigMap.configName.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.configKey.visible" :label="fieldConfigMap.configKey.label" prop="configKey">
        <el-input
          v-model="form.configKey"
          :disabled="!fieldConfigMap.configKey.editable"
          :placeholder="fieldConfigMap.configKey.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.configValue.visible" :label="fieldConfigMap.configValue.label" prop="configValue">
        <el-input
          v-model="form.configValue"
          :disabled="!fieldConfigMap.configValue.editable"
          :placeholder="fieldConfigMap.configValue.placeholder"
          type="textarea"
          :rows="3"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.configType.visible" :label="fieldConfigMap.configType.label" prop="configType">
        <el-radio-group v-model="form.configType" :disabled="!fieldConfigMap.configType.editable">
          <el-radio value="Y">是</el-radio>
          <el-radio value="N">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.note.visible" :label="fieldConfigMap.note.label" prop="note">
        <el-input
          v-model="form.note"
          :disabled="!fieldConfigMap.note.editable"
          :placeholder="fieldConfigMap.note.placeholder"
          type="textarea"
          :rows="3"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        v-if="actionButton.visible"
        type="primary"
        :loading="submitting"
        :disabled="actionButton.disabled"
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
import type { SysConfig } from '@/types/global';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  type ModuleButtonFallback,
  type ModuleFieldFallback
} from '@/utils/moduleContext';

type ConfigFieldCode = 'configName' | 'configKey' | 'configValue' | 'configType' | 'note';
type ConfigButtonCode = 'create' | 'modify';
const CONFIG_FORM_COMPONENT = 'config_form';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  context?: ModuleContext | null;
  initialData?: Partial<SysConfig>;
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  context: null,
  initialData: () => ({}),
  submitting: false
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  submit: [value: Partial<SysConfig>];
  cancel: [];
}>();

const configFieldFallbackMap: Record<ConfigFieldCode, ModuleFieldFallback> = {
  configKey: { label: '参数键名', placeholder: '请输入参数键名', required: true, visible: true },
  configName: { label: '参数名称', placeholder: '请输入参数名称', required: true, visible: true },
  configType: { label: '系统内置', placeholder: '请选择系统内置', required: true, visible: true },
  configValue: { label: '参数键值', placeholder: '请输入参数键值', required: false, visible: true },
  note: { label: '备注', placeholder: '请输入备注', required: false, visible: true }
};

const configButtonFallbackMap: Record<ConfigButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true }
};

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();

const createDefaultForm = (): Partial<SysConfig> => ({
  configKey: '',
  configName: '',
  configType: 'N',
  configValue: '',
  note: ''
});

const form = reactive<Partial<SysConfig>>(createDefaultForm());

const fieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(props.context?.fieldConfigs || {}, CONFIG_FORM_COMPONENT, configFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(props.context?.fieldConfigs || {}, configButtonFallbackMap);
});

const actionButton = computed(() => {
  const buttonCode: ConfigButtonCode = props.mode === 'create' ? 'create' : 'modify';
  return buttonConfigMap.value[buttonCode];
});

const createRequiredRule = (fieldCode: ConfigFieldCode) => {
  return {
    trigger: ['blur', 'change'],
    validator: (_rule: unknown, value: string, callback: (error?: Error) => void) => {
      const fieldConfig = fieldConfigMap.value[fieldCode];
      if (!fieldConfig.visible || !fieldConfig.required || !fieldConfig.editable) {
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

const rules = computed<FormRules<Partial<SysConfig>>>(() => ({
  configKey: [createRequiredRule('configKey')],
  configName: [createRequiredRule('configName')],
  configType: [createRequiredRule('configType')]
}));

const dialogTitle = computed(() => (props.mode === 'create' ? '新增参数' : '修改参数'));

const resetForm = () => {
  Object.assign(form, createDefaultForm());
  formRef.value?.clearValidate();
};

watch(
  () => [props.modelValue, props.initialData] as const,
  ([open, initialData]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), initialData || {});
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