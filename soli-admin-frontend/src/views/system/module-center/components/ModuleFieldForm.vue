<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="640px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="字段标题" prop="defaultTitle">
              <el-input v-model="form.defaultTitle" placeholder="请输入字段标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字段编码" prop="fieldCode">
              <el-input v-model="form.fieldCode" placeholder="请输入字段编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="组件类型" prop="componentType">
              <el-select v-model="form.componentType" placeholder="请选择组件类型" style="width: 100%">
                <el-option label="文本" value="text" />
                <el-option label="输入框" value="input" />
                <el-option label="多行文本" value="textarea" />
                <el-option label="下拉选择" value="select" />
                <el-option label="搜索选择" value="search-select" />
                <el-option label="单选框" value="radio" />
                <el-option label="开关" value="switch" />
                <el-option label="数字" value="number" />
                <el-option label="金额" value="amount" />
                <el-option label="日期" value="date" />
                <el-option label="日期时间" value="datetime" />
                <el-option label="标签" value="tag" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="值类型" prop="valueType">
              <el-select v-model="form.valueType" placeholder="请选择值类型" style="width: 100%">
                <el-option label="字符串" value="string" />
                <el-option label="长整数" value="long" />
                <el-option label="整数" value="int" />
                <el-option label="小数" value="decimal" />
                <el-option label="布尔" value="boolean" />
                <el-option label="日期" value="date" />
                <el-option label="日期时间" value="datetime" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="16">
            <el-form-item label="数据路径" prop="dataPath">
              <el-input v-model="form.dataPath" placeholder="如 header.customerName / detail.items.qty" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="是否必填" prop="requiredFlag">
              <el-select v-model="form.requiredFlag" placeholder="请选择" style="width: 100%">
                <el-option label="否" value="0" />
                <el-option label="是" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item label="说明" prop="note">
              <el-input v-model="form.note" placeholder="请输入字段说明" />
            </el-form-item>
          </el-col>
        </el-row>
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
import type {
  ModuleComponentTypeCode,
  ModuleFieldDefinition,
  ModuleValueTypeCode,
  YesNoCode
} from '@/api/moduleCenter';
import { getEnumCode } from '@/utils/enum';

export interface ModuleFieldFormModel {
  id?: number;
  componentType: ModuleComponentTypeCode;
  dataPath: string;
  defaultTitle: string;
  fieldCode: string;
  note: string;
  requiredFlag: YesNoCode;
  sort: number;
  valueType: ModuleValueTypeCode;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleFieldDefinition> | Partial<ModuleFieldFormModel>;
  title?: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: Partial<ModuleFieldFormModel>): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function createDefaultForm(): ModuleFieldFormModel {
  return {
    componentType: 'input',
    dataPath: '',
    defaultTitle: '',
    fieldCode: '',
    note: '',
    requiredFlag: '0',
    sort: 1,
    valueType: 'string'
  };
}

const formRef = ref<FormInstance>();
const form = reactive<ModuleFieldFormModel>(createDefaultForm());

const rules: FormRules<ModuleFieldFormModel> = {
  componentType: [{ message: '请选择组件类型', required: true, trigger: 'change' }],
  dataPath: [{ message: '请输入数据路径', required: true, trigger: 'blur' }],
  defaultTitle: [{ message: '请输入字段标题', required: true, trigger: 'blur' }],
  fieldCode: [{ message: '请输入字段编码', required: true, trigger: 'blur' }],
  valueType: [{ message: '请选择值类型', required: true, trigger: 'change' }]
};

watch(
  () => props.initialData,
  (value) => {
    Object.assign(form, createDefaultForm(), value ? {
      ...value,
      componentType: getEnumCode(value.componentType) || 'input',
      requiredFlag: getEnumCode(value.requiredFlag) || '0',
      valueType: getEnumCode(value.valueType) || 'string'
    } : {});
  },
  { immediate: true }
);

const dialogTitle = computed(() => props.title || (props.mode === 'create' ? '新增字段' : '编辑字段'));

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
