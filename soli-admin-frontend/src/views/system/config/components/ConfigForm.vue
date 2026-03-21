<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="600px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="form.configName" placeholder="请输入参数名称" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="form.configKey" placeholder="请输入参数键名" />
      </el-form-item>
      <el-form-item label="参数键值" prop="configValue">
        <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="请输入参数键值" />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-radio-group v-model="form.configType">
          <el-radio value="Y">是</el-radio>
          <el-radio value="N">否</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="note">
        <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { SysConfig } from '@/types/global';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<SysConfig>;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

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

const rules: FormRules = {
  configKey: [{ message: '请输入参数键名', required: true, trigger: 'blur' }],
  configName: [{ message: '请输入参数名称', required: true, trigger: 'blur' }],
  configType: [{ message: '请选择系统内置', required: true, trigger: 'change' }]
};

const syncForm = () => {
  Object.assign(form, createDefaultForm(), props.initialData || {});
};

watch(
  () => [props.modelValue, props.initialData],
  ([open]) => {
    if (open) {
      syncForm();
      formRef.value?.clearValidate();
    }
  },
  { deep: true, immediate: true }
);

const dialogTitle = computed(() => (props.mode === 'create' ? '新增参数' : '修改参数'));

const handleCancel = () => {
  emit('cancel');
  visible.value = false;
};

const handleSubmit = async () => {
  if (!formRef.value) {
    return;
  }
  await formRef.value.validate();
  emit('submit', { ...form });
  visible.value = false;
};
</script>
