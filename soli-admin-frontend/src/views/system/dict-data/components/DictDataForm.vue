<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="字典标签" prop="label">
        <el-input v-model="form.label" placeholder="请输入字典标签" />
      </el-form-item>
      <el-form-item label="字典键值" prop="value">
        <el-input v-model="form.value" placeholder="请输入字典键值" />
      </el-form-item>
      <el-form-item label="显示排序" prop="sort">
        <el-input v-model="form.sort" placeholder="请输入显示排序" />
      </el-form-item>
      <el-form-item label="回显样式" prop="listClass">
        <el-input v-model="form.listClass" placeholder="请输入表格回显样式" />
      </el-form-item>
      <el-form-item label="样式属性" prop="cssClass">
        <el-input v-model="form.cssClass" placeholder="请输入样式属性" />
      </el-form-item>
      <el-form-item label="是否默认" prop="defaultFlag">
        <el-radio-group v-model="form.defaultFlag">
          <el-radio value="Y">是</el-radio>
          <el-radio value="N">否</el-radio>
        </el-radio-group>
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
import type { SysDictData } from '@/types/global';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<SysDictData>;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();

const createDefaultForm = (): Partial<SysDictData> => ({
  cssClass: '',
  defaultFlag: 'N',
  label: '',
  listClass: '',
  note: '',
  sort: '0',
  status: '0',
  value: ''
});

const form = reactive<Partial<SysDictData>>(createDefaultForm());

const rules: FormRules = {
  defaultFlag: [{ message: '请选择是否默认', required: true, trigger: 'change' }],
  label: [{ message: '请输入字典标签', required: true, trigger: 'blur' }],
  status: [{ message: '请选择状态', required: true, trigger: 'change' }],
  value: [{ message: '请输入字典键值', required: true, trigger: 'blur' }]
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

const dialogTitle = computed(() => (props.mode === 'create' ? '新增字典数据' : '修改字典数据'));

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
