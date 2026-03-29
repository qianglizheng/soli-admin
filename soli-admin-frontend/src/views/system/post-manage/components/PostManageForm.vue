<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="620px" top="6vh" destroy-on-close>
    <div class="dialog-scroll-body">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-form-item
          v-if="fieldConfigMap.postParentNodeKey.visible"
          :label="fieldConfigMap.postParentNodeKey.label"
          prop="parentNodeKey"
        >
          <el-tree-select
            v-model="form.parentNodeKey"
            check-strictly
            filterable
            style="width: 100%"
            :data="treeOptions"
            :disabled="!fieldConfigMap.postParentNodeKey.editable"
            :placeholder="fieldConfigMap.postParentNodeKey.placeholder"
            :props="treeSelectProps"
          />
        </el-form-item>

        <el-form-item v-if="fieldConfigMap.postName.visible" :label="fieldConfigMap.postName.label" prop="postName">
          <el-input
            v-model="form.postName"
            :disabled="!fieldConfigMap.postName.editable"
            :placeholder="fieldConfigMap.postName.placeholder"
          />
        </el-form-item>

        <el-form-item v-if="fieldConfigMap.postCode.visible" :label="fieldConfigMap.postCode.label" prop="postCode">
          <el-input
            v-model="form.postCode"
            :disabled="!fieldConfigMap.postCode.editable"
            :placeholder="fieldConfigMap.postCode.placeholder"
          />
        </el-form-item>

        <el-row :gutter="16">
          <el-col v-if="fieldConfigMap.postType.visible" :span="12">
            <el-form-item :label="fieldConfigMap.postType.label" prop="postType">
              <el-select
                v-model="form.postType"
                style="width: 100%"
                :disabled="!fieldConfigMap.postType.editable"
                :placeholder="fieldConfigMap.postType.placeholder"
              >
                <el-option
                  v-for="item in POST_TYPE_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col v-if="fieldConfigMap.managerUserId.visible" :span="12">
            <el-form-item :label="fieldConfigMap.managerUserId.label" prop="managerUserId">
              <el-select
                v-model="form.managerUserId"
                clearable
                filterable
                remote
                reserve-keyword
                style="width: 100%"
                :disabled="!fieldConfigMap.managerUserId.editable"
                :loading="userLoading"
                :placeholder="fieldConfigMap.managerUserId.placeholder"
                :remote-method="handleManagerSearch"
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
        </el-row>

        <el-row :gutter="16">
          <el-col v-if="fieldConfigMap.postSort.visible" :span="12">
            <el-form-item :label="fieldConfigMap.postSort.label" prop="sort">
              <el-input-number
                v-model="form.sort"
                controls-position="right"
                style="width: 100%"
                :disabled="!fieldConfigMap.postSort.editable"
                :min="1"
              />
            </el-form-item>
          </el-col>

          <el-col v-if="fieldConfigMap.postStatus.visible" :span="12">
            <el-form-item :label="fieldConfigMap.postStatus.label" prop="status">
              <el-select
                v-model="form.status"
                style="width: 100%"
                :disabled="!fieldConfigMap.postStatus.editable"
                :placeholder="fieldConfigMap.postStatus.placeholder"
              >
                <el-option label="启用" value="0" />
                <el-option label="停用" value="1" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item v-if="fieldConfigMap.postNote.visible" :label="fieldConfigMap.postNote.label" prop="note">
          <el-input
            v-model="form.note"
            type="textarea"
            :rows="3"
            :disabled="!fieldConfigMap.postNote.editable"
            :placeholder="fieldConfigMap.postNote.placeholder"
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
import { POST_TYPE_OPTIONS, type OrgPostFormModel, type OrgPostTreeNode, type YesNo } from '@/api/orgPost';
import { useRemoteUserOptions } from '@/composables/useRemoteUserOptions';
import { buildResolvedButtonConfigMap, buildResolvedFieldConfigMap } from '@/utils/moduleContext';
import {
  POST_FORM_COMPONENT,
  orgPostButtonFallbackMap,
  postFieldFallbackMap,
  type PostFormFieldCode
} from '../moduleConfig';

interface TreeOption extends OrgPostTreeNode {
  disabled?: boolean;
  children?: TreeOption[];
}

interface Props {
  modelValue: boolean;
  mode: 'create' | 'edit';
  context?: ModuleContext | null;
  initialData?: Partial<OrgPostFormModel>;
  treeData?: OrgPostTreeNode[];
  currentNodeKey?: string;
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
  (e: 'submit', value: OrgPostFormModel): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const formRef = ref<FormInstance>();
const { ensureUserOption, loadUserOptions, loading: userLoading, userOptions } = useRemoteUserOptions();

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

const form = reactive<OrgPostFormModel>(createDefaultForm());

const fieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(props.context?.fieldConfigs || {}, POST_FORM_COMPONENT, postFieldFallbackMap);
});

const buttonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(props.context?.fieldConfigs || {}, orgPostButtonFallbackMap);
});

const actionButton = computed(() => {
  return buttonConfigMap.value[props.mode === 'edit' ? 'modify' : 'create'];
});

const dialogTitle = computed(() => (props.mode === 'create' ? '新增岗位' : '编辑岗位'));

const createRequiredRule = (fieldCode: PostFormFieldCode) => {
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

const rules = computed<FormRules<OrgPostFormModel>>(() => ({
  parentNodeKey: [createRequiredRule('postParentNodeKey')],
  postCode: [createRequiredRule('postCode')],
  postName: [createRequiredRule('postName')],
  postType: [createRequiredRule('postType')],
  status: [createRequiredRule('postStatus')]
}));

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
  () => [props.modelValue, props.initialData] as const,
  async ([open]) => {
    if (!open) {
      return;
    }
    Object.assign(form, createDefaultForm(), props.initialData || {});
    formRef.value?.clearValidate();
    await loadUserOptions();
    await ensureUserOption(form.managerUserId);
  },
  { deep: true, immediate: true }
);

function handleManagerSearch(keyword: string) {
  void loadUserOptions(keyword);
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
