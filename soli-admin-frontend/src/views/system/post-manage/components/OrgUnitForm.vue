<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item
          v-if="fieldConfigMap.orgParentNodeKey.visible"
          :label="fieldConfigMap.orgParentNodeKey.label"
          prop="parentNodeKey"
        >
          <el-input
            disabled
            :model-value="parentNodeLabel"
            :placeholder="fieldConfigMap.orgParentNodeKey.placeholder"
          />
        </el-form-item>

        <el-form-item v-if="fieldConfigMap.orgName.visible" :label="fieldConfigMap.orgName.label" prop="orgName">
          <el-input
            v-model="form.orgName"
            :disabled="!fieldConfigMap.orgName.editable"
            :placeholder="fieldConfigMap.orgName.placeholder"
          />
        </el-form-item>

        <el-form-item v-if="fieldConfigMap.orgCode.visible" :label="fieldConfigMap.orgCode.label" prop="orgCode">
          <el-input
            v-model="form.orgCode"
            :disabled="!fieldConfigMap.orgCode.editable"
            :placeholder="fieldConfigMap.orgCode.placeholder"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col v-if="fieldConfigMap.leaderUserId.visible" :span="12">
            <el-form-item :label="fieldConfigMap.leaderUserId.label" prop="leaderUserId">
              <el-select
                v-model="form.leaderUserId"
                clearable
                filterable
                remote
                reserve-keyword
                style="width: 100%"
                :disabled="!fieldConfigMap.leaderUserId.editable"
                :loading="userLoading"
                :placeholder="fieldConfigMap.leaderUserId.placeholder"
                :remote-method="handleLeaderSearch"
              >
                <el-option
                  v-for="item in userOptions"
                  :key="item.id"
                  :label="item.label"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col v-if="fieldConfigMap.orgSort.visible" :span="12">
            <el-form-item :label="fieldConfigMap.orgSort.label" prop="sort">
              <el-input-number
                v-model="form.sort"
                controls-position="right"
                style="width: 100%"
                :disabled="!fieldConfigMap.orgSort.editable"
                :min="1"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="fieldConfigMap.orgStatus.visible" :label="fieldConfigMap.orgStatus.label" prop="status">
          <el-select
            v-model="form.status"
            style="width: 100%"
            :disabled="!fieldConfigMap.orgStatus.editable"
            :placeholder="fieldConfigMap.orgStatus.placeholder"
          >
            <el-option label="启用" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="fieldConfigMap.orgNote.visible" :label="fieldConfigMap.orgNote.label" prop="note">
          <el-input
            v-model="form.note"
            type="textarea"
            :rows="3"
            :disabled="!fieldConfigMap.orgNote.editable"
            :placeholder="fieldConfigMap.orgNote.placeholder"
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <el-button @click="handleCancel">取消</el-button>
      <el-button
        v-if="actionButton.visible"
        type="primary"
        :disabled="actionButton.disabled"
        :loading="submitting"
        @click="handleSubmit"
      >
        {{ actionButton.label }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { ModuleContext } from '@/api/moduleCenter';
import type { OrgPostTreeNode, OrgUnitFormModel } from '@/api/orgPost';
import { useRemoteUserOptions } from '@/composables/useRemoteUserOptions';
import { buildResolvedButtonConfigMap, buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import {
  ORG_FORM_COMPONENT,
  orgFieldFallbackMap,
  orgPostButtonFallbackMap,
  type OrgFormFieldCode
} from '../moduleConfig';

interface TreeOption extends OrgPostTreeNode {
  disabled?: boolean;
  children?: TreeOption[];
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  context?: ModuleContext | null;
  initialData?: Partial<OrgUnitFormModel>;
  treeData?: OrgPostTreeNode[];
  submitting?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  context: null,
  initialData: () => ({}),
  submitting: false,
  treeData: () => []
});

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: OrgUnitFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const dialogTitle = computed(() => (props.mode === 'edit' ? '编辑分公司' : '新增分公司'));
const formRef = ref<FormInstance>();
const { ensureUserOption, loadUserOptions, loading: userLoading, userOptions } = useRemoteUserOptions();

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

const form = reactive<OrgUnitFormModel>(createDefaultForm());

const fieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(props.context?.fieldConfigs || {}, ORG_FORM_COMPONENT, orgFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(props.context?.fieldConfigs || {}, orgPostButtonFallbackMap);
});

const actionButton = computed(() => {
  return buttonConfigMap.value[props.mode === 'edit' ? 'modify' : 'create'];
});

const createRequiredRule = (fieldCode: OrgFormFieldCode) => {
  return {
    trigger: ['blur', 'change'],
    validator: (_rule: unknown, value: string | number | undefined, callback: (error?: Error) => void) => {
      const fieldConfig = fieldConfigMap.value[fieldCode];
      const shouldValidate = fieldConfig.visible && fieldConfig.required && fieldConfig.editable;
      if (!shouldValidate) {
        callback();
        return;
      }
      if (value === undefined || value === null || String(value).trim() === '') {
        callback(new Error(fieldConfig.placeholder || `请输入${fieldConfig.label}`));
        return;
      }
      callback();
    }
  };
};

const rules = computed<FormRules<OrgUnitFormModel>>(() => ({
  parentNodeKey: [createRequiredRule('orgParentNodeKey')],
  orgCode: [createRequiredRule('orgCode')],
  orgName: [createRequiredRule('orgName')],
  status: [createRequiredRule('orgStatus')]
}));

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
const parentNodeLabel = computed(() => resolveNodeName(treeOptions.value, form.parentNodeKey) || '未选择');

watch(
  () => [props.modelValue, props.initialData] as const,
  async ([open]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), props.initialData || {});
    formRef.value?.clearValidate();
    await loadUserOptions();
    await ensureUserOption(form.leaderUserId);
  },
  { deep: true, immediate: true }
);

function handleLeaderSearch(keyword: string) {
  void loadUserOptions(keyword);
}

function resolveNodeName(nodes: TreeOption[], nodeKey?: string): string {
  if (!nodeKey) {
    return '';
  }
  for (const node of nodes) {
    if (node.nodeKey === nodeKey) {
      return node.nodeName;
    }
    if (node.children?.length) {
      const matched = resolveNodeName(node.children, nodeKey);
      if (matched) {
        return matched;
      }
    }
  }
  return '';
}

function handleCancel() {
  emit('cancel');
  visible.value = false;
}

async function handleSubmit() {
  if (!formRef.value || !actionButton.value.visible || actionButton.value.disabled) {
    return;
  }
  const valid = await formRef.value.validate().catch(() => false);
  if (!valid) {
    return;
  }
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
