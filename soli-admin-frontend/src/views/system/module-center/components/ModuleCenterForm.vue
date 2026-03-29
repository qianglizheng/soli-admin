<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="680px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="上级模块" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeOptions"
            :props="treeSelectProps"
            check-strictly
            filterable
            placeholder="请选择上级模块"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="模块名称" prop="moduleName">
          <el-input v-model="form.moduleName" placeholder="请输入模块名称" />
        </el-form-item>

        <el-form-item label="模块编码" prop="moduleCode">
          <el-input v-model="form.moduleCode" placeholder="请输入模块编码" />
        </el-form-item>

        <el-form-item label="模块类型" prop="moduleType">
          <el-select v-model="form.moduleType" placeholder="请选择模块类型" style="width: 100%">
            <el-option label="目录" value="CATALOG" />
            <el-option label="页面" value="PAGE" />
          </el-select>
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="路由地址" prop="routePath">
              <el-input v-model="form.routePath" placeholder="如 /purchase 或 order" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="componentPath">
              <el-input v-model="form.componentPath" placeholder="如 purchase/order/PurchaseOrderIndex" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="form.icon" placeholder="Element Plus 图标名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="导航可见" prop="navVisible">
              <el-select v-model="form.navVisible" placeholder="请选择导航可见状态" style="width: 100%">
                <el-option label="显示" value="1" />
                <el-option label="隐藏" value="0" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模块状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择模块状态" style="width: 100%">
                <el-option label="启用" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="状态型模块" prop="statefulFlag">
              <el-select v-model="form.statefulFlag" placeholder="请选择" style="width: 100%">
                <el-option label="否" value="0" />
                <el-option label="是" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态字段" prop="stateFieldCode">
              <el-input v-model="form.stateFieldCode" :disabled="form.statefulFlag !== '1'" placeholder="如 status" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="模块说明" prop="note">
          <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入模块说明" />
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
import type { ModuleFormModel, ModuleTreeNode } from '@/api/moduleCenter';

interface TreeOption extends ModuleTreeNode {
  disabled?: boolean;
  children?: TreeOption[];
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleFormModel>;
  treeData?: ModuleTreeNode[];
  currentId?: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: ModuleFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function createDefaultForm(): ModuleFormModel {
  return {
    componentPath: '',
    icon: 'Document',
    moduleCode: '',
    moduleName: '',
    moduleType: 'PAGE',
    navVisible: '1',
    note: '',
    parentId: 0,
    routePath: '',
    sort: 1,
    stateFieldCode: '',
    statefulFlag: '0',
    status: '0'
  };
}

function normalizeModuleType(moduleType?: ModuleFormModel['moduleType']): ModuleFormModel['moduleType'] {
  return moduleType === 'BILL' ? 'PAGE' : (moduleType || 'PAGE');
}

const formRef = ref<FormInstance>();
const form = reactive<ModuleFormModel>(createDefaultForm());

const rules: FormRules<ModuleFormModel> = {
  moduleCode: [{ message: '请输入模块编码', required: true, trigger: 'blur' }],
  moduleName: [{ message: '请输入模块名称', required: true, trigger: 'blur' }],
  moduleType: [{ message: '请选择模块类型', required: true, trigger: 'change' }],
  status: [{ message: '请选择模块状态', required: true, trigger: 'change' }]
};

const treeSelectProps = {
  children: 'children',
  disabled: 'disabled',
  label: 'moduleName',
  value: 'id'
};

function collectDisabledIds(
  nodes: ModuleTreeNode[] | undefined,
  currentId?: number,
  bucket: Set<number> = new Set()
): Set<number> {
  if (!nodes || currentId === undefined) {
    return bucket;
  }
  for (const node of nodes) {
    if (node.id === currentId) {
      bucket.add(node.id);
      collectChildIds(node.children, bucket);
      break;
    }
    collectDisabledIds(node.children, currentId, bucket);
  }
  return bucket;
}

function collectChildIds(nodes: ModuleTreeNode[] | undefined, bucket: Set<number>) {
  if (!nodes) {
    return;
  }
  nodes.forEach((node) => {
    bucket.add(node.id);
    collectChildIds(node.children, bucket);
  });
}

function markDisabled(nodes: ModuleTreeNode[] | undefined, disabledIds: Set<number>): TreeOption[] {
  if (!nodes) {
    return [];
  }
  return nodes.map((node) => ({
    ...node,
    children: markDisabled(node.children, disabledIds),
    disabled: disabledIds.has(node.id) || node.moduleType !== 'CATALOG'
  }));
}

const treeOptions = computed(() => {
  const disabledIds = collectDisabledIds(props.treeData, props.currentId);
  return [
    {
      id: 0,
      moduleName: '顶级模块',
      parentId: -1,
      moduleCode: 'ROOT',
      moduleType: 'CATALOG',
      sort: 0,
      statefulFlag: '0',
      status: '0',
      children: markDisabled(props.treeData, disabledIds)
    }
  ] as TreeOption[];
});

watch(
  () => props.initialData,
  (value) => {
    Object.assign(form, createDefaultForm(), value ? {
      ...value,
      moduleType: normalizeModuleType(value.moduleType)
    } : {});
  },
  { immediate: true }
);

watch(
  () => form.statefulFlag,
  (value) => {
    if (value !== '1') {
      form.stateFieldCode = '';
    }
  }
);

const dialogTitle = computed(() => (props.mode === 'create' ? '新增模块' : '编辑模块'));

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
