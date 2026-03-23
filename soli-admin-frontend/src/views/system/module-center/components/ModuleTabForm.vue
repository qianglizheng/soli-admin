<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="Tab 名称" prop="tabName">
        <el-input v-model="form.tabName" placeholder="请输入 Tab 名称" />
      </el-form-item>
      <el-form-item label="Tab 编码" prop="tabCode">
        <el-input v-model="form.tabCode" placeholder="请输入 Tab 编码" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
      </el-form-item>
      <el-form-item label="说明" prop="note">
        <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入 Tab 说明" />
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
import type { ModuleTabDefinition } from '../moduleCenterMock';

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleTabDefinition>;
  title?: string;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

const visible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val)
});

const createDefaultForm = (): Partial<ModuleTabDefinition> => ({
  note: '',
  sort: 1,
  tabCode: '',
  tabName: ''
});

const formRef = ref();
const form = reactive<Partial<ModuleTabDefinition>>(createDefaultForm());

const rules = {
  tabCode: [{ message: '请输入 Tab 编码', required: true, trigger: 'blur' }],
  tabName: [{ message: '请输入 Tab 名称', required: true, trigger: 'blur' }]
} as const;

watch(() => props.initialData, (val) => {
  Object.assign(form, createDefaultForm(), val || {});
}, { immediate: true });

const dialogTitle = computed(() => {
  return props.title || (props.mode === 'create' ? '新增 Tab' : '编辑 Tab');
});

const handleCancel = () => {
  emit('cancel');
  visible.value = false;
};

const handleSubmit = async () => {
  await formRef.value.validate();
  emit('submit', { ...form });
  visible.value = false;
};
</script>

<style scoped>
.dialog-scroll-body {
  max-height: calc(100vh - 240px);
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
}
</style>
