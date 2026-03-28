<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="560px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="按钮标题" prop="defaultTitle">
          <el-input v-model="form.defaultTitle" placeholder="请输入按钮标题" />
        </el-form-item>
        <el-form-item label="按钮编码" prop="buttonCode">
          <el-input v-model="form.buttonCode" placeholder="请输入按钮编码" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="说明" prop="note">
          <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入按钮说明" />
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { ModuleButtonDefinition } from '@/api/moduleCenter';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleButtonDefinition>;
  title?: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: Partial<ModuleButtonDefinition>): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function createDefaultForm(): Partial<ModuleButtonDefinition> {
  return {
    buttonCode: '',
    defaultTitle: '',
    note: '',
    sort: 1
  };
}

const formRef = ref<FormInstance>();
const form = reactive<Partial<ModuleButtonDefinition>>(createDefaultForm());

const rules: FormRules = {
  buttonCode: [{ message: '请输入按钮编码', required: true, trigger: 'blur' }],
  defaultTitle: [{ message: '请输入按钮标题', required: true, trigger: 'blur' }]
};

watch(
  () => props.initialData,
  (value) => {
    Object.assign(form, createDefaultForm(), value || {});
  },
  { immediate: true }
);

const dialogTitle = computed(() => props.title || (props.mode === 'create' ? '新增按钮' : '编辑按钮'));

function handleCancel() {
  emit('cancel');
  visible.value = false;
}

async function handleSubmit() {
  if (!formRef.value) {
    return;
  }
  await formRef.value.validate();
  emit('submit', { ...form });
  visible.value = false;
}
</script>

<style scoped>
.dialog-scroll-body {
  max-height: calc(100vh - 240px);
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
}
</style>
