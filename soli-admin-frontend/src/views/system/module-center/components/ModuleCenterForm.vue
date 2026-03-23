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
          <el-option label="单据模块" value="BILL" />
        </el-select>
      </el-form-item>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="路由地址" prop="routePath">
            <el-input v-model="form.routePath" placeholder="如 /purchase/order" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="组件路径" prop="componentPath">
            <el-input v-model="form.componentPath" placeholder="如 purchase/order/index" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="图标" prop="icon">
            <el-input v-model="form.icon" placeholder="Element Plus 图标名" />
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
            <el-select v-model="form.navVisible" placeholder="请选择导航可见性" style="width: 100%">
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
import type { ModuleNode, ModuleType, YesNo } from '../moduleCenterMock';

interface ModuleCenterFormModel {
  id?: number;
  parentId: number;
  moduleCode: string;
  moduleName: string;
  moduleType: ModuleType;
  routePath: string;
  componentPath: string;
  icon: string;
  sort: number;
  navVisible: YesNo;
  statefulFlag: YesNo;
  stateFieldCode: string;
  status: YesNo;
  note: string;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<ModuleCenterFormModel>;
  treeData?: ModuleNode[];
  currentId?: number;
}

const props = defineProps<Props>();
const emit = defineEmits(['update:modelValue', 'submit', 'cancel']);

const visible = computed({
  get: () => props.modelValue,
  set: (val: boolean) => emit('update:modelValue', val)
});

const createDefaultForm = (): ModuleCenterFormModel => ({
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
});

const formRef = ref();
const form = reactive<ModuleCenterFormModel>(createDefaultForm());

const rules = {
  moduleCode: [{ message: '请输入模块编码', required: true, trigger: 'blur' }],
  moduleName: [{ message: '请输入模块名称', required: true, trigger: 'blur' }],
  moduleType: [{ message: '请选择模块类型', required: true, trigger: 'change' }],
  status: [{ message: '请选择模块状态', required: true, trigger: 'change' }]
} as const;

const treeSelectProps = {
  children: 'children',
  disabled: 'disabled',
  label: 'moduleName',
  value: 'id'
};

const collectDisabledIds = (nodes: ModuleNode[] | undefined, currentId?: number, bucket: Set<number> = new Set()): Set<number> => {
  if (!nodes) {
    return bucket;
  }
  for (const node of nodes) {
    if (node.id === currentId) {
      bucket.add(node.id);
      const collectChildren = (children: ModuleNode[] | undefined) => {
        if (!children) {
          return;
        }
        children.forEach((child) => {
          bucket.add(child.id);
          collectChildren(child.children);
        });
      };
      collectChildren(node.children);
      break;
    }
    collectDisabledIds(node.children, currentId, bucket);
  }
  return bucket;
};

const markDisabled = (nodes: ModuleNode[] | undefined, disabledIds: Set<number>): Array<ModuleNode & { disabled?: boolean }> => {
  if (!nodes) {
    return [];
  }
  return nodes.map((node) => ({
    ...node,
    children: markDisabled(node.children, disabledIds),
    disabled: disabledIds.has(node.id)
  }));
};

const treeOptions = computed(() => {
  const disabledIds = collectDisabledIds(props.treeData, props.currentId);
  return [
    {
      children: markDisabled(props.treeData, disabledIds),
      id: 0,
      moduleName: '顶级模块'
    }
  ];
});

watch(() => props.initialData, (val) => {
  Object.assign(form, createDefaultForm(), val || {});
}, { immediate: true });

watch(() => form.statefulFlag, (val) => {
  if (val !== '1') {
    form.stateFieldCode = '';
  }
});

const dialogTitle = computed(() => {
  return props.mode === 'create' ? '新增模块' : '编辑模块';
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
