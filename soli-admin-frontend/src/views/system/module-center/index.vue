<template>
  <div class="app-container module-center-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="8" class="module-tree-col">
        <el-card v-loading="treeLoading" shadow="never" class="module-tree-card">
          <template #header>
            <div class="card-header">
              <span>模块树</span>
              <el-button link type="primary" @click="handleCreateRoot">新增根模块</el-button>
            </div>
          </template>
          <el-input
            v-model="moduleKeyword"
            clearable
            placeholder="请输入模块名称或编码"
            prefix-icon="Search"
            class="module-search"
          />
          <el-scrollbar class="module-tree-scrollbar">
            <el-tree
              ref="treeRef"
              :data="moduleTree"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :highlight-current="true"
              node-key="id"
              @node-click="handleNodeClick"
            >
              <template #default="{ data }">
                <div class="tree-node">
                  <div class="tree-node__head">
                    <span class="tree-node__label">{{ data.moduleName }}</span>
                    <div class="tree-node__meta">
                      <div class="tree-node__actions">
                        <el-button v-if="data.moduleType === 'CATALOG'" link type="primary" size="small"
                                   @click.stop="handleCreateChild(data)">新增
                        </el-button>
                        <el-button link size="small" @click.stop="handleEditModule(data)">编辑
                        </el-button>
                        <el-button link type="danger" size="small"
                                   @click.stop="handleDeleteModule(data)">删除
                        </el-button>
                      </div>
                      <el-tag size="small" :type="getModuleTypeTagType(data.moduleType)"
                              effect="plain">
                        {{ getModuleTypeLabel(data.moduleType) }}
                      </el-tag>
                    </div>
                  </div>
                  <div class="tree-node__code">{{ data.moduleCode }}</div>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>

      <el-col :span="16" class="module-workspace-col">
        <el-card v-loading="workspaceLoading" shadow="never" class="module-workspace-card">
          <template v-if="selectedModule">
            <div class="workspace-overview">
              <div class="overview-header">
                <div>
                  <div class="overview-title">
                    <span>{{ selectedModule.moduleName }}</span>
                    <el-tag size="small" :type="getModuleTypeTagType(selectedModule.moduleType)"
                            effect="plain">
                      {{ getModuleTypeLabel(selectedModule.moduleType) }}
                    </el-tag>
                    <el-tag size="small"
                            :type="selectedModule.status === '0' ? 'success' : 'danger'"
                            effect="plain">
                      {{ selectedModule.status === '0' ? '启用' : '停用' }}
                    </el-tag>
                    <el-tag v-if="selectedModule.statefulFlag === '1'" size="small" type="warning"
                            effect="plain">
                      状态型模块
                    </el-tag>
                  </div>
                  <div class="overview-subtitle">
                    {{ selectedModule.note || '暂无模块说明，建议补充当前模块的业务用途、适用单据或页面范围。'
                    }}
                  </div>
                </div>
                <div class="overview-actions">
                  <el-button type="primary" icon="View" @click="openContextPreview">运行时上下文
                  </el-button>
                </div>
              </div>

              <el-row :gutter="12" class="metric-row">
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">表头 Tab</div>
                    <div class="metric-card__value">{{ selectedModule.headerTabs.length }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">明细 Tab</div>
                    <div class="metric-card__value">{{ selectedModule.detailTabs.length }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">字段总数</div>
                    <div class="metric-card__value">{{ selectedFieldCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">按钮总数</div>
                    <div class="metric-card__value">{{ selectedModule.buttons.length }}</div>
                  </div>
                </el-col>
              </el-row>

              <el-descriptions :column="3" border class="module-descriptions">
                <el-descriptions-item label="模块编码">{{ selectedModule.moduleCode }}
                </el-descriptions-item>
                <el-descriptions-item label="路由地址">{{ selectedModule.routePath || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="组件路径">{{ selectedModule.componentPath || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="导航可见">
                  {{ selectedModule.navVisible === '1' ? '显示' : '隐藏' }}
                </el-descriptions-item>
                <el-descriptions-item label="状态字段">{{ selectedModule.stateFieldCode || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="上下文版本">v{{ selectedModule.contextVersion }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <el-tabs v-model="activePane" class="workspace-tabs">
              <el-tab-pane label="基础说明" name="basic">
                <el-alert
                  type="info"
                  :closable="false"
                  show-icon
                  title="模块管理是开发阶段使用的内部元数据工具。左侧仅加载模块树，选中节点后再加载 Tab、字段、按钮和状态定义；新增字段和按钮后会同步补齐字段标题默认记录与 admin 权限。"
                />
              </el-tab-pane>

              <el-tab-pane label="表头 Tab" name="header">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">表头 Tab 管理</div>
                  <div class="tab-toolbar__actions">
                    <el-button icon="Plus" @click="handleCreateTab('header')">新增 Tab</el-button>
                    <el-button icon="Edit" :disabled="!activeHeaderTabDef"
                               @click="handleEditTab('header')">
                      编辑当前 Tab
                    </el-button>
                    <el-button type="danger" plain icon="Delete" :disabled="!activeHeaderTabDef"
                               @click="handleDeleteTab('header')">
                      删除当前 Tab
                    </el-button>
                    <el-button type="primary" icon="Plus" :disabled="!activeHeaderTabDef"
                               @click="handleCreateField('header')">
                      新增字段
                    </el-button>
                  </div>
                </div>
                <el-empty v-if="!selectedModule.headerTabs.length"
                          description="当前模块还没有配置表头 Tab" />
                <template v-else>
                  <el-tabs v-model="activeHeaderTab" type="border-card" class="sub-tabs">
                    <el-tab-pane
                      v-for="tab in selectedModule.headerTabs"
                      :key="tab.tabInfo.tabCode"
                      :label="`${tab.tabInfo.tabName} (${tab.fields.length})`"
                      :name="tab.tabInfo.tabCode"
                    >
                      <div class="sub-tab-summary">
                        <span>Tab 编码：{{ tab.tabInfo.tabCode }}</span>
                        <span>排序：{{ tab.tabInfo.sort }}</span>
                        <span>说明：{{ tab.tabInfo.note || '-' }}</span>
                      </div>
                      <el-table :data="tab.fields.slice().sort(sortBySort)" border
                                style="width: 100%">
                        <el-table-column prop="fieldCode" label="字段编码" min-width="180" />
                        <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                        <el-table-column prop="componentType" label="组件类型" width="120" />
                        <el-table-column prop="dataPath" label="数据路径" min-width="220"
                                         show-overflow-tooltip />
                        <el-table-column prop="valueType" label="值类型" width="120" />
                        <el-table-column label="必填" width="90" align="center">
                          <template #default="scope">
                            <el-tag size="small"
                                    :type="scope.row.requiredFlag === '1' ? 'danger' : 'info'"
                                    effect="plain">
                              {{ scope.row.requiredFlag === '1' ? '是' : '否' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="sort" label="排序" width="80" align="center" />
                        <el-table-column label="操作" width="180" fixed="right" align="center">
                          <template #default="scope">
                            <el-button link type="primary"
                                       @click="handleEditField('header', tab.tabInfo.tabCode, scope.row)">
                              编辑
                            </el-button>
                            <el-button link type="danger"
                                       @click="handleDeleteField('header', tab.tabInfo.tabCode, scope.row)">
                              删除
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-tab-pane>
                  </el-tabs>
                </template>
              </el-tab-pane>

              <el-tab-pane label="明细 Tab" name="detail">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">明细 Tab 管理</div>
                  <div class="tab-toolbar__actions">
                    <el-button icon="Plus" @click="handleCreateTab('detail')">新增 Tab</el-button>
                    <el-button icon="Edit" :disabled="!activeDetailTabDef"
                               @click="handleEditTab('detail')">
                      编辑当前 Tab
                    </el-button>
                    <el-button type="danger" plain icon="Delete" :disabled="!activeDetailTabDef"
                               @click="handleDeleteTab('detail')">
                      删除当前 Tab
                    </el-button>
                    <el-button type="primary" icon="Plus" :disabled="!activeDetailTabDef"
                               @click="handleCreateField('detail')">
                      新增字段
                    </el-button>
                  </div>
                </div>
                <el-empty v-if="!selectedModule.detailTabs.length"
                          description="当前模块还没有配置明细 Tab" />
                <template v-else>
                  <el-tabs v-model="activeDetailTab" type="border-card" class="sub-tabs">
                    <el-tab-pane
                      v-for="tab in selectedModule.detailTabs"
                      :key="tab.tabInfo.tabCode"
                      :label="`${tab.tabInfo.tabName} (${tab.fields.length})`"
                      :name="tab.tabInfo.tabCode"
                    >
                      <div class="sub-tab-summary">
                        <span>Tab 编码：{{ tab.tabInfo.tabCode }}</span>
                        <span>排序：{{ tab.tabInfo.sort }}</span>
                        <span>说明：{{ tab.tabInfo.note || '-' }}</span>
                      </div>
                      <el-table :data="tab.fields.slice().sort(sortBySort)" border
                                style="width: 100%">
                        <el-table-column prop="fieldCode" label="字段编码" min-width="180" />
                        <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                        <el-table-column prop="componentType" label="组件类型" width="120" />
                        <el-table-column prop="dataPath" label="数据路径" min-width="220"
                                         show-overflow-tooltip />
                        <el-table-column prop="valueType" label="值类型" width="120" />
                        <el-table-column label="必填" width="90" align="center">
                          <template #default="scope">
                            <el-tag size="small"
                                    :type="scope.row.requiredFlag === '1' ? 'danger' : 'info'"
                                    effect="plain">
                              {{ scope.row.requiredFlag === '1' ? '是' : '否' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="sort" label="排序" width="80" align="center" />
                        <el-table-column label="操作" width="180" fixed="right" align="center">
                          <template #default="scope">
                            <el-button link type="primary"
                                       @click="handleEditField('detail', tab.tabInfo.tabCode, scope.row)">
                              编辑
                            </el-button>
                            <el-button link type="danger"
                                       @click="handleDeleteField('detail', tab.tabInfo.tabCode, scope.row)">
                              删除
                            </el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-tab-pane>
                  </el-tabs>
                </template>
              </el-tab-pane>

              <el-tab-pane label="按钮定义" name="button">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">按钮定义管理</div>
                  <div class="tab-toolbar__actions">
                    <el-button type="primary" icon="Plus" @click="handleCreateButton">新增按钮
                    </el-button>
                  </div>
                </div>
                <el-empty v-if="!selectedModule.buttons.length"
                          description="当前模块还没有配置按钮定义" />
                <template v-else>
                  <div v-for="group in buttonGroups" :key="group.area" class="button-group-card">
                    <div class="button-group-card__header">
                      <span>{{ getButtonAreaLabel(group.area) }}</span>
                      <el-tag size="small" effect="plain">{{ group.buttons.length }} 个按钮</el-tag>
                    </div>
                    <el-table :data="group.buttons.slice().sort(sortBySort)" border
                              style="width: 100%">
                      <el-table-column prop="buttonCode" label="按钮编码" min-width="180" />
                      <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                      <el-table-column label="所属区域" width="160">
                        <template #default="scope">{{ getButtonAreaLabel(scope.row.area) }}
                        </template>
                      </el-table-column>
                      <el-table-column prop="sort" label="排序" width="90" align="center" />
                      <el-table-column prop="note" label="说明" min-width="200"
                                       show-overflow-tooltip />
                      <el-table-column label="操作" width="180" fixed="right" align="center">
                        <template #default="scope">
                          <el-button link type="primary" @click="handleEditButton(scope.row)">编辑
                          </el-button>
                          <el-button link type="danger" @click="handleDeleteButton(scope.row)">
                            删除
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </template>
              </el-tab-pane>
            </el-tabs>
          </template>

          <div v-else class="workspace-empty">
            <el-empty description="请选择左侧模块节点查看详情" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <module-center-form
      v-model="formVisible"
      :mode="formMode"
      :initial-data="formInitial"
      :tree-data="moduleTree"
      :current-id="formMode === 'edit' ? formInitial.id : undefined"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
    <module-tab-form
      v-model="tabFormVisible"
      :mode="tabFormMode"
      :initial-data="tabFormInitial"
      :title="tabFormTitle"
      @submit="handleTabFormSubmit"
      @cancel="handleTabFormCancel"
    />
    <module-field-form
      v-model="fieldFormVisible"
      :mode="fieldFormMode"
      :initial-data="fieldFormInitial"
      :title="fieldFormTitle"
      @submit="handleFieldFormSubmit"
      @cancel="handleFieldFormCancel"
    />
    <module-button-form
      v-model="buttonFormVisible"
      :mode="buttonFormMode"
      :initial-data="buttonFormInitial"
      :title="buttonFormTitle"
      @submit="handleButtonFormSubmit"
      @cancel="handleButtonFormCancel"
    />
    <el-drawer v-model="contextPreviewVisible" title="运行时模块上下文预览" size="46%">
      <pre class="context-preview">{{ contextPreviewJson }}</pre>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ModuleButtonForm from './components/ModuleButtonForm.vue'
import ModuleCenterForm from './components/ModuleCenterForm.vue'
import ModuleFieldForm from './components/ModuleFieldForm.vue'
import ModuleTabForm from './components/ModuleTabForm.vue'
import {
  buttonAreaLabelMap,
  createModule,
  createModuleButton,
  createModuleField,
  createModuleTab,
  deleteModule,
  deleteModuleButton,
  deleteModuleField,
  deleteModuleTab,
  getModuleContextPreview,
  getModuleDetail,
  getModuleTree,
  moduleTypeLabelMap,
  updateModule,
  updateModuleButton,
  updateModuleField,
  updateModuleTab,
  type ModuleButtonArea,
  type ModuleButtonDefinition,
  type ModuleDetail,
  type ModuleFieldDefinition,
  type ModuleFormModel,
  type ModuleTabDefinition,
  type ModuleTreeNode,
  type ModuleType,
  type YesNo
} from '@/api/moduleCenter'

defineOptions({ name: 'SystemModuleCenter' })

interface ModuleNode extends ModuleTreeNode {
  routePath: string;
  componentPath: string;
  icon: string;
  navVisible: YesNo;
  stateFieldCode: string;
  contextVersion: number;
  note: string;
  headerTabs: ModuleTabDefinition[];
  detailTabs: ModuleTabDefinition[];
  buttons: ModuleButtonDefinition[];
  children?: ModuleNode[];
}

interface ModuleTabFormModel {
  id?: number;
  note?: string;
  sort?: number;
  tabCode?: string;
  tabName?: string;
}

const treeRef = ref()
const treeLoading = ref(false)
const workspaceLoading = ref(false)
const moduleKeyword = ref('')
const moduleTree = ref<ModuleNode[]>([])
const selectedModuleId = ref<number>()
const activePane = ref<'basic' | 'header' | 'detail' | 'button'>('basic')
const activeHeaderTab = ref('')
const activeDetailTab = ref('')
const contextPreviewVisible = ref(false)
const contextPreviewJson = ref('')
const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formInitial = ref<Partial<ModuleFormModel>>({})
const tabFormVisible = ref(false)
const tabFormMode = ref<'create' | 'edit'>('create')
const tabFormInitial = ref<Partial<ModuleTabFormModel>>({})
const tabFormScope = ref<'header' | 'detail'>('header')
const fieldFormVisible = ref(false)
const fieldFormMode = ref<'create' | 'edit'>('create')
const fieldFormInitial = ref<Partial<ModuleFieldDefinition>>({})
const fieldFormScope = ref<'header' | 'detail'>('header')
const fieldFormTabCode = ref('')
const buttonFormVisible = ref(false)
const buttonFormMode = ref<'create' | 'edit'>('create')
const buttonFormInitial = ref<Partial<ModuleButtonDefinition>>({})

function findModuleNode(nodes: ModuleNode[], id?: number): ModuleNode | undefined {
  if (id === undefined) {
    return undefined
  }
  for (const node of nodes) {
    if (node.id === id) {
      return node
    }
    if (node.children?.length) {
      const matched = findModuleNode(node.children, id)
      if (matched) {
        return matched
      }
    }
  }
  return undefined
}

function findFirstEditableModule(nodes: ModuleNode[]): ModuleNode | undefined {
  for (const node of nodes) {
    if (node.moduleType !== 'CATALOG') {
      return node
    }
    if (node.children?.length) {
      const matched = findFirstEditableModule(node.children)
      if (matched) {
        return matched
      }
    }
  }
  return undefined
}

function mapTreeNode(node: ModuleTreeNode): ModuleNode {
  return {
    ...node,
    buttons: [],
    children: node.children?.map(mapTreeNode),
    componentPath: '',
    contextVersion: 0,
    detailTabs: [],
    headerTabs: [],
    icon: 'Document',
    navVisible: '1',
    note: '',
    routePath: '',
    stateFieldCode: ''
  }
}

function resolveActiveTabCode(tabs: ModuleTabDefinition[], preferred?: string) {
  if (preferred && tabs.some((item) => item.tabInfo.tabCode === preferred)) {
    return preferred
  }
  return tabs[0]?.tabInfo.tabCode || ''
}

const selectedModule = computed(() => findModuleNode(moduleTree.value, selectedModuleId.value))
const selectedFieldCount = computed(() => {
  if (!selectedModule.value) {
    return 0
  }
  return [...selectedModule.value.headerTabs, ...selectedModule.value.detailTabs].reduce((sum, tab) => sum + tab.fields.length, 0)
})
const buttonGroups = computed(() => {
  if (!selectedModule.value) {
    return [] as Array<{ area: ModuleButtonArea; buttons: ModuleButtonDefinition[] }>
  }
  return (Object.keys(buttonAreaLabelMap) as ModuleButtonArea[])
    .map((area) => ({
      area,
      buttons: selectedModule.value?.buttons.filter((button) => button.area === area) || []
    }))
    .filter((item) => item.buttons.length > 0)
})
const activeHeaderTabDef = computed(() => selectedModule.value?.headerTabs.find((tab) => tab.tabInfo.tabCode === activeHeaderTab.value))
const activeDetailTabDef = computed(() => selectedModule.value?.detailTabs.find((tab) => tab.tabInfo.tabCode === activeDetailTab.value))
const tabFormTitle = computed(() => `${tabFormMode.value === 'create' ? '新增' : '编辑'}${tabFormScope.value === 'header' ? '表头' : '明细'} Tab`)
const fieldFormTitle = computed(() => `${fieldFormMode.value === 'create' ? '新增' : '编辑'}${fieldFormScope.value === 'header' ? '表头' : '明细'}字段`)
const buttonFormTitle = computed(() => (buttonFormMode.value === 'create' ? '新增按钮' : '编辑按钮'))

function sortBySort<T extends { sort: number }>(left: T, right: T) {
  return left.sort - right.sort
}

function getModuleTypeTagType(type: ModuleType) {
  if (type === 'CATALOG') {
    return 'info'
  }
  return 'warning'
}

function getModuleTypeLabel(type: ModuleType) {
  return moduleTypeLabelMap[type]
}

function getButtonAreaLabel(area: ModuleButtonArea) {
  return buttonAreaLabelMap[area]
}

function getTabList(scope: 'header' | 'detail', module = selectedModule.value) {
  if (!module) {
    return undefined
  }
  return scope === 'header' ? module.headerTabs : module.detailTabs
}

function getActiveTab(scope: 'header' | 'detail', module = selectedModule.value) {
  const list = getTabList(scope, module)
  if (!list) {
    return undefined
  }
  return list.find((tab) => tab.tabInfo.tabCode === (scope === 'header' ? activeHeaderTab.value : activeDetailTab.value))
}

function filterNode(value: string, data: ModuleNode) {
  return !value || data.moduleName.includes(value) || data.moduleCode.includes(value)
}

function selectModule(id?: number) {
  selectedModuleId.value = id
  if (id !== undefined) {
    treeRef.value?.setCurrentKey(id)
  }
}

function hydrateModuleDetail(detail: ModuleDetail, preferredHeader?: string, preferredDetail?: string) {
  const currentNode = findModuleNode(moduleTree.value, detail.id)
  if (!currentNode) {
    return
  }
  Object.assign(currentNode, detail)
  activeHeaderTab.value = resolveActiveTabCode(detail.headerTabs, preferredHeader || activeHeaderTab.value)
  activeDetailTab.value = resolveActiveTabCode(detail.detailTabs, preferredDetail || activeDetailTab.value)
}

async function loadModuleDetail(id: number, preferredHeader?: string, preferredDetail?: string) {
  workspaceLoading.value = true
  try {
    const { data } = await getModuleDetail(id)
    if (selectedModuleId.value === id) {
      hydrateModuleDetail(data, preferredHeader, preferredDetail)
    }
  } finally {
    workspaceLoading.value = false
  }
}

async function handleModuleSelection(id?: number) {
  selectModule(id)
  if (id === undefined) {
    activeHeaderTab.value = ''
    activeDetailTab.value = ''
    return
  }
  await loadModuleDetail(id)
}

async function loadTree(preferId?: number) {
  treeLoading.value = true
  try {
    const { data } = await getModuleTree()
    moduleTree.value = data.map(mapTreeNode)
    const nextId = findModuleNode(moduleTree.value, preferId)?.id
      || findModuleNode(moduleTree.value, selectedModuleId.value)?.id
      || findFirstEditableModule(moduleTree.value)?.id
      || moduleTree.value[0]?.id
    await handleModuleSelection(nextId)
  } finally {
    treeLoading.value = false
  }
}

function handleNodeClick(node: ModuleNode) {
  void handleModuleSelection(node.id)
}

function handleCreateRoot() {
  formMode.value = 'create'
  formInitial.value = {
    componentPath: '',
    icon: 'Document',
    moduleCode: '',
    moduleName: '',
    moduleType: 'CATALOG',
    navVisible: '1',
    note: '',
    parentId: 0,
    routePath: '',
    sort: 1,
    stateFieldCode: '',
    statefulFlag: '0',
    status: '0'
  }
  formVisible.value = true
}

function handleCreateChild(node?: ModuleNode) {
  const targetNode = node || selectedModule.value
  if (!targetNode) {
    return
  }
  if (targetNode.moduleType !== 'CATALOG') {
    ElMessage.warning('只有目录节点可以新增下级模块')
    return
  }
  selectModule(targetNode.id)
  formMode.value = 'create'
  formInitial.value = {
    componentPath: '',
    icon: targetNode.moduleType === 'CATALOG' ? 'Document' : targetNode.icon,
    moduleCode: '',
    moduleName: '',
    moduleType: 'PAGE',
    navVisible: '1',
    note: '',
    parentId: targetNode.id,
    routePath: '',
    sort: 1,
    stateFieldCode: '',
    statefulFlag: '0',
    status: '0'
  }
  formVisible.value = true
}

async function handleEditModule(node?: ModuleNode) {
  const targetNode = node || selectedModule.value
  if (!targetNode) {
    return
  }
  selectModule(targetNode.id)
  workspaceLoading.value = true
  try {
    const { data } = await getModuleDetail(targetNode.id)
    hydrateModuleDetail(data)
    formMode.value = 'edit'
    formInitial.value = { ...data }
    formVisible.value = true
  } finally {
    workspaceLoading.value = false
  }
}

async function handleDeleteModule(node?: ModuleNode) {
  const targetNode = node || selectedModule.value
  if (!targetNode) {
    return
  }
  const currentId = targetNode.id
  const parentId = targetNode.parentId
  try {
    await ElMessageBox.confirm(`确认删除模块「${targetNode.moduleName}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModule(currentId)
  ElMessage.success('模块删除成功')
  await loadTree(parentId > 0 ? parentId : undefined)
}

async function handleFormSubmit(payload: ModuleFormModel) {
  const requestPayload: ModuleFormModel = {
    componentPath: payload.componentPath || '',
    icon: payload.icon || 'Document',
    moduleCode: payload.moduleCode || '',
    moduleName: payload.moduleName || '',
    moduleType: payload.moduleType || 'PAGE',
    navVisible: payload.navVisible || '1',
    note: payload.note || '',
    parentId: payload.parentId || 0,
    routePath: payload.routePath || '',
    sort: payload.sort || 1,
    stateFieldCode: payload.stateFieldCode || '',
    statefulFlag: payload.statefulFlag || '0',
    status: payload.status || '0'
  }
  if (formMode.value === 'create') {
    const { data } = await createModule(requestPayload)
    ElMessage.success('模块新增成功')
    formVisible.value = false
    await loadTree(data)
    return
  }
  if (!payload.id) {
    return
  }
  await updateModule({ ...requestPayload, id: payload.id })
  ElMessage.success('模块编辑成功')
  formVisible.value = false
  await loadTree(payload.id)
}

function handleFormCancel() {
  formVisible.value = false
}

function handleCreateTab(scope: 'header' | 'detail') {
  tabFormScope.value = scope
  tabFormMode.value = 'create'
  tabFormInitial.value = {
    note: '',
    sort: (getTabList(scope)?.length || 0) + 1,
    tabCode: '',
    tabName: ''
  }
  tabFormVisible.value = true
}

function handleEditTab(scope: 'header' | 'detail') {
  const currentTab = getActiveTab(scope)
  if (!currentTab) {
    ElMessage.warning('请先选择需要编辑的 Tab')
    return
  }
  tabFormScope.value = scope
  tabFormMode.value = 'edit'
  tabFormInitial.value = { ...currentTab.tabInfo }
  tabFormVisible.value = true
}

async function handleDeleteTab(scope: 'header' | 'detail') {
  const module = selectedModule.value
  const currentTab = getActiveTab(scope)
  if (!module || !currentTab) {
    ElMessage.warning('请先选择需要删除的 Tab')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除 Tab「${currentTab.tabInfo.tabName}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModuleTab(currentTab.tabInfo.id)
  ElMessage.success('Tab 删除成功')
  await loadModuleDetail(module.id)
}

async function handleTabFormSubmit(payload: Partial<ModuleTabFormModel>) {
  const module = selectedModule.value
  if (!module) {
    return
  }
  const tabScope = tabFormScope.value === 'header' ? 'HEADER' : 'DETAIL'
  if (tabFormMode.value === 'create') {
    await createModuleTab({
      moduleId: module.id,
      note: payload.note || '',
      sort: payload.sort || (getTabList(tabFormScope.value)?.length || 0) + 1,
      status: '0',
      tabCode: payload.tabCode || '',
      tabName: payload.tabName || '',
      tabScope
    })
    ElMessage.success('Tab 新增成功')
  } else if (payload.id) {
    await updateModuleTab({
      id: payload.id,
      moduleId: module.id,
      note: payload.note || '',
      sort: payload.sort || 1,
      status: '0',
      tabCode: payload.tabCode || '',
      tabName: payload.tabName || '',
      tabScope
    })
    ElMessage.success('Tab 编辑成功')
  }
  tabFormVisible.value = false
  await loadModuleDetail(
    module.id,
    tabFormScope.value === 'header' ? payload.tabCode : activeHeaderTab.value,
    tabFormScope.value === 'detail' ? payload.tabCode : activeDetailTab.value
  )
}

function handleTabFormCancel() {
  tabFormVisible.value = false
}

function handleCreateField(scope: 'header' | 'detail') {
  const currentTab = getActiveTab(scope)
  if (!currentTab) {
    ElMessage.warning('请先选择一个 Tab，再新增字段')
    return
  }
  fieldFormScope.value = scope
  fieldFormTabCode.value = currentTab.tabInfo.tabCode
  fieldFormMode.value = 'create'
  fieldFormInitial.value = {
    componentType: 'input',
    dataPath: '',
    defaultTitle: '',
    fieldCode: '',
    note: '',
    requiredFlag: '0',
    sort: currentTab.fields.length + 1,
    valueType: 'string'
  }
  fieldFormVisible.value = true
}

function handleEditField(scope: 'header' | 'detail', tabCode: string, field: ModuleFieldDefinition) {
  fieldFormScope.value = scope
  fieldFormTabCode.value = tabCode
  fieldFormMode.value = 'edit'
  fieldFormInitial.value = { ...field }
  fieldFormVisible.value = true
}

async function handleDeleteField(scope: 'header' | 'detail', tabCode: string, field: ModuleFieldDefinition) {
  const module = selectedModule.value
  if (!module) {
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除字段「${field.defaultTitle}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModuleField(field.id)
  ElMessage.success('字段删除成功')
  await loadModuleDetail(
    module.id,
    scope === 'header' ? tabCode : activeHeaderTab.value,
    scope === 'detail' ? tabCode : activeDetailTab.value
  )
}

async function handleFieldFormSubmit(payload: Partial<ModuleFieldDefinition>) {
  const module = selectedModule.value
  const currentTab = getTabList(fieldFormScope.value, module)?.find((tab) => tab.tabInfo.tabCode === fieldFormTabCode.value)
  if (!module || !currentTab) {
    return
  }
  const requestPayload = {
    componentType: payload.componentType || 'input',
    dataPath: payload.dataPath || '',
    defaultTitle: payload.defaultTitle || '',
    fieldCode: payload.fieldCode || '',
    fieldScope: currentTab.tabInfo.tabScope,
    moduleId: module.id,
    note: payload.note || '',
    requiredFlag: (payload.requiredFlag || '0') as YesNo,
    sort: payload.sort || currentTab.fields.length + 1,
    status: '0' as YesNo,
    tabId: currentTab.tabInfo.id,
    valueType: payload.valueType || 'string'
  }
  if (fieldFormMode.value === 'create') {
    await createModuleField(requestPayload)
    ElMessage.success('字段新增成功')
  } else if (payload.id) {
    await updateModuleField({ ...requestPayload, id: payload.id })
    ElMessage.success('字段编辑成功')
  }
  fieldFormVisible.value = false
  await loadModuleDetail(
    module.id,
    fieldFormScope.value === 'header' ? fieldFormTabCode.value : activeHeaderTab.value,
    fieldFormScope.value === 'detail' ? fieldFormTabCode.value : activeDetailTab.value
  )
}

function handleFieldFormCancel() {
  fieldFormVisible.value = false
}

function handleCreateButton() {
  if (!selectedModule.value) {
    return
  }
  buttonFormMode.value = 'create'
  buttonFormInitial.value = {
    area: 'HEADER_TOOLBAR',
    buttonCode: '',
    defaultTitle: '',
    note: '',
    sort: selectedModule.value.buttons.length + 1
  }
  buttonFormVisible.value = true
}

function handleEditButton(button: ModuleButtonDefinition) {
  buttonFormMode.value = 'edit'
  buttonFormInitial.value = { ...button }
  buttonFormVisible.value = true
}

async function handleDeleteButton(button: ModuleButtonDefinition) {
  const module = selectedModule.value
  if (!module) {
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除按钮「${button.defaultTitle}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModuleButton(button.id)
  ElMessage.success('按钮删除成功')
  await loadModuleDetail(module.id, activeHeaderTab.value, activeDetailTab.value)
}

async function handleButtonFormSubmit(payload: Partial<ModuleButtonDefinition>) {
  const module = selectedModule.value
  if (!module) {
    return
  }
  const requestPayload = {
    area: (payload.area || 'HEADER_TOOLBAR') as ModuleButtonArea,
    buttonCode: payload.buttonCode || '',
    defaultTitle: payload.defaultTitle || '',
    moduleId: module.id,
    note: payload.note || '',
    sort: payload.sort || module.buttons.length + 1,
    status: '0' as YesNo
  }
  if (buttonFormMode.value === 'create') {
    await createModuleButton(requestPayload)
    ElMessage.success('按钮新增成功')
  } else if (payload.id) {
    await updateModuleButton({ ...requestPayload, id: payload.id })
    ElMessage.success('按钮编辑成功')
  }
  buttonFormVisible.value = false
  await loadModuleDetail(module.id, activeHeaderTab.value, activeDetailTab.value)
}

function handleButtonFormCancel() {
  buttonFormVisible.value = false
}

async function openContextPreview() {
  if (!selectedModule.value) {
    return
  }
  const { data } = await getModuleContextPreview(selectedModule.value.id)
  contextPreviewJson.value = JSON.stringify(data, null, 2)
  contextPreviewVisible.value = true
}

watch(moduleKeyword, (value) => {
  treeRef.value?.filter(value)
})

onMounted(() => {
  void loadTree().then(() => {
    nextTick(() => {
      if (selectedModuleId.value !== undefined) {
        treeRef.value?.setCurrentKey(selectedModuleId.value)
      }
    })
  })
})
</script>

<style scoped>
.module-center-page, .module-tree-col, .module-workspace-col, .module-tree-card, .module-workspace-card {
  height: 100%;
  width: 100%;
  min-height: 0;
}

.module-center-page {
  box-sizing: border-box;
  overflow: hidden
}

:deep(.module-center-page > .el-row), :deep(.module-tree-card .el-card__body), :deep(.module-workspace-card .el-card__body) {
  height: 100%
}

.module-tree-col, .module-workspace-col, .module-tree-card, .module-workspace-card, :deep(.module-tree-card .el-card__body), :deep(.module-workspace-card .el-card__body), .workspace-overview {
  display: flex;
  flex-direction: column
}

.module-tree-col, .module-workspace-col, :deep(.module-tree-card .el-card__body), :deep(.module-workspace-card .el-card__body), .module-tree-card, .module-workspace-card, .module-tree-scrollbar, .workspace-tabs {
  flex: 1
}

.workspace-tabs {
  width: 100%;
  min-width: 0;
}

:deep(.workspace-tabs > .el-tabs__content) {
  width: 100%;
  min-width: 0;
}

:deep(.workspace-tabs > .el-tabs__content > .el-tab-pane) {
  width: 100%;
  min-width: 0;
  display: flex;
  flex-direction: column;
}

:deep(.workspace-tabs .el-tab-pane > *) {
  min-width: 0;
}

.module-tree-card, .module-workspace-card, .metric-card {
  border-radius: 12px
}

.card-header, .overview-header, .tab-toolbar, .button-group-card__header, .overview-actions, .tab-toolbar__actions, .sub-tab-summary {
  display: flex;
  gap: 8px
}

.card-header, .overview-header, .tab-toolbar, .button-group-card__header {
  align-items: center;
  justify-content: space-between
}

.overview-actions, .tab-toolbar__actions, .sub-tab-summary {
  flex-wrap: wrap
}

.module-search, .tab-toolbar, .module-descriptions, .button-group-card + .button-group-card {
  margin-bottom: 12px
}

.tree-node, .tree-node__head, .tree-node__meta, .tree-node__actions, .workspace-empty {
  display: flex
}

.tree-node__head {
  justify-content: space-between;
  gap: 8px
}

.tree-node {
  width: 100%;
  min-width: 0;
  padding-right: 8px;
  flex-direction: column
}

.tree-node, .tree-node__head, .tree-node__label {
  min-width: 0
}

.tree-node__meta {
  align-items: center;
  gap: 8px;
  margin-left: 12px;
  flex-shrink: 0
}

.tree-node__actions {
  align-items: center;
  gap: 2px;
  opacity: 0;
  transition: opacity .2s ease
}

.tree-node__label {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap
}

.tree-node__code, .overview-subtitle, .sub-tab-summary {
  color: var(--el-text-color-secondary);
  font-size: 13px
}

.tree-node__code {
  margin-top: 4px;
  line-height: 1.35
}

:deep(.module-tree-card .el-tree-node__content) {
  height: auto;
  align-items: flex-start;
  padding: 8px 0 8px 2px
}

:deep(.module-tree-card .el-tree-node__content:hover .tree-node__actions), :deep(.module-tree-card .is-current > .el-tree-node__content .tree-node__actions) {
  opacity: 1
}

:deep(.module-tree-card .el-tree-node__expand-icon) {
  margin-top: 10px
}

:deep(.module-tree-card .el-tree-node) {
  min-width: 0
}

.workspace-empty {
  min-height: 0;
  align-items: center;
  justify-content: center
}

.workspace-overview {
  flex-shrink: 0;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter)
}

.overview-title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 600
}

.metric-row {
  margin: 18px 0 12px
}

.metric-card {
  height: 100%;
  border: 1px solid var(--el-border-color-lighter);
  background: linear-gradient(180deg, #fcfdff 0%, #f8fafc 100%);
  padding: 16px 18px
}

.metric-card__label {
  color: var(--el-text-color-secondary);
  font-size: 13px
}

.metric-card__value {
  margin-top: 10px;
  font-size: 28px;
  font-weight: 600
}

.workspace-tabs, :deep(.workspace-tabs > .el-tabs__content), .sub-tabs, :deep(.sub-tabs > .el-tabs__content) {
  display: flex;
  min-height: 0;
  overflow: hidden
}

.workspace-tabs, .sub-tabs, :deep(.workspace-tabs > .el-tabs__content), :deep(.sub-tabs > .el-tabs__content) {
  flex: 1
}

:deep(.workspace-tabs > .el-tabs__content > .el-tab-pane), :deep(.sub-tabs > .el-tabs__content > .el-tab-pane) {
  min-height: 0;
  overflow: auto;
  padding-right: 4px
}

:deep(.sub-tabs .el-table), :deep(.button-group-card .el-table) {
  width: 100%
}

.context-preview {
  min-height: calc(100vh - 220px);
  overflow: auto;
  margin: 0;
  padding: 16px;
  border-radius: 12px;
  background: #0f172a;
  color: #dbeafe;
  font-size: 13px;
  line-height: 1.65;
  white-space: pre-wrap;
  word-break: break-word
}

@media (max-width: 1280px) {
  .module-center-page {
    height: auto;
    min-height: calc(100vh - 84px)
  }

  :deep(.module-center-page > .el-row) {
    height: auto
  }

  .overview-header {
    flex-direction: column;
    align-items: flex-start
  }

  .overview-actions {
    width: 100%
  }
}
</style>
