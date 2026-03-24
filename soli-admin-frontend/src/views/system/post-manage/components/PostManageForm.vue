<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="上级节点" prop="parentNodeKey">
          <el-tree-select
            v-model="form.parentNodeKey"
            :data="treeOptions"
            :props="treeSelectProps"
            check-strictly
            filterable
            placeholder="请选择上级节点"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="岗位名称" prop="postName">
          <el-input v-model="form.postName" placeholder="请输入岗位名称" />
        </el-form-item>

        <el-form-item label="岗位编码" prop="postCode">
          <el-input v-model="form.postCode" placeholder="请输入岗位编码" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="岗位类型" prop="postType">
              <el-select v-model="form.postType" placeholder="请选择岗位类型" style="width: 100%">
                <el-option v-for="item in POST_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="岗位负责人" prop="managerUserId">
              <el-select
                v-model="form.managerUserId"
                clearable
                filterable
                remote
                reserve-keyword
                :loading="managerLoading"
                :remote-method="handleManagerSearch"
                placeholder="请输入账号搜索负责人"
                style="width: 100%"
              >
                <el-option v-for="item in managerOptions" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
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
import { POST_TYPE_OPTIONS, type OrgPostFormModel, type OrgPostTreeNode, type YesNo } from '@/api/orgPost';
import { getUserDetail, getUserPage } from '@/api/user';

interface TreeOption extends OrgPostTreeNode {
  disabled?: boolean;
  children?: TreeOption[];
}

interface ManagerOption {
  id: number;
  label: string;
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  initialData?: Partial<OrgPostFormModel>;
  treeData?: OrgPostTreeNode[];
  currentNodeKey?: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: OrgPostFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function createDefaultForm(): OrgPostFormModel {
  return {
    note: '',
    parentNodeKey: '',
    postCode: '',
    postName: '',
    postType: POST_TYPE_OPTIONS[0]!.value,
    sort: 1,
    status: '0'
  };
}

const formRef = ref<FormInstance>();
const form = reactive<OrgPostFormModel>(createDefaultForm());
const managerOptions = ref<ManagerOption[]>([]);
const managerLoading = ref(false);

const rules: FormRules<OrgPostFormModel> = {
  parentNodeKey: [{ message: '请选择上级节点', required: true, trigger: 'change' }],
  postCode: [{ message: '请输入岗位编码', required: true, trigger: 'blur' }],
  postName: [{ message: '请输入岗位名称', required: true, trigger: 'blur' }],
  postType: [{ message: '请选择岗位类型', required: true, trigger: 'change' }],
  status: [{ message: '请选择岗位状态', required: true, trigger: 'change' }]
};

const treeSelectProps = {
  children: 'children',
  disabled: 'disabled',
  label: 'nodeName',
  value: 'nodeKey'
};

function collectChildNodeKeys(nodes: OrgPostTreeNode[] | undefined, bucket: Set<string>) {
  if (!nodes) {
    return;
  }
  nodes.forEach((node) => {
    bucket.add(node.nodeKey);
    collectChildNodeKeys(node.children, bucket);
  });
}

function collectDisabledNodeKeys(
  nodes: OrgPostTreeNode[] | undefined,
  currentNodeKey?: string,
  bucket: Set<string> = new Set()
): Set<string> {
  if (!nodes || !currentNodeKey) {
    return bucket;
  }
  for (const node of nodes) {
    if (node.nodeKey === currentNodeKey) {
      bucket.add(node.nodeKey);
      collectChildNodeKeys(node.children, bucket);
      break;
    }
    collectDisabledNodeKeys(node.children, currentNodeKey, bucket);
  }
  return bucket;
}

function markDisabled(nodes: OrgPostTreeNode[] | undefined, disabledKeys: Set<string>): TreeOption[] {
  if (!nodes) {
    return [];
  }
  return nodes.map((node) => ({
    ...node,
    children: markDisabled(node.children, disabledKeys),
    disabled: disabledKeys.has(node.nodeKey)
  }));
}

const treeOptions = computed(() => {
  const disabledKeys = collectDisabledNodeKeys(props.treeData, props.currentNodeKey);
  return markDisabled(props.treeData, disabledKeys);
});

watch(
  () => [props.modelValue, props.initialData],
  async ([open]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), props.initialData || {});
    formRef.value?.clearValidate();
    await loadManagerOptions();
    await ensureManagerOption(form.managerUserId);
  },
  { deep: true, immediate: true }
);

const dialogTitle = computed(() => (props.mode === 'create' ? '新增岗位' : '编辑岗位'));

async function loadManagerOptions(keyword = '') {
  managerLoading.value = true;
  try {
    const { data } = await getUserPage({
      pageNum: 1,
      pageSize: 20,
      username: keyword || undefined
    });
    managerOptions.value = data.list.map((item) => ({
      id: item.id,
      label: item.nickname ? `${item.nickname} / ${item.username}` : item.username
    }));
  } finally {
    managerLoading.value = false;
  }
}

async function ensureManagerOption(managerUserId?: number) {
  if (!managerUserId || managerOptions.value.some((item) => item.id === managerUserId)) {
    return;
  }
  const { data } = await getUserDetail(managerUserId);
  managerOptions.value.unshift({
    id: data.id,
    label: data.nickname ? `${data.nickname} / ${data.username}` : data.username
  });
}

function handleManagerSearch(keyword: string) {
  void loadManagerOptions(keyword);
}

function handleCancel() {
  emit('cancel');
  visible.value = false;
}

async function handleSubmit() {
  if (!formRef.value) {
    return;
  }
  await formRef.value.validate();
  emit('submit', { ...form, status: form.status as YesNo });
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
