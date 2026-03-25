<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close @closed="handleClosed">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="用户名称" prop="username">
        <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名称" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="用户密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入用户密码" show-password type="password" />
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="请输入用户昵称" />
      </el-form-item>
      <el-form-item label="用户邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入用户邮箱" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item label="性别" prop="sex">
        <el-radio-group v-model="form.sex">
          <el-radio value="0">男</el-radio>
          <el-radio value="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-radio-group v-model="form.type">
          <el-radio value="0">超级管理员</el-radio>
          <el-radio value="1">普通用户</el-radio>
        </el-radio-group>
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
import { computed, nextTick, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

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
  initialData?: Partial<UserFormModel>;
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
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

const rules: FormRules<UserFormModel> = {
  username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  password: [{
    validator: (_rule, value, callback) => {
      if (!isEdit.value && !value) {
        callback(new Error('请输入用户密码'));
        return;
      }
      callback();
    },
    trigger: 'blur'
  }]
};

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
  if (!formRef.value) {
    return;
  }
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }
  emit('submit', { ...form });
};
</script>
