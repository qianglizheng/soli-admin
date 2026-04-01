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
          <el-input v-model="moduleKeyword" clearable placeholder="请输入模块名称或编码"
                    prefix-icon="Search" class="module-search" />
          <el-scrollbar class="module-tree-scrollbar">
            <el-tree ref="treeRef" :data="moduleTree" :expand-on-click-node="false"
                     :filter-node-method="filterNode" :highlight-current="true" node-key="id"
                     @node-click="handleNodeClick">
              <template #default="{ data }">
                <div class="tree-node">
                  <div class="tree-node__head">
                    <span class="tree-node__label">{{ data.moduleName }}</span>
                    <div class="tree-node__meta">
                      <div class="tree-node__actions">
                        <el-button v-if="getEnumCode(data.moduleType) === 'CATALOG'" link type="primary"
                                   size="small" @click.stop="handleCreateChild(data)">新增
                        </el-button>
                        <el-button link size="small" @click.stop="handleEditModule(data)">编辑
                        </el-button>
                        <el-button link type="danger" size="small"
                                   @click.stop="handleDeleteModule(data)">删除
                        </el-button>
                      </div>
                      <el-tag size="small" :type="getModuleTypeTagType(data.moduleType)"
                              effect="plain">{{ getModuleTypeLabel(data.moduleType) }}
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
                            effect="plain">{{ getModuleTypeLabel(selectedModule.moduleType) }}
                    </el-tag>
                    <el-tag size="small"
                            :type="getEnumCode(selectedModule.status) === '0' ? 'success' : 'danger'"
                            effect="plain">{{ getEnumCode(selectedModule.status) === '0' ? '启用' : '停用' }}
                    </el-tag>
                    <el-tag v-if="getEnumCode(selectedModule.statefulFlag) === '1'" size="small" type="warning"
                            effect="plain">状态型模块
                    </el-tag>
                  </div>
                  <div class="overview-subtitle">
                    模块管理仅供开发阶段维护模块、组件、字段和按钮元数据，运行时直接消费组件和字段配置。
                  </div>
                </div>
                <div class="overview-actions">
                  <el-button type="primary" icon="View" @click="openContextPreview">运行时上下文
                  </el-button>
                </div>
              </div>

              <el-row :gutter="12" class="metric-row">
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">组件数</div>
                    <div class="metric-card__value">{{ selectedModule.components.length }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
                  <div class="metric-card">
                    <div class="metric-card__label">字段总数</div>
                    <div class="metric-card__value">{{ selectedFieldCount }}</div>
                  </div>
                </el-col>
                <el-col :span="8">
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
                  {{ getEnumCode(selectedModule.navVisible) === '1' ? '显示' : '隐藏' }}
                </el-descriptions-item>
                <el-descriptions-item label="状态字段">{{ selectedModule.stateFieldCode || '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="上下文版本">v{{ selectedModule.contextVersion }}
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <el-tabs v-model="activePane" class="workspace-tabs">
              <el-tab-pane label="基础说明" name="basic">
                <el-alert type="info" :closable="false" show-icon
                          title="模块中心只维护开发元数据。字段新增后会同步写入字段标题表，并为 admin 默认补齐字段/按钮权限。" />
              </el-tab-pane>

              <el-tab-pane label="组件" name="component">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">组件管理</div>
                  <div class="tab-toolbar__actions">
                    <el-button icon="Plus" @click="handleCreateComponent">新增组件</el-button>
                    <el-button icon="Edit" :disabled="!activeComponentDef"
                               @click="handleEditComponent">编辑当前组件
                    </el-button>
                    <el-button type="danger" plain icon="Delete" :disabled="!activeComponentDef"
                               @click="handleDeleteComponent">删除当前组件
                    </el-button>
                    <el-button type="primary" icon="Plus" :disabled="!activeComponentDef"
                               @click="handleCreateField">新增字段
                    </el-button>
                  </div>
                </div>
                <el-empty v-if="!selectedModule.components.length"
                          description="当前模块还没有配置组件" />
                <el-tabs v-else v-model="activeComponentCode" type="border-card" class="sub-tabs">
                  <el-tab-pane v-for="component in selectedModule.components"
                               :key="component.componentInfo.componentCode"
                               :label="`${component.componentInfo.componentName} (${component.fields.length})`"
                               :name="component.componentInfo.componentCode">
                    <div class="sub-tab-summary">
                      <span>组件编码：{{ component.componentInfo.componentCode }}</span>
                      <span>排序：{{ component.componentInfo.sort }}</span>
                      <span>说明：{{ component.componentInfo.note || '-' }}</span>
                    </div>
                    <el-table :data="component.fields.slice().sort(sortBySort)" border
                              style="width: 100%">
                      <el-table-column prop="fieldCode" label="字段编码" min-width="180" />
                      <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                      <el-table-column label="组件类型" width="120">
                        <template #default="scope">
                          {{ scope.row.componentType?.name || '-' }}
                        </template>
                      </el-table-column>
                      <el-table-column prop="dataPath" label="数据路径" min-width="220"
                                       show-overflow-tooltip />
                      <el-table-column label="值类型" width="120">
                        <template #default="scope">
                          {{ scope.row.valueType?.name || '-' }}
                        </template>
                      </el-table-column>
                      <el-table-column label="必填" width="90" align="center">
                        <template #default="scope">
                          <el-tag size="small"
                                  :type="getEnumCode(scope.row.requiredFlag) === '1' ? 'danger' : 'info'"
                                  effect="plain">{{ getEnumCode(scope.row.requiredFlag) === '1' ? '是' : '否' }}
                          </el-tag>
                        </template>
                      </el-table-column>
                      <el-table-column prop="sort" label="排序" width="80" align="center" />
                      <el-table-column label="操作" width="180" fixed="right" align="center">
                        <template #default="scope">
                          <el-button link type="primary"
                                     @click="handleEditField(component.componentInfo.componentCode, scope.row)">
                            编辑
                          </el-button>
                          <el-button link type="danger"
                                     @click="handleDeleteField(component.componentInfo.componentCode, scope.row)">
                            删除
                          </el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </el-tab-pane>
                </el-tabs>
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
                <el-table v-else :data="selectedModule.buttons.slice().sort(sortBySort)" border
                          style="width: 100%">
                  <el-table-column prop="buttonCode" label="按钮编码" min-width="180" />
                  <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                  <el-table-column prop="sort" label="排序" width="90" align="center" />
                  <el-table-column prop="note" label="说明" min-width="200" show-overflow-tooltip />
                  <el-table-column label="操作" width="180" fixed="right" align="center">
                    <template #default="scope">
                      <el-button link type="primary" @click="handleEditButton(scope.row)">编辑
                      </el-button>
                      <el-button link type="danger" @click="handleDeleteButton(scope.row)">删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </el-tab-pane>
            </el-tabs>
          </template>

          <div v-else class="workspace-empty">
            <el-empty description="请选择左侧模块节点查看详情" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <module-center-form v-model="formVisible" :mode="formMode" :initial-data="formInitial"
                        :tree-data="moduleTree"
                        :current-id="formMode === 'edit' ? formInitial.id : undefined"
                        @submit="handleFormSubmit" @cancel="handleFormCancel" />
    <module-component-form v-model="componentFormVisible" :mode="componentFormMode"
                           :initial-data="componentFormInitial" :title="componentFormTitle"
                           @submit="handleComponentFormSubmit"
                           @cancel="handleComponentFormCancel" />
    <module-field-form v-model="fieldFormVisible" :mode="fieldFormMode"
                       :initial-data="fieldFormInitial" :title="fieldFormTitle"
                       @submit="handleFieldFormSubmit" @cancel="handleFieldFormCancel" />
    <module-button-form v-model="buttonFormVisible" :mode="buttonFormMode"
                        :initial-data="buttonFormInitial" :title="buttonFormTitle"
                        @submit="handleButtonFormSubmit" @cancel="handleButtonFormCancel" />
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
import ModuleComponentForm from './components/ModuleComponentForm.vue'
import ModuleFieldForm, { type ModuleFieldFormModel } from './components/ModuleFieldForm.vue'
import {
  createModule,
  createModuleButton,
  createModuleComponent,
  createModuleField,
  deleteModule,
  deleteModuleButton,
  deleteModuleComponent,
  deleteModuleField,
  getModuleContextPreview,
  getModuleDetail,
  getModuleTree,
  moduleTypeLabelMap,
  updateModule,
  updateModuleButton,
  updateModuleComponent,
  updateModuleField,
  type ModuleButtonDefinition,
  type ModuleButtonPayload,
  type ModuleComponentDefinition,
  type ModuleComponentInfo,
  type ModuleDetail,
  type ModuleFieldDefinition,
  type ModuleFieldPayload,
  type ModuleFormModel,
  type ModuleStateDefinition,
  type ModuleStateTransition,
  type ModuleTreeNode,
  type ModuleType,
  type ModuleTypeCode,
  type ModuleValueTypeCode,
  type YesNoCode
} from '@/api/moduleCenter'
import { getEnumCode } from '@/utils/enum'

defineOptions({ name: 'SystemModuleCenter' })

interface ModuleNode extends Omit<ModuleTreeNode, 'moduleType' | 'navVisible' | 'statefulFlag' | 'status' | 'children'> {
  routePath: string;
  componentPath: string;
  icon: string;
  moduleType: ModuleTypeCode;
  navVisible: YesNoCode;
  statefulFlag: YesNoCode;
  stateFieldCode: string;
  contextVersion: number;
  status: YesNoCode;
  note: string;
  components: ModuleComponentDefinition[];
  buttons: ModuleButtonDefinition[];
  states: ModuleStateDefinition[];
  transitions: ModuleStateTransition[];
  children?: ModuleNode[];
}

const treeRef = ref()
const moduleKeyword = ref('')
const moduleTree = ref<ModuleNode[]>([])
const treeLoading = ref(false)
const workspaceLoading = ref(false)
const selectedModuleId = ref<number>()
const activePane = ref<'basic' | 'component' | 'button'>('basic')
const activeComponentCode = ref('')
const contextPreviewVisible = ref(false)
const contextPreviewJson = ref('')

const formVisible = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const formInitial = ref<Partial<ModuleFormModel>>({})

const componentFormVisible = ref(false)
const componentFormMode = ref<'create' | 'edit'>('create')
const componentFormInitial = ref<Partial<ModuleComponentInfo>>({})

const fieldFormVisible = ref(false)
const fieldFormMode = ref<'create' | 'edit'>('create')
const fieldFormInitial = ref<Partial<ModuleFieldFormModel>>({})
const fieldFormComponentCode = ref('')

const buttonFormVisible = ref(false)
const buttonFormMode = ref<'create' | 'edit'>('create')
const buttonFormInitial = ref<Partial<ModuleButtonDefinition>>({})

const selectedModule = computed(() => selectedModuleId.value === undefined ? undefined : findModuleNode(moduleTree.value, selectedModuleId.value))
const selectedFieldCount = computed(() => selectedModule.value?.components.reduce((sum, component) => sum + component.fields.length, 0) || 0)
const activeComponentDef = computed(() => selectedModule.value?.components.find((component) => component.componentInfo.componentCode === activeComponentCode.value))
const componentFormTitle = computed(() => componentFormMode.value === 'create' ? '新增组件' : '编辑组件')
const fieldFormTitle = computed(() => fieldFormMode.value === 'create' ? '新增字段' : '编辑字段')
const buttonFormTitle = computed(() => buttonFormMode.value === 'create' ? '新增按钮' : '编辑按钮')

function mapTreeNode(node: ModuleTreeNode): ModuleNode {
  return {
    ...node,
    buttons: [],
    componentPath: node.componentPath || '',
    components: [],
    contextVersion: 0,
    icon: node.icon || 'Document',
    moduleType: getEnumCode(node.moduleType) || 'PAGE',
    navVisible: getEnumCode(node.navVisible) || '1',
    note: '',
    routePath: node.routePath || '',
    stateFieldCode: '',
    statefulFlag: getEnumCode(node.statefulFlag) || '0',
    states: [],
    status: getEnumCode(node.status) || '0',
    transitions: [],
    children: (node.children || []).map(mapTreeNode)
  }
}

function findModuleNode(nodes: ModuleNode[], id?: number): ModuleNode | undefined {
  for (const node of nodes) {
    if (node.id === id) {
      return node
    }
    const matched = findModuleNode(node.children || [], id)
    if (matched) {
      return matched
    }
  }
  return undefined
}

function findFirstEditableModule(nodes: ModuleNode[]): ModuleNode | undefined {
  for (const node of nodes) {
    if (getEnumCode(node.moduleType) !== 'CATALOG') {
      return node
    }
    const matched = findFirstEditableModule(node.children || [])
    if (matched) {
      return matched
    }
  }
  return undefined
}

function sortBySort<T extends { sort: number }>(left: T, right: T) {
  return left.sort - right.sort
}

function getModuleTypeTagType(type: ModuleType | ModuleTypeCode) {
  return getEnumCode(type) === 'CATALOG' ? 'info' : 'warning'
}

function getModuleTypeLabel(type: ModuleType | ModuleTypeCode) {
  const code = getEnumCode(type) || 'PAGE'
  return moduleTypeLabelMap[code]
}

function normalizeModuleValueType(valueType?: ModuleValueTypeCode | string): ModuleValueTypeCode {
  if (valueType === 'integer') {
    return 'int'
  }
  return (valueType as ModuleValueTypeCode) || 'string'
}

function filterNode(value: string, data: ModuleNode) {
  return !value || data.moduleName.includes(value) || data.moduleCode.includes(value)
}

function hydrateModuleDetail(detail: ModuleDetail, preferredComponent?: string) {
  const currentNode = findModuleNode(moduleTree.value, detail.id)
  if (!currentNode) {
    return
  }
  Object.assign(currentNode, detail, {
    moduleType: getEnumCode(detail.moduleType) || 'PAGE',
    navVisible: getEnumCode(detail.navVisible) || '1',
    statefulFlag: getEnumCode(detail.statefulFlag) || '0',
    status: getEnumCode(detail.status) || '0'
  })
  activeComponentCode.value = detail.components.find((item) => item.componentInfo.componentCode === preferredComponent)?.componentInfo.componentCode || detail.components[0]?.componentInfo.componentCode || ''
}

async function loadModuleDetail(id: number, preferredComponent?: string) {
  workspaceLoading.value = true
  try {
    const { data } = await getModuleDetail(id)
    if (selectedModuleId.value === id) {
      hydrateModuleDetail(data, preferredComponent || activeComponentCode.value)
    }
  } finally {
    workspaceLoading.value = false
  }
}

async function handleModuleSelection(id?: number) {
  selectedModuleId.value = id
  if (id !== undefined) {
    treeRef.value?.setCurrentKey(id)
    await loadModuleDetail(id)
  } else {
    activeComponentCode.value = ''
  }
}

async function loadTree(preferId?: number) {
  treeLoading.value = true
  try {
    const { data } = await getModuleTree()
    moduleTree.value = data.map(mapTreeNode)
    const nextId = findModuleNode(moduleTree.value, preferId)?.id || findModuleNode(moduleTree.value, selectedModuleId.value)?.id || findFirstEditableModule(moduleTree.value)?.id || moduleTree.value[0]?.id
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
  if (!targetNode || getEnumCode(targetNode.moduleType) !== 'CATALOG') {
    ElMessage.warning('只有目录节点可以新增下级模块')
    return
  }
  formMode.value = 'create'
  formInitial.value = {
    componentPath: '',
    icon: 'Document',
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
  await loadModuleDetail(targetNode.id)
  formMode.value = 'edit'
  const currentNode = findModuleNode(moduleTree.value, targetNode.id)
  if (!currentNode) {
    return
  }
  formInitial.value = {
    id: currentNode.id,
    parentId: currentNode.parentId,
    moduleCode: currentNode.moduleCode,
    moduleName: currentNode.moduleName,
    moduleType: currentNode.moduleType,
    routePath: currentNode.routePath,
    componentPath: currentNode.componentPath,
    icon: currentNode.icon,
    sort: currentNode.sort,
    navVisible: currentNode.navVisible,
    statefulFlag: currentNode.statefulFlag,
    stateFieldCode: currentNode.stateFieldCode,
    status: currentNode.status,
    note: currentNode.note
  }
  formVisible.value = true
}

async function handleDeleteModule(node?: ModuleNode) {
  const targetNode = node || selectedModule.value
  if (!targetNode) {
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除模块「${targetNode.moduleName}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModule(targetNode.id)
  ElMessage.success('模块删除成功')
  await loadTree(targetNode.parentId > 0 ? targetNode.parentId : undefined)
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
  if (payload.id) {
    await updateModule({ ...requestPayload, id: payload.id })
    ElMessage.success('模块编辑成功')
    formVisible.value = false
    await loadTree(payload.id)
  }
}

function handleFormCancel() {
  formVisible.value = false
}

function handleCreateComponent() {
  if (!selectedModule.value) {
    return
  }
  componentFormMode.value = 'create'
  componentFormInitial.value = {
    componentCode: '',
    componentName: '',
    note: '',
    sort: selectedModule.value.components.length + 1
  }
  componentFormVisible.value = true
}

function handleEditComponent() {
  if (!activeComponentDef.value) {
    ElMessage.warning('请先选择需要编辑的组件')
    return
  }
  componentFormMode.value = 'edit'
  componentFormInitial.value = { ...activeComponentDef.value.componentInfo }
  componentFormVisible.value = true
}

async function handleDeleteComponent() {
  if (!activeComponentDef.value || !selectedModule.value) {
    ElMessage.warning('请先选择需要删除的组件')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除组件「${activeComponentDef.value.componentInfo.componentName}」吗？`, '删除提示', {
      cancelButtonText: '取消',
      confirmButtonText: '确定删除',
      type: 'warning'
    })
  } catch {
    return
  }
  await deleteModuleComponent(activeComponentDef.value.componentInfo.id)
  ElMessage.success('组件删除成功')
  await loadModuleDetail(selectedModule.value.id)
}

async function handleComponentFormSubmit(payload: Partial<ModuleComponentInfo>) {
  if (!selectedModule.value) {
    return
  }
  if (componentFormMode.value === 'create') {
    await createModuleComponent({
      moduleId: selectedModule.value.id,
      componentCode: payload.componentCode || '',
      componentName: payload.componentName || '',
      note: payload.note || '',
      sort: payload.sort || selectedModule.value.components.length + 1,
      status: '0'
    })
    ElMessage.success('组件新增成功')
  } else if (payload.id) {
    await updateModuleComponent({
      id: payload.id,
      moduleId: selectedModule.value.id,
      componentCode: payload.componentCode || '',
      componentName: payload.componentName || '',
      note: payload.note || '',
      sort: payload.sort || 1,
      status: '0'
    })
    ElMessage.success('组件编辑成功')
  }
  componentFormVisible.value = false
  await loadModuleDetail(selectedModule.value.id, payload.componentCode)
}

function handleComponentFormCancel() {
  componentFormVisible.value = false
}

function handleCreateField() {
  if (!activeComponentDef.value) {
    ElMessage.warning('请先选择组件，再新增字段')
    return
  }
  fieldFormMode.value = 'create'
  fieldFormComponentCode.value = activeComponentDef.value.componentInfo.componentCode
  fieldFormInitial.value = {
    componentType: 'input',
    dataPath: `${activeComponentDef.value.componentInfo.componentCode}.`,
    defaultTitle: '',
    fieldCode: '',
    note: '',
    requiredFlag: '0',
    sort: activeComponentDef.value.fields.length + 1,
    valueType: 'string'
  }
  fieldFormVisible.value = true
}

function handleEditField(componentCode: string, field: ModuleFieldDefinition) {
  fieldFormMode.value = 'edit'
  fieldFormComponentCode.value = componentCode
  fieldFormInitial.value = {
    id: field.id,
    componentType: getEnumCode(field.componentType) || 'input',
    dataPath: field.dataPath,
    defaultTitle: field.defaultTitle,
    fieldCode: field.fieldCode,
    note: field.note || '',
    requiredFlag: getEnumCode(field.requiredFlag) || '0',
    sort: field.sort,
    valueType: getEnumCode(field.valueType) || 'string'
  }
  fieldFormVisible.value = true
}

async function handleDeleteField(componentCode: string, field: ModuleFieldDefinition) {
  if (!selectedModule.value) {
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
  await loadModuleDetail(selectedModule.value.id, componentCode)
}

async function handleFieldFormSubmit(payload: Partial<ModuleFieldFormModel>) {
  const component = selectedModule.value?.components.find((item) => item.componentInfo.componentCode === fieldFormComponentCode.value)
  if (!selectedModule.value || !component) {
    return
  }
  const requestPayload: ModuleFieldPayload = {
    componentId: component.componentInfo.id,
    componentType: payload.componentType || 'input',
    dataPath: payload.dataPath || '',
    defaultTitle: payload.defaultTitle || '',
    fieldCode: payload.fieldCode || '',
    moduleId: selectedModule.value.id,
    note: payload.note || '',
    requiredFlag: payload.requiredFlag || '0',
    sort: payload.sort || component.fields.length + 1,
    status: '0',
    valueType: normalizeModuleValueType(payload.valueType)
  }
  if (fieldFormMode.value === 'create') {
    await createModuleField(requestPayload)
    ElMessage.success('字段新增成功')
  } else if (payload.id) {
    await updateModuleField({ ...requestPayload, id: payload.id })
    ElMessage.success('字段编辑成功')
  }
  fieldFormVisible.value = false
  await loadModuleDetail(selectedModule.value.id, fieldFormComponentCode.value)
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
  if (!selectedModule.value) {
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
  await loadModuleDetail(selectedModule.value.id, activeComponentCode.value)
}

async function handleButtonFormSubmit(payload: Partial<ModuleButtonDefinition>) {
  if (!selectedModule.value) {
    return
  }
  const requestPayload: ModuleButtonPayload = {
    buttonCode: payload.buttonCode || '',
    defaultTitle: payload.defaultTitle || '',
    moduleId: selectedModule.value.id,
    note: payload.note || '',
    sort: payload.sort || selectedModule.value.buttons.length + 1,
    status: '0'
  }
  if (buttonFormMode.value === 'create') {
    await createModuleButton(requestPayload)
    ElMessage.success('按钮新增成功')
  } else if (payload.id) {
    await updateModuleButton({ ...requestPayload, id: payload.id })
    ElMessage.success('按钮编辑成功')
  }
  buttonFormVisible.value = false
  await loadModuleDetail(selectedModule.value.id, activeComponentCode.value)
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

watch(selectedModule, (module) => {
  activeComponentCode.value = module?.components.find((item) => item.componentInfo.componentCode === activeComponentCode.value)?.componentInfo.componentCode || module?.components[0]?.componentInfo.componentCode || ''
}, { immediate: true })

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

.card-header, .overview-header, .tab-toolbar, .overview-actions, .tab-toolbar__actions, .sub-tab-summary {
  display: flex;
  gap: 8px
}

.card-header, .overview-header, .tab-toolbar {
  align-items: center;
  justify-content: space-between
}

.overview-actions, .tab-toolbar__actions, .sub-tab-summary {
  flex-wrap: wrap
}

.module-search, .tab-toolbar, .module-descriptions {
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

:deep(.module-tree-card .el-tree-node__content) {
  height: auto;
  align-items: flex-start;
  padding: 8px 0 8px 2px
}

:deep(.module-tree-card .el-tree-node__content:hover .tree-node__actions), :deep(.module-tree-card .is-current > .el-tree-node__content .tree-node__actions) {
  opacity: 1
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
  padding: 16px 18px;
  border-radius: 12px
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

:deep(.el-tab-pane) {
  min-width: 0;
  width: 100%;
}
</style>

