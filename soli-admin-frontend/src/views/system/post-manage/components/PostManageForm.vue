<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="上级节点" prop="parentId">
          <el-tree-select
            v-model="form.parentId"
            :data="treeOptions"
            :props="treeSelectProps"
            check-strictly
            filterable
            placeholder="请选择上级节点"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="岗位名称" prop="nodeName">
          <el-input v-model="form.nodeName" placeholder="请输入岗位名称" />
        </el-form-item>
        <el-form-item label="岗位编码" prop="nodeCode">
          <el-input v-model="form.nodeCode" placeholder="请输入岗位编码" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位类型" prop="postTypeLabel">
              <el-select v-model="form.postTypeLabel" placeholder="请选择岗位类型" style="width: 100%">
                <el-option v-for="item in postTypeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位负责人" prop="managerName">
              <el-input v-model="form.managerName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="sort">
              <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="岗位状态" prop="status">
              <el-select v-model="form.status" placeholder="请选择岗位状态" style="width: 100%">
                <el-option label="启用" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="岗位说明" prop="note">
          <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入岗位说明" />
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
import { postTypeOptions, type PostManageTreeNode, type YesNo } from '../postManageMock';

interface PostManageFormModel {
  id?: number;
  parentId: number;
  nodeCode: string;
  nodeName: string;
  postTypeLabel: string;
  managerName: string;
  sort: number;
  status: YesNo;
  note: string;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<PostManageFormModel>;
  treeData?: PostManageTreeNode[];
  currentId?: number;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: PostManageFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const createDefaultForm = (): PostManageFormModel => ({
  managerName: '',
  nodeCode: '',
  nodeName: '',
  note: '',
  parentId: 1,
  postTypeLabel: postTypeOptions[0],
  sort: 1,
  status: '0'
});

const formRef = ref<FormInstance>();
const form = reactive<PostManageFormModel>(createDefaultForm());

const rules: FormRules<PostManageFormModel> = {
  nodeCode: [{ message: '请输入岗位编码', required: true, trigger: 'blur' }],
  nodeName: [{ message: '请输入岗位名称', required: true, trigger: 'blur' }],
  parentId: [{ message: '请选择上级节点', required: true, trigger: 'change' }],
  postTypeLabel: [{ message: '请选择岗位类型', required: true, trigger: 'change' }],
  status: [{ message: '请选择岗位状态', required: true, trigger: 'change' }]
};

const treeSelectProps = {
  children: 'children',
  disabled: 'disabled',
  label: 'nodeName',
  value: 'id'
};

const collectDisabledIds = (
  nodes: PostManageTreeNode[] | undefined,
  currentId?: number,
  bucket: Set<number> = new Set()
): Set<number> => {
  if (!nodes) {
    return bucket;
  }
  for (const node of nodes) {
    if (node.id === currentId) {
      bucket.add(node.id);
      const collectChildren = (children: PostManageTreeNode[] | undefined) => {
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

const markDisabled = (
  nodes: PostManageTreeNode[] | undefined,
  disabledIds: Set<number>
): Array<PostManageTreeNode & { disabled?: boolean }> => {
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
  return markDisabled(props.treeData, disabledIds);
});

watch(
  () => [props.modelValue, props.initialData],
  ([open]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), props.initialData || {});
    formRef.value?.clearValidate();
  },
  { deep: true, immediate: true }
);

const dialogTitle = computed(() => (props.mode === 'create' ? '新增岗位' : '编辑岗位'));

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

<style scoped>
.dialog-scroll-body {
  max-height: calc(100vh - 240px);
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 6px;
}
</style>
