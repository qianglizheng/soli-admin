<template>
  <el-dialog v-model="visible" title="新增分公司" width="620px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="上级组织" prop="parentNodeKey">
          <el-tree-select
            v-model="form.parentNodeKey"
            :data="treeOptions"
            :props="treeSelectProps"
            check-strictly
            filterable
            placeholder="请选择上级组织"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="分公司名称" prop="orgName">
          <el-input v-model="form.orgName" placeholder="请输入分公司名称" />
        </el-form-item>

        <el-form-item label="分公司编码" prop="orgCode">
          <el-input v-model="form.orgCode" placeholder="请输入分公司编码" />
        </el-form-item>

        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leaderUserId">
              <el-select
                v-model="form.leaderUserId"
                clearable
                filterable
                remote
                reserve-keyword
                :loading="leaderLoading"
                :remote-method="handleLeaderSearch"
                placeholder="请输入账号搜索负责人"
                style="width: 100%"
              >
                <el-option v-for="item in leaderOptions" :key="item.id" :label="item.label" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="显示顺序" prop="sort">
              <el-input-number v-model="form.sort" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>

        <el-form-item label="说明" prop="note">
          <el-input v-model="form.note" type="textarea" :rows="3" placeholder="请输入分公司说明" />
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
import type { OrgPostTreeNode, OrgUnitFormModel } from '@/api/orgPost';
import { getUserDetail, getUserPage } from '@/api/user';

interface TreeOption extends OrgPostTreeNode {
  disabled?: boolean;
  children?: TreeOption[];
}

interface LeaderOption {
  id: number;
  label: string;
}

interface Props {
  modelValue: boolean;
  initialData?: Partial<OrgUnitFormModel>;
  treeData?: OrgPostTreeNode[];
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: OrgUnitFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

function createDefaultForm(): OrgUnitFormModel {
  return {
    note: '',
    orgCode: '',
    orgName: '',
    parentNodeKey: '',
    sort: 1,
    status: '0'
  };
}

const formRef = ref<FormInstance>();
const form = reactive<OrgUnitFormModel>(createDefaultForm());
const leaderOptions = ref<LeaderOption[]>([]);
const leaderLoading = ref(false);

const rules: FormRules<OrgUnitFormModel> = {
  parentNodeKey: [{ message: '请选择上级组织', required: true, trigger: 'change' }],
  orgCode: [{ message: '请输入分公司编码', required: true, trigger: 'blur' }],
  orgName: [{ message: '请输入分公司名称', required: true, trigger: 'blur' }],
  status: [{ message: '请选择状态', required: true, trigger: 'change' }]
};

const treeSelectProps = {
  children: 'children',
  disabled: 'disabled',
  label: 'nodeName',
  value: 'nodeKey'
};

function mapOrgTreeOptions(nodes: OrgPostTreeNode[] | undefined): TreeOption[] {
  if (!nodes) {
    return [];
  }
  return nodes
    .filter((node) => node.nodeType !== 'POST')
    .map((node) => ({
      ...node,
      children: mapOrgTreeOptions(node.children)
    }));
}

const treeOptions = computed(() => mapOrgTreeOptions(props.treeData));

watch(
  () => [props.modelValue, props.initialData],
  async ([open]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), props.initialData || {});
    formRef.value?.clearValidate();
    await loadLeaderOptions();
    await ensureLeaderOption(form.leaderUserId);
  },
  { deep: true, immediate: true }
);

async function loadLeaderOptions(keyword = '') {
  leaderLoading.value = true;
  try {
    const { data } = await getUserPage({
      pageNum: 1,
      pageSize: 20,
      username: keyword || undefined
    });
    leaderOptions.value = data.list.map((item) => ({
      id: item.id,
      label: item.nickname ? `${item.nickname} / ${item.username}` : item.username
    }));
  } finally {
    leaderLoading.value = false;
  }
}

async function ensureLeaderOption(leaderUserId?: number) {
  if (!leaderUserId || leaderOptions.value.some((item) => item.id === leaderUserId)) {
    return;
  }
  const { data } = await getUserDetail(leaderUserId);
  leaderOptions.value.unshift({
    id: data.id,
    label: data.nickname ? `${data.nickname} / ${data.username}` : data.username
  });
}

function handleLeaderSearch(keyword: string) {
  void loadLeaderOptions(keyword);
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
  emit('submit', { ...form });
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
