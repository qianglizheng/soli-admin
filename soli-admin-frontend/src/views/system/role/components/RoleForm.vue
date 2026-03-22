<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input v-model="form.roleKey" placeholder="请输入权限字符" />
      </el-form-item>
      <el-form-item label="显示顺序" prop="roleSort">
        <el-input v-model="form.roleSort" placeholder="请输入显示顺序" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio value="0">正常</el-radio>
          <el-radio value="1">停用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button :loading="submitting" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

interface RoleFormData {
  roleId?: number;
  roleName: string;
  roleKey: string;
  roleSort: string;
  dataScope: string;
  status: string;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<RoleFormData>;
  submitting?: boolean;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: RoleFormData): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();

const createDefaultForm = (): RoleFormData => ({
  roleName: '',
  roleKey: '',
  roleSort: '1',
  dataScope: '1',
  status: '0'
});

const form = reactive<RoleFormData>(createDefaultForm());

const rules: FormRules<RoleFormData> = {
  roleName: [{ message: '请输入角色名称', required: true, trigger: 'blur' }],
  roleKey: [{ message: '请输入权限字符', required: true, trigger: 'blur' }]
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

const dialogTitle = computed(() => (props.mode === 'create' ? '新增角色' : '修改角色'));

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
};
</script>
