<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="组件编码" prop="componentCode">
        <el-input v-model="form.componentCode" placeholder="请输入组件编码" />
      </el-form-item>
      <el-form-item label="组件名称" prop="componentName">
        <el-input v-model="form.componentName" placeholder="请输入组件名称" />
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
      </el-form-item>
      <el-form-item label="说明" prop="note">
        <el-input v-model="form.note" placeholder="请输入组件说明" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { ModuleComponentInfo } from '@/api/moduleCenter'

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleComponentInfo>;
  title?: string;
}

const props = defineProps<Props>()
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: Partial<ModuleComponentInfo>): void;
  (e: 'cancel'): void;
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
})

function createDefaultForm(): Partial<ModuleComponentInfo> {
  return {
    componentCode: '',
    componentName: '',
    sort: 1
  }
}

const formRef = ref<FormInstance>()
const form = reactive<Partial<ModuleComponentInfo>>(createDefaultForm())

const rules: FormRules = {
  componentCode: [{ message: '请输入组件编码', required: true, trigger: 'blur' }],
  componentName: [{ message: '请输入组件名称', required: true, trigger: 'blur' }]
}

watch(
  () => props.initialData,
  (value) => {
    Object.assign(form, createDefaultForm(), value || {})
  },
  { immediate: true }
)

const dialogTitle = computed(() => props.title || (props.mode === 'create' ? '新增组件' : '编辑组件'))

function handleCancel() {
  emit('cancel')
  visible.value = false
}

async function handleSubmit() {
  if (!formRef.value) {
    return
  }
  await formRef.value.validate()
  emit('submit', { ...form })
  visible.value = false
}
</script>
