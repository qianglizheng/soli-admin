<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" destroy-on-close @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item v-if="fieldConfigMap.label.visible" :label="fieldConfigMap.label.label" prop="label">
        <el-input
          v-model="form.label"
          :disabled="!fieldConfigMap.label.editable"
          :placeholder="fieldConfigMap.label.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.value.visible" :label="fieldConfigMap.value.label" prop="value">
        <el-input
          v-model="form.value"
          :disabled="!fieldConfigMap.value.editable"
          :placeholder="fieldConfigMap.value.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.sort.visible" :label="fieldConfigMap.sort.label" prop="sort">
        <el-input
          v-model="form.sort"
          :disabled="!fieldConfigMap.sort.editable"
          :placeholder="fieldConfigMap.sort.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.listClass.visible" :label="fieldConfigMap.listClass.label" prop="listClass">
        <el-input
          v-model="form.listClass"
          :disabled="!fieldConfigMap.listClass.editable"
          :placeholder="fieldConfigMap.listClass.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.cssClass.visible" :label="fieldConfigMap.cssClass.label" prop="cssClass">
        <el-input
          v-model="form.cssClass"
          :disabled="!fieldConfigMap.cssClass.editable"
          :placeholder="fieldConfigMap.cssClass.placeholder"
        />
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.defaultFlag.visible" :label="fieldConfigMap.defaultFlag.label" prop="defaultFlag">
        <el-radio-group v-model="form.defaultFlag" :disabled="!fieldConfigMap.defaultFlag.editable">
          <el-radio value="Y">是</el-radio>
          <el-radio value="N">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="fieldConfigMap.status.visible" :label="fieldConfigMap.status.label" prop="status">
        <el-radio-group v-model="form.status" :disabled="!fieldConfigMap.status.editable">
          <el-radio value="0">正常</el-radio>
          <el-radio value="1">停用</el-radio>
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
import type { NormalDisableStatusEnumCode, YesNoEnumCode } from '@/types/enums';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  type ModuleButtonFallback,
  type ModuleFieldFallback
} from '@/utils/moduleContext';

type DictDataFieldCode = 'label' | 'value' | 'sort' | 'listClass' | 'cssClass' | 'defaultFlag' | 'status' | 'note';
type DictDataButtonCode = 'create' | 'modify';
const DICT_DATA_FORM_COMPONENT = 'dict_data_form';

export interface DictDataFormModel {
  id?: number;
  dictTypeId?: number;
  label: string;
  value: string;
  sort: string;
  cssClass: string;
  listClass: string;
  defaultFlag: YesNoEnumCode;
  status: NormalDisableStatusEnumCode;
  note: string;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  context?: ModuleContext | null;
  initialData?: Partial<DictDataFormModel>;
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  context: null,
  initialData: () => ({}),
  submitting: false
});

const emit = defineEmits<{
  'update:modelValue': [value: boolean];
  submit: [value: DictDataFormModel];
  cancel: [];
}>();

const dictDataFieldFallbackMap: Record<DictDataFieldCode, ModuleFieldFallback> = {
  cssClass: { label: '样式属性', placeholder: '请输入样式属性', required: false, visible: true },
  defaultFlag: { label: '是否默认', placeholder: '请选择是否默认', required: true, visible: true },
  label: { label: '字典标签', placeholder: '请输入字典标签', required: true, visible: true },
  listClass: { label: '回显样式', placeholder: '请输入回显样式', required: false, visible: true },
  note: { label: '备注', placeholder: '请输入备注', required: false, visible: true },
  sort: { label: '显示排序', placeholder: '请输入显示排序', required: false, visible: true },
  status: { label: '状态', placeholder: '请选择状态', required: true, visible: true },
  value: { label: '字典键值', placeholder: '请输入字典键值', required: true, visible: true }
};

const dictDataButtonFallbackMap: Record<DictDataButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true }
};

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();

const createDefaultForm = (): DictDataFormModel => ({
  cssClass: '',
  defaultFlag: 'N',
  label: '',
  listClass: '',
  note: '',
  sort: '0',
  status: '0',
  value: ''
});

const form = reactive<DictDataFormModel>(createDefaultForm());

const fieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(props.context?.fieldConfigs || {}, DICT_DATA_FORM_COMPONENT, dictDataFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(props.context?.fieldConfigs || {}, dictDataButtonFallbackMap);
});

const actionButton = computed(() => {
  const buttonCode: DictDataButtonCode = props.mode === 'create' ? 'create' : 'modify';
  return buttonConfigMap.value[buttonCode];
});

const createRequiredRule = (fieldCode: DictDataFieldCode) => {
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

const rules = computed<FormRules<DictDataFormModel>>(() => ({
  defaultFlag: [createRequiredRule('defaultFlag')],
  label: [createRequiredRule('label')],
  status: [createRequiredRule('status')],
  value: [createRequiredRule('value')]
}));

const dialogTitle = computed(() => (props.mode === 'create' ? '新增字典数据' : '修改字典数据'));

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