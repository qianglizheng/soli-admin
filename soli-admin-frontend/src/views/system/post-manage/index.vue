<template>
  <div class="app-container post-manage-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="8" class="tree-col">
        <el-card v-loading="treeLoading" shadow="never" class="tree-card">
          <template #header>
            <div class="card-header">
              <span>岗位树</span>
              <el-tag type="info" effect="plain">{{ postCount }} 个岗位</el-tag>
            </div>
          </template>

          <el-input
            v-model="treeKeyword"
            clearable
            placeholder="请输入岗位名称或编码"
            prefix-icon="Search"
            class="tree-search"
          />

          <div class="tree-toolbar">
            <el-button type="primary" plain icon="Plus" @click="handleCreateRoot">新增岗位</el-button>
            <el-button plain icon="OfficeBuilding" @click="handleCreateOrgUnit">新增分公司</el-button>
            <el-button plain icon="DocumentAdd" :disabled="!selectedNode" @click="handleCreateChild">新增下级</el-button>
            <el-button plain icon="Edit" :disabled="!selectedPostNode" @click="handleEditPost">编辑岗位</el-button>
          </div>

          <el-scrollbar class="tree-scrollbar">
            <el-tree
              ref="treeRef"
              :data="postTree"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :highlight-current="true"
              node-key="nodeKey"
              @node-click="handleNodeClick"
            >
              <template #default="{ data }">
                <div class="tree-node">
                  <div class="tree-node__head">
                    <span class="tree-node__label">{{ data.nodeName }}</span>
                    <el-tag size="small" :type="getNodeTagType(data.nodeType)" effect="plain">
                      {{ getOrgNodeTypeLabel(data.nodeType) }}
                    </el-tag>
                  </div>
                  <div class="tree-node__code">{{ data.nodeCode }}</div>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>

      <el-col :span="16" class="workspace-col">
        <el-card v-loading="workspaceLoading" shadow="never" class="workspace-card">
          <template v-if="selectedPostNode">
            <div class="workspace-overview">
              <div class="overview-header">
                <div>
                  <div class="overview-title">
                    <span>{{ selectedPostDetail?.postName || selectedPostNode.nodeName }}</span>
                    <el-tag size="small" type="primary" effect="plain">{{ currentPostTypeLabel }}</el-tag>
                    <el-tag size="small" type="info" effect="plain">{{ currentOrgTypeLabel }}</el-tag>
                    <el-tag size="small" :type="currentStatus === '0' ? 'success' : 'danger'" effect="plain">
                      {{ currentStatus === '0' ? '启用' : '停用' }}
                    </el-tag>
                  </div>
                  <div class="overview-subtitle">{{ selectedPostDetail?.note || '当前岗位暂未填写说明。' }}</div>
                </div>

                <div class="overview-actions">
                  <el-button type="primary" icon="UserFilled" @click="openAssignEmployeeDialog">添加员工</el-button>
                  <el-button plain icon="View" @click="openViewEmployeeDialog">查看员工</el-button>
                </div>
              </div>

              <el-row :gutter="12" class="metric-row">
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">岗位员工</div>
                    <div class="metric-card__value">{{ selectedPostDetail?.employeeCount || 0 }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">下级岗位</div>
                    <div class="metric-card__value">{{ selectedPostDetail?.childPostCount || 0 }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">显示顺序</div>
                    <div class="metric-card__value">{{ selectedPostDetail?.sort || 0 }}</div>
                  </div>
                </el-col>
              </el-row>

              <el-descriptions :column="2" border class="post-descriptions">
                <el-descriptions-item label="岗位编码">{{ selectedPostDetail?.postCode || '-' }}</el-descriptions-item>
                <el-descriptions-item label="所属组织">{{ selectedPostDetail?.orgName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="上级节点">{{ selectedPostDetail?.parentNodeName || '-' }}</el-descriptions-item>
                <el-descriptions-item label="岗位负责人">{{ selectedPostDetail?.managerName || '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="employee-panel">
              <div class="panel-header">
                <span>岗位员工预览</span>
                <el-tag size="small" effect="plain">{{ employeePreviewResult.total }} 人</el-tag>
              </div>

              <el-empty v-if="!employeePreviewResult.list.length" description="当前岗位暂无员工" />
              <el-table v-else :data="employeePreviewResult.list" border height="100%">
                <el-table-column prop="username" label="账号" min-width="120" />
                <el-table-column prop="nickname" label="姓名" min-width="110" />
                <el-table-column prop="phone" label="手机号" min-width="140" />
                <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
                <el-table-column label="状态" width="90" align="center">
                  <template #default="scope">
                    <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                      {{ scope.row.status === '0' ? '启用' : '停用' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>

              <div v-if="employeePreviewResult.total > employeePreviewResult.list.length" class="employee-hint">
                当前仅预览前 {{ employeePreviewResult.list.length }} 人，可点击“查看员工”查看完整分页列表。
              </div>
            </div>
          </template>

          <div v-else class="workspace-empty">
            <el-empty description="请选择左侧岗位节点后查看岗位员工" />
            <el-alert
              v-if="selectedNode"
              title="当前选中的是组织节点，可在左侧新增岗位；如需维护员工，请选择具体岗位。"
              type="info"
              :closable="false"
              show-icon
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <post-manage-form
      v-model="formVisible"
      :mode="formMode"
      :initial-data="formInitial"
      :tree-data="postTree"
      :current-node-key="formMode === 'edit' ? selectedPostNode?.nodeKey : undefined"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />

    <org-unit-form
      v-model="orgFormVisible"
      :initial-data="orgFormInitial"
      :tree-data="postTree"
      @submit="handleOrgFormSubmit"
      @cancel="handleOrgFormCancel"
    />

    <post-employee-dialog
      v-model="assignDialogVisible"
      mode="assign"
      :post-id="selectedPostDetail?.id"
      :post-name="selectedPostDetail?.postName"
      @submit="handleEmployeeChanged"
      @cancel="assignDialogVisible = false"
    />

    <post-employee-dialog
      v-model="viewDialogVisible"
      mode="view"
      :post-id="selectedPostDetail?.id"
      :post-name="selectedPostDetail?.postName"
      @submit="handleEmployeeChanged"
      @cancel="viewDialogVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import OrgUnitForm from './components/OrgUnitForm.vue';
import PostEmployeeDialog from './components/PostEmployeeDialog.vue';
import PostManageForm from './components/PostManageForm.vue';
import {
  createOrgUnit,
  createOrgPost,
  type CreateOrgUnitPayload,
  getOrgNodeTypeLabel,
  getOrgPostDetail,
  getOrgPostTree,
  getOrgPostUserPage,
  getPostTypeLabel,
  POST_TYPE_OPTIONS,
  updateOrgPost,
  type CreateOrgPostPayload,
  type OrgPostDetail,
  type OrgPostFormModel,
  type OrgPostTreeNode,
  type OrgPostUser,
  type OrgUnitFormModel
} from '@/api/orgPost';
import type { PageResult } from '@/types/global';

defineOptions({
  name: 'SystemPostManage'
});

const treeRef = ref();
const treeKeyword = ref('');
const treeLoading = ref(false);
const workspaceLoading = ref(false);
const postTree = ref<OrgPostTreeNode[]>([]);
const selectedNodeKey = ref<string>();
const selectedPostDetail = ref<OrgPostDetail>();
const employeePreviewResult = ref<PageResult<OrgPostUser>>({
  list: [],
  pageNum: 1,
  pageSize: 6,
  total: 0
});
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<OrgPostFormModel>>({});
const orgFormVisible = ref(false);
const orgFormInitial = ref<Partial<OrgUnitFormModel>>({});
const assignDialogVisible = ref(false);
const viewDialogVisible = ref(false);

const selectedNode = computed(() => findTreeNode(postTree.value, selectedNodeKey.value));
const selectedPostNode = computed(() => (selectedNode.value?.nodeType === 'POST' ? selectedNode.value : undefined));
const postCount = computed(() => countPostNodes(postTree.value));
const currentPostTypeLabel = computed(() => getPostTypeLabel(selectedPostDetail.value?.postType));
const currentOrgTypeLabel = computed(() => getOrgNodeTypeLabel(selectedPostDetail.value?.orgType || selectedNode.value?.nodeType));
const currentStatus = computed(() => selectedPostDetail.value?.status || selectedPostNode.value?.status || '0');

const filterNode = (value: string, data: OrgPostTreeNode) => {
  if (!value) {
    return true;
  }
  return data.nodeName.includes(value) || data.nodeCode.includes(value);
};

const findTreeNode = (nodes: OrgPostTreeNode[], nodeKey?: string): OrgPostTreeNode | undefined => {
  if (!nodeKey) {
    return undefined;
  }
  for (const node of nodes) {
    if (node.nodeKey === nodeKey) {
      return node;
    }
    if (node.children?.length) {
      const matched = findTreeNode(node.children, nodeKey);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

const findFirstPostNode = (nodes: OrgPostTreeNode[]): OrgPostTreeNode | undefined => {
  for (const node of nodes) {
    if (node.nodeType === 'POST') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstPostNode(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

const countPostNodes = (nodes: OrgPostTreeNode[]) => {
  return nodes.reduce((count, node) => {
    const current = node.nodeType === 'POST' ? 1 : 0;
    return count + current + (node.children?.length ? countPostNodes(node.children) : 0);
  }, 0);
};

const getNodeTagType = (nodeType: string) => {
  if (nodeType === 'GROUP') {
    return 'info';
  }
  if (nodeType === 'POST') {
    return 'warning';
  }
  return 'success';
};

const resetWorkspace = () => {
  selectedPostDetail.value = undefined;
  employeePreviewResult.value = {
    list: [],
    pageNum: 1,
    pageSize: 6,
    total: 0
  };
};

const loadPostWorkspace = async (postId: number) => {
  workspaceLoading.value = true;
  try {
    const [detailResponse, previewResponse] = await Promise.all([
      getOrgPostDetail(postId),
      getOrgPostUserPage({
        orgPostId: postId,
        pageNum: 1,
        pageSize: 6
      })
    ]);
    if (selectedPostNode.value?.id !== postId) {
      return;
    }
    selectedPostDetail.value = detailResponse.data;
    employeePreviewResult.value = previewResponse.data;
  } finally {
    workspaceLoading.value = false;
  }
};

const handleNodeSelection = async (nodeKey?: string) => {
  selectedNodeKey.value = nodeKey;
  if (!nodeKey) {
    resetWorkspace();
    return;
  }
  await nextTick();
  treeRef.value?.setCurrentKey(nodeKey);
  const node = findTreeNode(postTree.value, nodeKey);
  if (!node || node.nodeType !== 'POST') {
    resetWorkspace();
    return;
  }
  resetWorkspace();
  await loadPostWorkspace(node.id);
};

const loadTree = async (preferNodeKey?: string) => {
  treeLoading.value = true;
  try {
    const { data } = await getOrgPostTree();
    postTree.value = data;
    const nextNodeKey = findTreeNode(data, preferNodeKey)
      ? preferNodeKey
      : findTreeNode(data, selectedNodeKey.value)
        ? selectedNodeKey.value
        : findFirstPostNode(data)?.nodeKey || data[0]?.nodeKey;
    await handleNodeSelection(nextNodeKey);
  } finally {
    treeLoading.value = false;
  }
};

const handleNodeClick = (node: OrgPostTreeNode) => {
  void handleNodeSelection(node.nodeKey);
};

const resolveNextSort = (parentNodeKey: string) => {
  const parentNode = findTreeNode(postTree.value, parentNodeKey);
  const children = parentNode?.children || [];
  return children.length ? Math.max(...children.map((item) => item.sort || 0)) + 1 : 1;
};

const resolveNextOrgSort = (parentNodeKey: string) => {
  const parentNode = findTreeNode(postTree.value, parentNodeKey);
  const orgChildren = (parentNode?.children || []).filter((item) => item.nodeType !== 'POST');
  return orgChildren.length ? Math.max(...orgChildren.map((item) => item.sort || 0)) + 1 : 1;
};

const getCreateParentNodeKey = () => {
  if (!selectedNode.value) {
    return postTree.value[0]?.nodeKey || '';
  }
  if (selectedNode.value.nodeType === 'POST') {
    return selectedNode.value.parentNodeKey || '';
  }
  return selectedNode.value.nodeKey;
};

const getCreateOrgParentNodeKey = () => {
  if (!selectedNode.value) {
    return postTree.value[0]?.nodeKey || '';
  }
  if (selectedNode.value.nodeType === 'POST') {
    return `ORG_${selectedNode.value.orgUnitId}`;
  }
  return selectedNode.value.nodeKey;
};

const buildFormPayload = (formData: OrgPostFormModel): CreateOrgPostPayload => {
  const parentNode = findTreeNode(postTree.value, formData.parentNodeKey);
  if (!parentNode) {
    throw new Error('上级节点不存在');
  }
  return {
    managerUserId: formData.managerUserId,
    note: formData.note,
    orgUnitId: parentNode.nodeType === 'POST' ? parentNode.orgUnitId : parentNode.id,
    parentPostId: parentNode.nodeType === 'POST' ? parentNode.id : 0,
    postCode: formData.postCode,
    postName: formData.postName,
    postType: formData.postType,
    sort: formData.sort,
    status: formData.status
  };
};

const buildOrgFormPayload = (formData: OrgUnitFormModel): CreateOrgUnitPayload => {
  const parentNode = findTreeNode(postTree.value, formData.parentNodeKey);
  if (!parentNode || parentNode.nodeType === 'POST') {
    throw new Error('上级组织不存在');
  }
  return {
    leaderUserId: formData.leaderUserId,
    note: formData.note,
    orgCode: formData.orgCode,
    orgName: formData.orgName,
    parentId: parentNode.id,
    sort: formData.sort,
    status: formData.status
  };
};

const handleCreateRoot = () => {
  const parentNodeKey = getCreateParentNodeKey();
  if (!parentNodeKey) {
    ElMessage.warning('请先初始化组织节点后再新增岗位');
    return;
  }
  formMode.value = 'create';
  formInitial.value = {
    note: '',
    parentNodeKey,
    postCode: '',
    postName: '',
    postType: POST_TYPE_OPTIONS[0]!.value,
    sort: resolveNextSort(parentNodeKey),
    status: '0'
  };
  formVisible.value = true;
};

const handleCreateOrgUnit = () => {
  const parentNodeKey = getCreateOrgParentNodeKey();
  if (!parentNodeKey) {
    ElMessage.warning('请先初始化组织节点后再新增分公司');
    return;
  }
  orgFormInitial.value = {
    note: '',
    orgCode: '',
    orgName: '',
    parentNodeKey,
    sort: resolveNextOrgSort(parentNodeKey),
    status: '0'
  };
  orgFormVisible.value = true;
};

const handleCreateChild = () => {
  if (!selectedNode.value) {
    return;
  }
  formMode.value = 'create';
  formInitial.value = {
    note: '',
    parentNodeKey: selectedNode.value.nodeKey,
    postCode: '',
    postName: '',
    postType: POST_TYPE_OPTIONS[0]!.value,
    sort: resolveNextSort(selectedNode.value.nodeKey),
    status: '0'
  };
  formVisible.value = true;
};

const handleEditPost = () => {
  if (!selectedPostNode.value || !selectedPostDetail.value) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = {
    id: selectedPostDetail.value.id,
    managerUserId: selectedPostDetail.value.managerUserId,
    note: selectedPostDetail.value.note || '',
    parentNodeKey: selectedPostNode.value.parentNodeKey || '',
    postCode: selectedPostDetail.value.postCode,
    postName: selectedPostDetail.value.postName,
    postType: selectedPostDetail.value.postType || POST_TYPE_OPTIONS[0]!.value,
    sort: selectedPostDetail.value.sort,
    status: (selectedPostDetail.value.status || '0') as '0' | '1'
  };
  formVisible.value = true;
};

const handleFormSubmit = async (formData: OrgPostFormModel) => {
  try {
    const payload = buildFormPayload(formData);
    if (formMode.value === 'create') {
      const { data } = await createOrgPost(payload);
      ElMessage.success('岗位新增成功');
      formVisible.value = false;
      await loadTree(data ? `POST_${data}` : undefined);
      return;
    }
    await updateOrgPost({
      ...payload,
      id: formData.id!
    });
    ElMessage.success('岗位信息已更新');
    formVisible.value = false;
    await loadTree(`POST_${formData.id}`);
  } catch (error) {
    if (error instanceof Error && error.message === '上级节点不存在') {
      ElMessage.error(error.message);
    }
  }
};

const handleFormCancel = () => {
  formVisible.value = false;
};

const handleOrgFormSubmit = async (formData: OrgUnitFormModel) => {
  try {
    const payload = buildOrgFormPayload(formData);
    const { data } = await createOrgUnit(payload);
    ElMessage.success('分公司新增成功');
    orgFormVisible.value = false;
    await loadTree(data ? `ORG_${data}` : undefined);
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.error(error.message);
    }
  }
};

const handleOrgFormCancel = () => {
  orgFormVisible.value = false;
};

const openAssignEmployeeDialog = () => {
  if (!selectedPostDetail.value?.id) {
    return;
  }
  assignDialogVisible.value = true;
};

const openViewEmployeeDialog = () => {
  if (!selectedPostDetail.value?.id) {
    return;
  }
  viewDialogVisible.value = true;
};

const handleEmployeeChanged = async () => {
  if (!selectedPostDetail.value?.id) {
    return;
  }
  await loadPostWorkspace(selectedPostDetail.value.id);
};

watch(treeKeyword, (value) => {
  treeRef.value?.filter(value);
});

onMounted(() => {
  void loadTree();
});
</script>

<style scoped>
.post-manage-page {
  box-sizing: border-box;
  height: 100%;
  overflow: hidden;
}

:deep(.post-manage-page > .el-row) {
  height: 100%;
}

.workspace-col,
.tree-col {
  display: flex;
  height: 100%;
  min-height: 0;
  flex-direction: column;
}

.workspace-card,
.tree-card {
  display: flex;
  height: 100%;
  min-height: 0;
  flex: 1;
  border-radius: 12px;
  flex-direction: column;
}

:deep(.workspace-card .el-card__body),
:deep(.tree-card .el-card__body) {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

.workspace-overview,
.workspace-empty {
  flex-shrink: 0;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.workspace-empty {
  display: flex;
  flex-direction: column;
  gap: 16px;
  justify-content: center;
}

.overview-header,
.card-header,
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.overview-title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 600;
}

.overview-subtitle {
  color: var(--el-text-color-secondary);
  line-height: 1.7;
}

.overview-actions,
.tree-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.metric-row {
  margin: 18px 0 8px;
}

.metric-card {
  height: 100%;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: linear-gradient(180deg, #fcfdff 0%, #f8fafc 100%);
  padding: 16px 18px;
}

.metric-card__label {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.metric-card__value {
  margin-top: 10px;
  color: var(--el-text-color-primary);
  font-size: 28px;
  font-weight: 600;
}

.post-descriptions {
  margin-top: 16px;
}

.employee-panel {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  padding-top: 16px;
  flex-direction: column;
}

.employee-hint {
  margin-top: 12px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.6;
}

.tree-search {
  margin-bottom: 12px;
}

.tree-scrollbar {
  min-height: 0;
  flex: 1;
}

.tree-node {
  display: block;
  width: 100%;
  min-width: 0;
  padding-right: 8px;
}

.tree-node__head {
  display: flex;
  width: 100%;
  min-width: 0;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.tree-node__label {
  min-width: 0;
  flex: 1;
  color: var(--el-text-color-primary);
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tree-node__code {
  width: 100%;
  margin-top: 4px;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

:deep(.tree-card .el-tree-node__content) {
  height: auto;
  align-items: flex-start;
  padding: 8px 0 8px 2px;
}

:deep(.tree-card .el-tree-node__expand-icon) {
  margin-top: 10px;
}

:deep(.tree-card .el-tree-node) {
  min-width: 0;
}

@media (max-width: 1280px) {
  .overview-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .overview-actions {
    width: 100%;
  }

  .post-manage-page {
    height: auto;
    min-height: calc(100vh - 84px);
  }

  :deep(.post-manage-page > .el-row) {
    height: auto;
  }

  .workspace-col,
  .tree-col,
  .workspace-card,
  .tree-card {
    min-height: auto;
  }
}
</style>
