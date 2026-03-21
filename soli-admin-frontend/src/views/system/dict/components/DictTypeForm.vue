<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="560px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="字典名称" prop="name">
        <el-input v-model="form.name" placeholder="请输入字典名称" />
      </el-form-item>
      <el-form-item label="字典类型" prop="type">
        <el-input v-model="form.type" placeholder="请输入字典类型" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio value="0">正常</el-radio>
          <el-radio value="1">停用</el-radio>
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
import type { SysDictType } from '@/types/global';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<SysDictType>;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();

const createDefaultForm = (): Partial<SysDictType> => ({
  name: '',
  note: '',
  status: '0',
  type: ''
});

const form = reactive<Partial<SysDictType>>(createDefaultForm());

const rules: FormRules = {
  name: [{ message: '请输入字典名称', required: true, trigger: 'blur' }],
  status: [{ message: '请选择状态', required: true, trigger: 'change' }],
  type: [{ message: '请输入字典类型', required: true, trigger: 'blur' }]
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

const dialogTitle = computed(() => (props.mode === 'create' ? '新增字典' : '修改字典'));

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
