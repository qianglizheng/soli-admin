<template>
  <div class="app-container post-manage-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="8" class="tree-col">
        <el-card shadow="never" class="tree-card">
          <template #header>
            <div class="card-header">
              <span>岗位树</span>
              <el-tag type="info" effect="plain">{{ postCount }} 个岗位</el-tag>
            </div>
          </template>

          <el-alert
            title="当前页面为前端 mock 原型，左侧维护岗位树，右侧维护岗位详情与员工。"
            type="info"
            :closable="false"
            show-icon
            class="page-alert"
          />

          <el-input
            v-model="treeKeyword"
            clearable
            placeholder="请输入岗位名称或编码"
            prefix-icon="Search"
            class="tree-search"
          />

          <div class="tree-toolbar">
            <el-button type="primary" plain icon="Plus" @click="handleCreateRoot">新增岗位</el-button>
            <el-button plain icon="DocumentAdd" :disabled="!selectedNode" @click="handleCreateChild">新增下级</el-button>
            <el-button plain icon="Edit" :disabled="!selectedPost" @click="handleEditPost">编辑岗位</el-button>
          </div>

          <el-scrollbar class="tree-scrollbar">
            <el-tree
              ref="treeRef"
              :data="postTree"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :highlight-current="true"
              node-key="id"
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
        <el-card shadow="never" class="workspace-card">
          <template v-if="selectedPost">
            <div class="workspace-overview">
              <div class="overview-header">
                <div>
                  <div class="overview-title">
                    <span>{{ selectedPost.nodeName }}</span>
                    <el-tag size="small" type="primary" effect="plain">{{ selectedPost.postTypeLabel || '岗位' }}</el-tag>
                    <el-tag size="small" type="info" effect="plain">{{ selectedPost.orgTypeLabel }}</el-tag>
                    <el-tag size="small" :type="selectedPost.status === '0' ? 'success' : 'danger'" effect="plain">
                      {{ selectedPost.status === '0' ? '启用' : '停用' }}
                    </el-tag>
                  </div>
                  <div class="overview-subtitle">{{ selectedPost.note || '当前岗位暂未填写说明。' }}</div>
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
                    <div class="metric-card__value">{{ assignedEmployees.length }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">下级岗位</div>
                    <div class="metric-card__value">{{ childPostCount }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">显示顺序</div>
                    <div class="metric-card__value">{{ selectedPost.sort }}</div>
                  </div>
                </el-col>
              </el-row>

              <el-descriptions :column="2" border class="post-descriptions">
                <el-descriptions-item label="岗位编码">{{ selectedPost.nodeCode }}</el-descriptions-item>
                <el-descriptions-item label="所属组织">{{ selectedPost.orgName }}</el-descriptions-item>
                <el-descriptions-item label="上级节点">{{ parentNodeName }}</el-descriptions-item>
                <el-descriptions-item label="岗位负责人">{{ selectedPost.managerName || '-' }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="employee-panel">
              <div class="panel-header">
                <span>岗位员工预览</span>
                <el-tag size="small" effect="plain">{{ assignedEmployees.length }} 人</el-tag>
              </div>

              <el-empty v-if="!assignedEmployees.length" description="当前岗位暂无员工" />
              <el-table v-else :data="employeePreviewList" border height="100%">
                <el-table-column prop="employeeNo" label="工号" min-width="110" />
                <el-table-column prop="employeeName" label="姓名" min-width="100" />
                <el-table-column prop="deptName" label="部门" min-width="120" />
                <el-table-column prop="mobile" label="手机号" min-width="130" />
                <el-table-column label="状态" width="90" align="center">
                  <template #default="scope">
                    <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                      {{ scope.row.status === '0' ? '在岗' : '停用' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>

              <div v-if="assignedEmployees.length > employeePreviewList.length" class="employee-hint">
                当前仅预览前 {{ employeePreviewList.length }} 人，可点击“查看员工”查看完整列表。
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
      :current-id="formMode === 'edit' ? formInitial.id : undefined"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />

    <post-employee-dialog
      v-model="assignDialogVisible"
      mode="assign"
      :post-name="dialogTargetPost?.nodeName"
      :employees="employeeList"
      :selected-ids="dialogTargetPost?.employeeIds || []"
      @submit="handleAssignEmployeeSubmit"
      @cancel="assignDialogVisible = false"
    />

    <post-employee-dialog
      v-model="viewDialogVisible"
      mode="view"
      :post-name="dialogTargetPost?.nodeName"
      :employees="dialogTargetEmployees"
      @cancel="viewDialogVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import PostEmployeeDialog from './components/PostEmployeeDialog.vue';
import PostManageForm from './components/PostManageForm.vue';
import {
  clonePostManageEmployees,
  clonePostManageTree,
  countPostNodes,
  findFirstPostNode,
  findPostManageNode,
  getOrgNodeTypeLabel,
  postTypeOptions,
  type OrgNodeType,
  type PostManageTreeNode
} from './postManageMock';

defineOptions({
  name: 'SystemPostManage'
});

const treeRef = ref();
const treeKeyword = ref('');
const postTree = ref(clonePostManageTree());
const employeeList = ref(clonePostManageEmployees());
const selectedNodeId = ref<number>();
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<PostManageTreeNode>>({});
const assignDialogVisible = ref(false);
const viewDialogVisible = ref(false);
const dialogTargetPostId = ref<number>();

const sortBySort = <T extends { sort: number }>(left: T, right: T) => left.sort - right.sort;

const selectedNode = computed(() => {
  return findPostManageNode(postTree.value, selectedNodeId.value);
});

const selectedPost = computed(() => {
  return selectedNode.value?.nodeType === 'POST' ? selectedNode.value : undefined;
});

const dialogTargetPost = computed(() => {
  const node = findPostManageNode(postTree.value, dialogTargetPostId.value);
  return node?.nodeType === 'POST' ? node : undefined;
});

const dialogTargetEmployees = computed(() => {
  const employeeIds = new Set(dialogTargetPost.value?.employeeIds || []);
  return employeeList.value.filter((item) => employeeIds.has(item.id));
});

const assignedEmployees = computed(() => {
  const employeeIds = new Set(selectedPost.value?.employeeIds || []);
  return employeeList.value.filter((item) => employeeIds.has(item.id));
});

const employeePreviewList = computed(() => {
  return assignedEmployees.value.slice(0, 6);
});

const postCount = computed(() => countPostNodes(postTree.value));

const childPostCount = computed(() => {
  return selectedPost.value?.children?.filter((item) => item.nodeType === 'POST').length || 0;
});

const parentNodeName = computed(() => {
  if (!selectedPost.value) {
    return '-';
  }
  return findPostManageNode(postTree.value, selectedPost.value.parentId)?.nodeName || '-';
});

const getNodeTagType = (nodeType: OrgNodeType) => {
  if (nodeType === 'GROUP') {
    return 'info';
  }
  if (nodeType === 'POST') {
    return 'warning';
  }
  return 'success';
};

const filterNode = (value: string, data: PostManageTreeNode) => {
  if (!value) {
    return true;
  }
  return data.nodeName.includes(value) || data.nodeCode.includes(value);
};

const selectNode = (id?: number) => {
  selectedNodeId.value = id;
  if (id !== undefined) {
    treeRef.value?.setCurrentKey(id);
  }
};

const handleNodeClick = (node: PostManageTreeNode) => {
  selectNode(node.id);
};

const findMaxId = (nodes: PostManageTreeNode[]): number => {
  return nodes.reduce((max, node) => {
    const childMax = node.children?.length ? findMaxId(node.children) : node.id;
    return Math.max(max, node.id, childMax);
  }, 0);
};

const appendChildNode = (nodes: PostManageTreeNode[], parentId: number, child: PostManageTreeNode): boolean => {
  for (const node of nodes) {
    if (node.id === parentId) {
      node.children = node.children || [];
      node.children.push(child);
      node.children.sort(sortBySort);
      return true;
    }
    if (node.children?.length && appendChildNode(node.children, parentId, child)) {
      return true;
    }
  }
  return false;
};

const removeNode = (nodes: PostManageTreeNode[], id: number): PostManageTreeNode | undefined => {
  for (let index = 0; index < nodes.length; index += 1) {
    const node = nodes[index]!;
    if (node.id === id) {
      return nodes.splice(index, 1)[0];
    }
    if (node.children?.length) {
      const removed = removeNode(node.children, id);
      if (removed) {
        return removed;
      }
    }
  }
  return undefined;
};

const updateNode = (nodes: PostManageTreeNode[], payload: PostManageTreeNode): boolean => {
  for (let index = 0; index < nodes.length; index += 1) {
    const node = nodes[index]!;
    if (node.id === payload.id) {
      nodes[index] = {
        ...node,
        ...payload,
        children: node.children
      };
      return true;
    }
    if (node.children?.length && updateNode(node.children, payload)) {
      return true;
    }
  }
  return false;
};

const sortTreeNodes = (nodes: PostManageTreeNode[]) => {
  nodes.sort(sortBySort);
  nodes.forEach((node) => {
    if (node.children?.length) {
      sortTreeNodes(node.children);
    }
  });
};

const resolveOrgContext = (parentId: number) => {
  const parentNode = findPostManageNode(postTree.value, parentId);
  if (!parentNode) {
    return {
      orgName: 'Soli集团',
      orgTypeLabel: '集团'
    };
  }
  if (parentNode.nodeType === 'POST') {
    return {
      orgName: parentNode.orgName,
      orgTypeLabel: parentNode.orgTypeLabel
    };
  }
  return {
    orgName: parentNode.orgName || parentNode.nodeName,
    orgTypeLabel: parentNode.orgTypeLabel || getOrgNodeTypeLabel(parentNode.nodeType)
  };
};

const resolveNextSort = (parentId: number) => {
  const parentNode = findPostManageNode(postTree.value, parentId);
  const siblingList = parentNode?.children || [];
  return siblingList.length ? Math.max(...siblingList.map((item) => item.sort)) + 1 : 1;
};

const getCreateParentId = () => {
  if (!selectedNode.value) {
    return postTree.value[0]?.id || 1;
  }
  if (selectedNode.value.nodeType === 'POST') {
    return selectedNode.value.parentId;
  }
  return selectedNode.value.id;
};

const handleCreateRoot = () => {
  const parentId = getCreateParentId();
  formMode.value = 'create';
  formInitial.value = {
    managerName: '',
    nodeCode: '',
    nodeName: '',
    note: '',
    parentId,
    postTypeLabel: postTypeOptions[0],
    sort: resolveNextSort(parentId),
    status: '0'
  };
  formVisible.value = true;
};

const handleCreateChild = () => {
  if (!selectedNode.value) {
    return;
  }
  formMode.value = 'create';
  formInitial.value = {
    managerName: '',
    nodeCode: '',
    nodeName: '',
    note: '',
    parentId: selectedNode.value.id,
    postTypeLabel: postTypeOptions[0],
    sort: resolveNextSort(selectedNode.value.id),
    status: '0'
  };
  formVisible.value = true;
};

const handleEditPost = () => {
  if (!selectedPost.value) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = {
    id: selectedPost.value.id,
    managerName: selectedPost.value.managerName,
    nodeCode: selectedPost.value.nodeCode,
    nodeName: selectedPost.value.nodeName,
    note: selectedPost.value.note,
    parentId: selectedPost.value.parentId,
    postTypeLabel: selectedPost.value.postTypeLabel,
    sort: selectedPost.value.sort,
    status: selectedPost.value.status
  };
  formVisible.value = true;
};

const handleFormSubmit = (payload: Partial<PostManageTreeNode>) => {
  const parentId = payload.parentId || postTree.value[0]?.id || 1;
  const orgContext = resolveOrgContext(parentId);

  if (formMode.value === 'create') {
    const newId = findMaxId(postTree.value) + 1;
    const nextNode: PostManageTreeNode = {
      children: [],
      employeeIds: [],
      id: newId,
      managerName: payload.managerName || '',
      nodeCode: payload.nodeCode || `post_${newId}`,
      nodeName: payload.nodeName || '未命名岗位',
      nodeType: 'POST',
      note: payload.note || '',
      orgName: orgContext.orgName,
      orgTypeLabel: orgContext.orgTypeLabel,
      parentId,
      postTypeLabel: payload.postTypeLabel || postTypeOptions[0],
      sort: payload.sort || resolveNextSort(parentId),
      status: payload.status || '0'
    };

    appendChildNode(postTree.value, parentId, nextNode);
    sortTreeNodes(postTree.value);
    selectNode(newId);
    ElMessage.success('岗位已加入本地 mock 数据');
    return;
  }

  if (payload.id === undefined) {
    return;
  }

  const currentNode = findPostManageNode(postTree.value, payload.id);
  if (!currentNode || currentNode.nodeType !== 'POST') {
    return;
  }

  const nextNode: PostManageTreeNode = {
    ...currentNode,
    ...payload,
    children: currentNode.children || [],
    employeeIds: currentNode.employeeIds || [],
    nodeType: 'POST',
    orgName: orgContext.orgName,
    orgTypeLabel: orgContext.orgTypeLabel,
    parentId
  };

  if (currentNode.parentId !== parentId) {
    removeNode(postTree.value, nextNode.id);
    appendChildNode(postTree.value, parentId, nextNode);
  } else {
    updateNode(postTree.value, nextNode);
  }

  sortTreeNodes(postTree.value);
  selectNode(nextNode.id);
  ElMessage.success('岗位信息已更新');
};

const handleFormCancel = () => {
  formVisible.value = false;
};

const openAssignEmployeeDialog = () => {
  if (!selectedPost.value) {
    return;
  }
  dialogTargetPostId.value = selectedPost.value.id;
  assignDialogVisible.value = true;
};

const openViewEmployeeDialog = () => {
  if (!selectedPost.value) {
    return;
  }
  dialogTargetPostId.value = selectedPost.value.id;
  viewDialogVisible.value = true;
};

const handleAssignEmployeeSubmit = (employeeIds: number[]) => {
  if (!dialogTargetPost.value) {
    return;
  }
  dialogTargetPost.value.employeeIds = [...employeeIds];
  ElMessage.success('岗位员工已更新到本地 mock 数据');
};

watch(treeKeyword, (value) => {
  treeRef.value?.filter(value);
});

const firstPost = findFirstPostNode(postTree.value);
if (firstPost) {
  selectNode(firstPost.id);
}

onMounted(() => {
  nextTick(() => {
    if (selectedNodeId.value !== undefined) {
      treeRef.value?.setCurrentKey(selectedNodeId.value);
    }
  });
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

.page-alert,
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
