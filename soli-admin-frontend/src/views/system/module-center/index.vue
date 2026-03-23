<template>
  <div class="app-container module-center-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="6" class="module-tree-col">
        <el-card shadow="never" class="module-tree-card">
          <template #header>
            <div class="card-header">
              <span>模块树</span>
              <el-button link type="primary" @click="handleCreateRoot">新增模块</el-button>
            </div>
          </template>

          <el-input
            v-model="moduleKeyword"
            clearable
            placeholder="请输入模块名称或编码"
            prefix-icon="Search"
            class="module-search"
          />

          <div class="tree-toolbar">
            <el-button type="primary" plain icon="Plus" @click="handleCreateRoot">新增模块</el-button>
            <el-button plain icon="DocumentAdd" :disabled="!selectedModule" @click="handleCreateChild">新增子级</el-button>
            <el-button plain icon="Edit" :disabled="!selectedModule" @click="handleEditModule">编辑</el-button>
          </div>

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
                    <el-tag size="small" :type="getModuleTypeTagType(data.moduleType)" effect="plain">
                      {{ getModuleTypeLabel(data.moduleType) }}
                    </el-tag>
                  </div>
                  <div class="tree-node__code">{{ data.moduleCode }}</div>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>

      <el-col :span="18" class="module-workspace-col">
        <el-empty v-if="!selectedModule" description="请选择左侧模块查看详情" />

        <template v-else>
          <el-card shadow="never" class="module-workspace-card">
            <div class="workspace-overview">
              <div class="overview-header">
                <div>
                <div class="overview-title">
                  <span>{{ selectedModule.moduleName }}</span>
                  <el-tag size="small" :type="getModuleTypeTagType(selectedModule.moduleType)" effect="plain">
                    {{ getModuleTypeLabel(selectedModule.moduleType) }}
                  </el-tag>
                  <el-tag size="small" :type="selectedModule.status === '0' ? 'success' : 'danger'" effect="plain">
                    {{ selectedModule.status === '0' ? '启用' : '停用' }}
                  </el-tag>
                  <el-tag v-if="selectedModule.statefulFlag === '1'" size="small" type="warning" effect="plain">
                    状态型模块
                  </el-tag>
                </div>
                <div class="overview-subtitle">{{ selectedModule.note || '当前模块暂未填写说明。' }}</div>
              </div>

              <div class="overview-actions">
                <el-button icon="Edit" @click="handleEditModule">编辑模块</el-button>
                <el-button type="primary" icon="View" @click="openContextPreview">预览上下文</el-button>
              </div>
            </div>

            <el-row :gutter="12" class="metric-row">
              <el-col :span="6">
                <div class="metric-card">
                  <div class="metric-card__label">单头 Tab</div>
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
              <el-descriptions-item label="模块编码">{{ selectedModule.moduleCode }}</el-descriptions-item>
              <el-descriptions-item label="路由地址">{{ selectedModule.routePath || '-' }}</el-descriptions-item>
              <el-descriptions-item label="组件路径">{{ selectedModule.componentPath || '-' }}</el-descriptions-item>
              <el-descriptions-item label="导航可见">{{ selectedModule.navVisible === '1' ? '显示' : '隐藏' }}</el-descriptions-item>
              <el-descriptions-item label="状态字段">{{ selectedModule.stateFieldCode || '-' }}</el-descriptions-item>
              <el-descriptions-item label="上下文版本">v{{ selectedModule.contextVersion }}</el-descriptions-item>
            </el-descriptions>
            </div>

            <div class="workspace-main">
              <el-tabs v-model="activePane" class="workspace-tabs">
              <el-tab-pane label="基础信息" name="basic">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">模块基础信息</div>
                  <div class="tab-toolbar__actions">
                    <el-button icon="Edit" @click="handleEditModule">编辑基础信息</el-button>
                  </div>
                </div>

                <el-row :gutter="16">
                  <el-col :span="12">
                    <el-card shadow="never" class="inner-card">
                      <template #header>结构摘要</template>
                      <el-descriptions :column="1" size="large">
                        <el-descriptions-item label="模块类型">{{ getModuleTypeLabel(selectedModule.moduleType) }}</el-descriptions-item>
                        <el-descriptions-item label="状态型模块">{{ selectedModule.statefulFlag === '1' ? '是' : '否' }}</el-descriptions-item>
                        <el-descriptions-item label="默认图标">{{ selectedModule.icon || '-' }}</el-descriptions-item>
                        <el-descriptions-item label="排序">{{ selectedModule.sort }}</el-descriptions-item>
                      </el-descriptions>
                    </el-card>
                  </el-col>
                  <el-col :span="12">
                    <el-card shadow="never" class="inner-card">
                      <template #header>实施提示</template>
                      <el-alert type="info" :closable="false" show-icon title="当前模块中心原型仅接本地 mock，用于确认页面结构与交互方式。" />
                      <div class="tips-list">
                        <div>1. 左侧选择模块，右侧按基础信息、单头、明细、按钮四类工作区查看。</div>
                        <div>2. 当前页面已经按模块上下文思路组织字段和按钮元数据。</div>
                        <div>3. 确认方向后，下一步可以继续补字段、Tab、按钮的增删改。</div>
                      </div>
                    </el-card>
                  </el-col>
                </el-row>
              </el-tab-pane>

              <el-tab-pane label="单头设计" name="header">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">单头 Tab 与字段</div>
                    <div class="tab-toolbar__actions">
                    <el-button icon="Plus" @click="handleCreateTab('header')">新增 Tab</el-button>
                    <el-button icon="Edit" :disabled="!activeHeaderTabDef" @click="handleEditTab('header')">编辑当前 Tab</el-button>
                    <el-button type="danger" plain icon="Delete" :disabled="!activeHeaderTabDef" @click="handleDeleteTab('header')">删除当前 Tab</el-button>
                    <el-button type="primary" icon="Plus" :disabled="!activeHeaderTabDef" @click="handleCreateField('header')">新增字段</el-button>
                  </div>
                </div>

                <el-empty v-if="!selectedModule.headerTabs.length" description="当前模块还没有单头 Tab" />
                <template v-else>
                  <el-tabs v-model="activeHeaderTab" type="border-card" class="sub-tabs">
                    <el-tab-pane
                      v-for="tab in selectedModule.headerTabs"
                      :key="tab.tabCode"
                      :label="`${tab.tabName} (${tab.fields.length})`"
                      :name="tab.tabCode"
                    >
                      <div class="sub-tab-summary">
                        <div>Tab 编码：{{ tab.tabCode }}</div>
                        <div>排序：{{ tab.sort }}</div>
                        <div>说明：{{ tab.note || '-' }}</div>
                      </div>

                      <el-table :data="tab.fields.slice().sort(sortBySort)" border>
                        <el-table-column prop="fieldCode" label="字段编码" min-width="180" />
                        <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                        <el-table-column prop="componentType" label="组件类型" width="120" />
                        <el-table-column prop="dataPath" label="数据路径" min-width="220" show-overflow-tooltip />
                        <el-table-column prop="valueType" label="值类型" width="120" />
                        <el-table-column label="必填" align="center" width="90">
                          <template #default="scope">
                            <el-tag size="small" :type="scope.row.requiredFlag === '1' ? 'danger' : 'info'" effect="plain">
                              {{ scope.row.requiredFlag === '1' ? '是' : '否' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="sort" label="排序" width="80" align="center" />
                        <el-table-column label="操作" width="180" fixed="right" align="center">
                          <template #default="scope">
                            <el-button link type="primary" @click="handleEditField('header', tab.tabCode, scope.row)">编辑</el-button>
                            <el-button link type="danger" @click="handleDeleteField('header', tab.tabCode, scope.row)">删除</el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-tab-pane>
                  </el-tabs>
                </template>
              </el-tab-pane>

              <el-tab-pane label="明细设计" name="detail">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">明细 Tab 与字段</div>
                  <div class="tab-toolbar__actions">
                    <el-button icon="Plus" @click="handleCreateTab('detail')">新增 Tab</el-button>
                    <el-button icon="Edit" :disabled="!activeDetailTabDef" @click="handleEditTab('detail')">编辑当前 Tab</el-button>
                    <el-button type="danger" plain icon="Delete" :disabled="!activeDetailTabDef" @click="handleDeleteTab('detail')">删除当前 Tab</el-button>
                    <el-button type="primary" icon="Plus" :disabled="!activeDetailTabDef" @click="handleCreateField('detail')">新增字段</el-button>
                  </div>
                </div>

                <el-empty v-if="!selectedModule.detailTabs.length" description="当前模块还没有明细 Tab" />
                <template v-else>
                  <el-tabs v-model="activeDetailTab" type="border-card" class="sub-tabs">
                    <el-tab-pane
                      v-for="tab in selectedModule.detailTabs"
                      :key="tab.tabCode"
                      :label="`${tab.tabName} (${tab.fields.length})`"
                      :name="tab.tabCode"
                    >
                      <div class="sub-tab-summary">
                        <div>Tab 编码：{{ tab.tabCode }}</div>
                        <div>排序：{{ tab.sort }}</div>
                        <div>说明：{{ tab.note || '-' }}</div>
                      </div>

                      <el-table :data="tab.fields.slice().sort(sortBySort)" border>
                        <el-table-column prop="fieldCode" label="字段编码" min-width="180" />
                        <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                        <el-table-column prop="componentType" label="组件类型" width="120" />
                        <el-table-column prop="dataPath" label="数据路径" min-width="220" show-overflow-tooltip />
                        <el-table-column prop="valueType" label="值类型" width="120" />
                        <el-table-column label="必填" align="center" width="90">
                          <template #default="scope">
                            <el-tag size="small" :type="scope.row.requiredFlag === '1' ? 'danger' : 'info'" effect="plain">
                              {{ scope.row.requiredFlag === '1' ? '是' : '否' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="sort" label="排序" width="80" align="center" />
                        <el-table-column label="操作" width="180" fixed="right" align="center">
                          <template #default="scope">
                            <el-button link type="primary" @click="handleEditField('detail', tab.tabCode, scope.row)">编辑</el-button>
                            <el-button link type="danger" @click="handleDeleteField('detail', tab.tabCode, scope.row)">删除</el-button>
                          </template>
                        </el-table-column>
                      </el-table>
                    </el-tab-pane>
                  </el-tabs>
                </template>
              </el-tab-pane>

              <el-tab-pane label="按钮设计" name="button">
                <div class="tab-toolbar">
                  <div class="tab-toolbar__title">按钮分组定义</div>
                  <div class="tab-toolbar__actions">
                    <el-button type="primary" icon="Plus" @click="handleCreateButton">新增按钮</el-button>
                  </div>
                </div>

                <el-empty v-if="!selectedModule.buttons.length" description="当前模块还没有按钮定义" />
                <template v-else>
                  <div v-for="group in buttonGroups" :key="group.area" class="button-group-card">
                    <div class="button-group-card__header">
                      <span>{{ getButtonAreaLabel(group.area) }}</span>
                      <el-tag size="small" effect="plain">{{ group.buttons.length }} 个按钮</el-tag>
                    </div>
                    <el-table :data="group.buttons.slice().sort(sortBySort)" border>
                      <el-table-column prop="buttonCode" label="按钮编码" min-width="180" />
                      <el-table-column prop="defaultTitle" label="默认标题" min-width="140" />
                      <el-table-column label="所属区域" width="160">
                        <template #default="scope">
                          {{ getButtonAreaLabel(scope.row.area) }}
                        </template>
                      </el-table-column>
                      <el-table-column prop="sort" label="排序" width="90" align="center" />
                      <el-table-column prop="note" label="说明" min-width="200" show-overflow-tooltip />
                      <el-table-column label="操作" width="180" fixed="right" align="center">
                        <template #default="scope">
                          <el-button link type="primary" @click="handleEditButton(scope.row)">编辑</el-button>
                          <el-button link type="danger" @click="handleDeleteButton(scope.row)">删除</el-button>
                        </template>
                      </el-table-column>
                    </el-table>
                  </div>
                </template>
              </el-tab-pane>
            </el-tabs>
            </div>
          </el-card>
        </template>
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

    <el-drawer v-model="contextPreviewVisible" title="模块上下文预览" size="46%">
      <div class="preview-header">
        <el-tag type="primary" effect="plain">{{ selectedModule?.moduleName }}</el-tag>
        <el-tag v-if="selectedModule?.statefulFlag === '1'" type="warning" effect="plain">状态：未审核</el-tag>
        <el-tag type="info" effect="plain">岗位：采购员</el-tag>
      </div>
      <pre class="context-preview">{{ contextPreviewJson }}</pre>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import ModuleButtonForm from './components/ModuleButtonForm.vue';
import ModuleCenterForm from './components/ModuleCenterForm.vue';
import ModuleFieldForm from './components/ModuleFieldForm.vue';
import ModuleTabForm from './components/ModuleTabForm.vue';
import {
  buildModuleContextPreview,
  buttonAreaLabelMap,
  cloneModuleCenterTree,
  findFirstEditableModule,
  findModuleNode,
  type ModuleButtonArea,
  type ModuleButtonDefinition,
  type ModuleFieldDefinition,
  type ModuleNode,
  type ModuleTabDefinition
} from './moduleCenterMock';

defineOptions({
  name: 'SystemModuleCenter'
});

const moduleTypeLabelMap = {
  BILL: '单据模块',
  CATALOG: '目录',
  PAGE: '页面'
} as const;

const treeRef = ref();
const moduleKeyword = ref('');
const moduleTree = ref<ModuleNode[]>(cloneModuleCenterTree());
const selectedModuleId = ref<number>();
const activePane = ref<'basic' | 'header' | 'detail' | 'button'>('basic');
const activeHeaderTab = ref('');
const activeDetailTab = ref('');
const contextPreviewVisible = ref(false);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<ModuleNode>>({});
const tabFormVisible = ref(false);
const tabFormMode = ref<'create' | 'edit'>('create');
const tabFormInitial = ref<Partial<ModuleTabDefinition>>({});
const tabFormScope = ref<'header' | 'detail'>('header');
const fieldFormVisible = ref(false);
const fieldFormMode = ref<'create' | 'edit'>('create');
const fieldFormInitial = ref<Partial<ModuleFieldDefinition>>({});
const fieldFormScope = ref<'header' | 'detail'>('header');
const fieldFormTabCode = ref('');
const buttonFormVisible = ref(false);
const buttonFormMode = ref<'create' | 'edit'>('create');
const buttonFormInitial = ref<Partial<ModuleButtonDefinition>>({});

const selectedModule = computed(() => {
  return findModuleNode(moduleTree.value, selectedModuleId.value);
});

const selectedFieldCount = computed(() => {
  if (!selectedModule.value) {
    return 0;
  }
  const headerCount = selectedModule.value.headerTabs.reduce((sum, tab) => sum + tab.fields.length, 0);
  const detailCount = selectedModule.value.detailTabs.reduce((sum, tab) => sum + tab.fields.length, 0);
  return headerCount + detailCount;
});

const buttonGroups = computed(() => {
  if (!selectedModule.value) {
    return [] as Array<{ area: ModuleButtonArea; buttons: ModuleButtonDefinition[] }>;
  }
  return (Object.keys(buttonAreaLabelMap) as ModuleButtonArea[]).map((area) => ({
    area,
    buttons: selectedModule.value?.buttons.filter((button) => button.area === area) || []
  })).filter((group) => group.buttons.length > 0);
});

const activeHeaderTabDef = computed(() => {
  return selectedModule.value?.headerTabs.find((tab) => tab.tabCode === activeHeaderTab.value);
});

const activeDetailTabDef = computed(() => {
  return selectedModule.value?.detailTabs.find((tab) => tab.tabCode === activeDetailTab.value);
});

const tabFormTitle = computed(() => {
  const scopeLabel = tabFormScope.value === 'header' ? '单头' : '明细';
  return tabFormMode.value === 'create' ? `新增${scopeLabel} Tab` : `编辑${scopeLabel} Tab`;
});

const fieldFormTitle = computed(() => {
  const scopeLabel = fieldFormScope.value === 'header' ? '单头' : '明细';
  return fieldFormMode.value === 'create' ? `新增${scopeLabel}字段` : `编辑${scopeLabel}字段`;
});

const buttonFormTitle = computed(() => {
  return buttonFormMode.value === 'create' ? '新增按钮' : '编辑按钮';
});

const contextPreviewJson = computed(() => {
  if (!selectedModule.value) {
    return '';
  }
  return JSON.stringify(buildModuleContextPreview(selectedModule.value), null, 2);
});

const sortBySort = <T extends { sort: number }>(left: T, right: T) => {
  return left.sort - right.sort;
};

const getModuleTypeTagType = (type: ModuleNode['moduleType']) => {
  if (type === 'BILL') {
    return 'success';
  }
  if (type === 'PAGE') {
    return 'warning';
  }
  return 'info';
};

const getModuleTypeLabel = (type: ModuleNode['moduleType']) => {
  return moduleTypeLabelMap[type];
};

const getButtonAreaLabel = (area: ModuleButtonArea) => {
  return buttonAreaLabelMap[area];
};

const nextEntityId = () => {
  const collectMaxId = (nodes: ModuleNode[]): number => {
    return nodes.reduce((max, node) => {
      const headerMax = node.headerTabs.reduce((tabMax, tab) => {
        const fieldMax = tab.fields.reduce((fieldInnerMax, field) => Math.max(fieldInnerMax, field.id), 0);
        return Math.max(tabMax, tab.id, fieldMax);
      }, 0);
      const detailMax = node.detailTabs.reduce((tabMax, tab) => {
        const fieldMax = tab.fields.reduce((fieldInnerMax, field) => Math.max(fieldInnerMax, field.id), 0);
        return Math.max(tabMax, tab.id, fieldMax);
      }, 0);
      const buttonMax = node.buttons.reduce((buttonInnerMax, button) => Math.max(buttonInnerMax, button.id), 0);
      const childMax = node.children?.length ? collectMaxId(node.children) : 0;
      return Math.max(max, node.id, headerMax, detailMax, buttonMax, childMax);
    }, 0);
  };
  return collectMaxId(moduleTree.value) + 1;
};

const getTabList = (scope: 'header' | 'detail', module = selectedModule.value) => {
  if (!module) {
    return undefined;
  }
  return scope === 'header' ? module.headerTabs : module.detailTabs;
};

const getActiveTab = (scope: 'header' | 'detail', module = selectedModule.value) => {
  const list = getTabList(scope, module);
  if (!list) {
    return undefined;
  }
  const activeCode = scope === 'header' ? activeHeaderTab.value : activeDetailTab.value;
  return list.find((tab) => tab.tabCode === activeCode);
};

const touchModuleVersion = (module = selectedModule.value) => {
  if (!module) {
    return;
  }
  module.contextVersion += 1;
};

const filterNode = (value: string, data: ModuleNode) => {
  if (!value) {
    return true;
  }
  return data.moduleName.includes(value) || data.moduleCode.includes(value);
};

const selectModule = (id?: number) => {
  selectedModuleId.value = id;
  if (id !== undefined) {
    treeRef.value?.setCurrentKey(id);
  }
};

const handleNodeClick = (node: ModuleNode) => {
  selectModule(node.id);
};

const handleCreateRoot = () => {
  formMode.value = 'create';
  formInitial.value = {
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
  };
  formVisible.value = true;
};

const handleCreateChild = () => {
  if (!selectedModule.value) {
    return;
  }
  formMode.value = 'create';
  formInitial.value = {
    componentPath: '',
    icon: selectedModule.value.moduleType === 'CATALOG' ? 'Document' : selectedModule.value.icon,
    moduleCode: '',
    moduleName: '',
    moduleType: 'PAGE',
    navVisible: '1',
    note: '',
    parentId: selectedModule.value.id,
    routePath: '',
    sort: 1,
    stateFieldCode: '',
    statefulFlag: '0',
    status: '0'
  };
  formVisible.value = true;
};

const handleEditModule = () => {
  if (!selectedModule.value) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = { ...selectedModule.value };
  formVisible.value = true;
};

const findMaxId = (nodes: ModuleNode[]): number => {
  return nodes.reduce((max, node) => {
    const childMax = node.children?.length ? findMaxId(node.children) : node.id;
    return Math.max(max, node.id, childMax);
  }, 0);
};

const appendChildModule = (nodes: ModuleNode[], parentId: number, child: ModuleNode): boolean => {
  for (const node of nodes) {
    if (node.id === parentId) {
      node.children = node.children || [];
      node.children.push(child);
      node.children.sort(sortBySort);
      return true;
    }
    if (node.children?.length && appendChildModule(node.children, parentId, child)) {
      return true;
    }
  }
  return false;
};

const removeModuleNode = (nodes: ModuleNode[], id: number): ModuleNode | undefined => {
  for (let index = 0; index < nodes.length; index += 1) {
    const node = nodes[index]!;
    if (node.id === id) {
      return nodes.splice(index, 1)[0];
    }
    if (node.children?.length) {
      const removed = removeModuleNode(node.children, id);
      if (removed) {
        return removed;
      }
    }
  }
  return undefined;
};

const updateModuleNode = (nodes: ModuleNode[], payload: ModuleNode): boolean => {
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
    if (node.children?.length && updateModuleNode(node.children, payload)) {
      return true;
    }
  }
  return false;
};

const sortTreeNodes = (nodes: ModuleNode[]) => {
  nodes.sort(sortBySort);
  nodes.forEach((node) => {
    if (node.children?.length) {
      sortTreeNodes(node.children);
    }
  });
};

const handleFormSubmit = (payload: Partial<ModuleNode>) => {
  if (formMode.value === 'create') {
    const newId = findMaxId(moduleTree.value) + 1;
    const nextModule: ModuleNode = {
      buttons: [],
      children: [],
      contextVersion: 1,
      detailTabs: [],
      headerTabs: [],
      id: newId,
      moduleCode: payload.moduleCode || `module_${newId}`,
      moduleName: payload.moduleName || '未命名模块',
      moduleType: payload.moduleType || 'PAGE',
      navVisible: payload.navVisible || '1',
      note: payload.note || '',
      parentId: payload.parentId || 0,
      routePath: payload.routePath || '',
      componentPath: payload.componentPath || '',
      icon: payload.icon || 'Document',
      sort: payload.sort || 1,
      stateFieldCode: payload.stateFieldCode || '',
      statefulFlag: payload.statefulFlag || '0',
      status: payload.status || '0'
    };

    if (nextModule.parentId === 0) {
      moduleTree.value.push(nextModule);
      moduleTree.value.sort(sortBySort);
    } else {
      appendChildModule(moduleTree.value, nextModule.parentId, nextModule);
    }

    sortTreeNodes(moduleTree.value);
    selectModule(newId);
    ElMessage.success('模块已加入本地原型数据');
    return;
  }

  if (payload.id === undefined) {
    return;
  }

  const currentModule = findModuleNode(moduleTree.value, payload.id);
  if (!currentModule) {
    return;
  }

  const nextModule: ModuleNode = {
    ...currentModule,
    ...payload,
    children: currentModule.children || []
  };

  if (currentModule.parentId !== nextModule.parentId) {
    removeModuleNode(moduleTree.value, nextModule.id);
    if (nextModule.parentId === 0) {
      moduleTree.value.push(nextModule);
    } else {
      appendChildModule(moduleTree.value, nextModule.parentId, nextModule);
    }
  } else {
    updateModuleNode(moduleTree.value, nextModule);
  }

  sortTreeNodes(moduleTree.value);
  selectModule(payload.id);
  ElMessage.success('模块信息已更新');
};

const handleFormCancel = () => {
  formVisible.value = false;
};

const handleCreateTab = (scope: 'header' | 'detail') => {
  tabFormScope.value = scope;
  tabFormMode.value = 'create';
  tabFormInitial.value = {
    note: '',
    sort: (getTabList(scope)?.length || 0) + 1,
    tabCode: '',
    tabName: ''
  };
  tabFormVisible.value = true;
};

const handleEditTab = (scope: 'header' | 'detail') => {
  const currentTab = getActiveTab(scope);
  if (!currentTab) {
    ElMessage.warning('请先选择一个 Tab');
    return;
  }
  tabFormScope.value = scope;
  tabFormMode.value = 'edit';
  tabFormInitial.value = { ...currentTab };
  tabFormVisible.value = true;
};

const handleDeleteTab = async (scope: 'header' | 'detail') => {
  const module = selectedModule.value;
  const currentTab = getActiveTab(scope);
  if (!module || !currentTab) {
    ElMessage.warning('请先选择一个 Tab');
    return;
  }

  await ElMessageBox.confirm(`确认删除 Tab “${currentTab.tabName}” 吗？`, '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  const tabList = getTabList(scope, module);
  if (!tabList) {
    return;
  }
  const index = tabList.findIndex((tab) => tab.id === currentTab.id);
  if (index >= 0) {
    tabList.splice(index, 1);
  }
  if (scope === 'header') {
    activeHeaderTab.value = tabList[0]?.tabCode || '';
  } else {
    activeDetailTab.value = tabList[0]?.tabCode || '';
  }
  touchModuleVersion(module);
  ElMessage.success('Tab 已删除');
};

const handleTabFormSubmit = (payload: Partial<ModuleTabDefinition>) => {
  const module = selectedModule.value;
  const tabList = getTabList(tabFormScope.value, module);
  if (!module || !tabList) {
    return;
  }

  if (tabFormMode.value === 'create') {
    const nextTab: ModuleTabDefinition = {
      fields: [],
      id: nextEntityId(),
      note: payload.note || '',
      sort: payload.sort || tabList.length + 1,
      tabCode: payload.tabCode || `tab_${Date.now()}`,
      tabName: payload.tabName || '未命名 Tab'
    };
    tabList.push(nextTab);
    tabList.sort(sortBySort);
    if (tabFormScope.value === 'header') {
      activeHeaderTab.value = nextTab.tabCode;
    } else {
      activeDetailTab.value = nextTab.tabCode;
    }
    touchModuleVersion(module);
    ElMessage.success('Tab 已新增');
    return;
  }

  const currentTab = tabList.find((tab) => tab.id === payload.id);
  if (!currentTab) {
    return;
  }
  const previousCode = currentTab.tabCode;
  Object.assign(currentTab, payload);
  tabList.sort(sortBySort);
  if (tabFormScope.value === 'header' && activeHeaderTab.value === previousCode) {
    activeHeaderTab.value = currentTab.tabCode;
  }
  if (tabFormScope.value === 'detail' && activeDetailTab.value === previousCode) {
    activeDetailTab.value = currentTab.tabCode;
  }
  touchModuleVersion(module);
  ElMessage.success('Tab 已更新');
};

const handleTabFormCancel = () => {
  tabFormVisible.value = false;
};

const handleCreateField = (scope: 'header' | 'detail') => {
  const currentTab = getActiveTab(scope);
  if (!currentTab) {
    ElMessage.warning('请先新增并选中一个 Tab');
    return;
  }
  fieldFormScope.value = scope;
  fieldFormTabCode.value = currentTab.tabCode;
  fieldFormMode.value = 'create';
  fieldFormInitial.value = {
    componentType: 'input',
    dataPath: '',
    defaultTitle: '',
    fieldCode: '',
    note: '',
    requiredFlag: '0',
    sort: currentTab.fields.length + 1,
    valueType: 'string'
  };
  fieldFormVisible.value = true;
};

const handleEditField = (scope: 'header' | 'detail', tabCode: string, field: ModuleFieldDefinition) => {
  fieldFormScope.value = scope;
  fieldFormTabCode.value = tabCode;
  fieldFormMode.value = 'edit';
  fieldFormInitial.value = { ...field };
  fieldFormVisible.value = true;
};

const handleDeleteField = async (scope: 'header' | 'detail', tabCode: string, field: ModuleFieldDefinition) => {
  const module = selectedModule.value;
  const tabList = getTabList(scope, module);
  const currentTab = tabList?.find((tab) => tab.tabCode === tabCode);
  if (!module || !currentTab) {
    return;
  }

  await ElMessageBox.confirm(`确认删除字段 “${field.defaultTitle}” 吗？`, '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  const index = currentTab.fields.findIndex((item) => item.id === field.id);
  if (index >= 0) {
    currentTab.fields.splice(index, 1);
  }
  touchModuleVersion(module);
  ElMessage.success('字段已删除');
};

const handleFieldFormSubmit = (payload: Partial<ModuleFieldDefinition>) => {
  const module = selectedModule.value;
  const tabList = getTabList(fieldFormScope.value, module);
  const currentTab = tabList?.find((tab) => tab.tabCode === fieldFormTabCode.value);
  if (!module || !currentTab) {
    return;
  }

  if (fieldFormMode.value === 'create') {
    const nextField: ModuleFieldDefinition = {
      componentType: payload.componentType || 'input',
      dataPath: payload.dataPath || '',
      defaultTitle: payload.defaultTitle || '未命名字段',
      fieldCode: payload.fieldCode || `field_${Date.now()}`,
      id: nextEntityId(),
      note: payload.note || '',
      requiredFlag: payload.requiredFlag || '0',
      sort: payload.sort || currentTab.fields.length + 1,
      valueType: payload.valueType || 'string'
    };
    currentTab.fields.push(nextField);
    currentTab.fields.sort(sortBySort);
    touchModuleVersion(module);
    ElMessage.success('字段已新增');
    return;
  }

  const currentField = currentTab.fields.find((field) => field.id === payload.id);
  if (!currentField) {
    return;
  }
  Object.assign(currentField, payload);
  currentTab.fields.sort(sortBySort);
  touchModuleVersion(module);
  ElMessage.success('字段已更新');
};

const handleFieldFormCancel = () => {
  fieldFormVisible.value = false;
};

const handleCreateButton = () => {
  if (!selectedModule.value) {
    return;
  }
  buttonFormMode.value = 'create';
  buttonFormInitial.value = {
    area: 'HEADER_TOOLBAR',
    buttonCode: '',
    defaultTitle: '',
    note: '',
    sort: selectedModule.value.buttons.length + 1
  };
  buttonFormVisible.value = true;
};

const handleEditButton = (button: ModuleButtonDefinition) => {
  buttonFormMode.value = 'edit';
  buttonFormInitial.value = { ...button };
  buttonFormVisible.value = true;
};

const handleDeleteButton = async (button: ModuleButtonDefinition) => {
  const module = selectedModule.value;
  if (!module) {
    return;
  }

  await ElMessageBox.confirm(`确认删除按钮 “${button.defaultTitle}” 吗？`, '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  const index = module.buttons.findIndex((item) => item.id === button.id);
  if (index >= 0) {
    module.buttons.splice(index, 1);
  }
  touchModuleVersion(module);
  ElMessage.success('按钮已删除');
};

const handleButtonFormSubmit = (payload: Partial<ModuleButtonDefinition>) => {
  const module = selectedModule.value;
  if (!module) {
    return;
  }

  if (buttonFormMode.value === 'create') {
    const nextButton: ModuleButtonDefinition = {
      area: payload.area || 'HEADER_TOOLBAR',
      buttonCode: payload.buttonCode || `button_${Date.now()}`,
      defaultTitle: payload.defaultTitle || '未命名按钮',
      id: nextEntityId(),
      note: payload.note || '',
      sort: payload.sort || module.buttons.length + 1
    };
    module.buttons.push(nextButton);
    module.buttons.sort(sortBySort);
    touchModuleVersion(module);
    ElMessage.success('按钮已新增');
    return;
  }

  const currentButton = module.buttons.find((button) => button.id === payload.id);
  if (!currentButton) {
    return;
  }
  Object.assign(currentButton, payload);
  module.buttons.sort(sortBySort);
  touchModuleVersion(module);
  ElMessage.success('按钮已更新');
};

const handleButtonFormCancel = () => {
  buttonFormVisible.value = false;
};

const openContextPreview = () => {
  if (!selectedModule.value) {
    return;
  }
  contextPreviewVisible.value = true;
};

watch(moduleKeyword, (val) => {
  treeRef.value?.filter(val);
});

watch(selectedModule, (module) => {
  if (!module) {
    activeHeaderTab.value = '';
    activeDetailTab.value = '';
    return;
  }
  activeHeaderTab.value = module.headerTabs[0]?.tabCode || '';
  activeDetailTab.value = module.detailTabs[0]?.tabCode || '';
}, { immediate: true });

const firstModule = findFirstEditableModule(moduleTree.value) || moduleTree.value[0];
if (firstModule) {
  selectModule(firstModule.id);
}

onMounted(() => {
  nextTick(() => {
    if (selectedModuleId.value !== undefined) {
      treeRef.value?.setCurrentKey(selectedModuleId.value);
    }
  });
});
</script>

<style scoped>
.module-center-page {
  box-sizing: border-box;
  height: 100%;
  overflow: hidden;
}

:deep(.module-center-page > .el-row) {
  height: 100%;
}

.module-tree-col,
.module-workspace-col {
  display: flex;
  height: 100%;
  min-height: 0;
  flex-direction: column;
}

.module-tree-card,
.module-workspace-card {
  border-radius: 12px;
}

.module-tree-card,
.module-workspace-card {
  display: flex;
  height: 100%;
  min-height: 0;
  flex: 1;
  flex-direction: column;
}

:deep(.module-tree-card .el-card__body),
:deep(.module-workspace-card .el-card__body) {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

.card-header,
.overview-header,
.tab-toolbar,
.button-group-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.module-search {
  margin-bottom: 12px;
}

.tree-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.module-tree-scrollbar {
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
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.35;
}

.tree-node__meta {
  flex-shrink: 0;
}

:deep(.module-tree-card .el-tree-node__content) {
  height: auto;
  align-items: flex-start;
  padding: 8px 0 8px 2px;
}

:deep(.module-tree-card .el-tree-node__expand-icon) {
  margin-top: 10px;
}

:deep(.module-tree-card .el-tree-node) {
  min-width: 0;
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

.overview-actions {
  display: flex;
  gap: 8px;
}

.metric-row {
  margin: 18px 0 8px;
}

.metric-card,
.inner-card {
  height: 100%;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: linear-gradient(180deg, #fcfdff 0%, #f8fafc 100%);
}

.metric-card {
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

.module-descriptions {
  margin-top: 16px;
}

.module-workspace-col :deep(.el-empty) {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
}

.workspace-overview {
  display: flex;
  flex-shrink: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
  flex-direction: column;
}

.workspace-main {
  display: flex;
  min-height: 0;
  flex: 1;
  flex-direction: column;
  overflow: hidden;
  padding-top: 16px;
}

.tab-toolbar {
  margin-bottom: 16px;
  flex-shrink: 0;
}

.tab-toolbar__title {
  color: var(--el-text-color-primary);
  font-size: 15px;
  font-weight: 600;
}

.tab-toolbar__actions {
  display: flex;
  gap: 8px;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.7;
}

.sub-tabs {
  background: #fff;
  display: flex;
  min-height: 0;
  height: 100%;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

.sub-tab-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 14px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.button-group-card + .button-group-card {
  margin-top: 18px;
}

.button-group-card__header {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.preview-header {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
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
  word-break: break-word;
}

.workspace-tabs {
  display: flex;
  min-height: 0;
  height: 100%;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

:deep(.workspace-tabs > .el-tabs__header),
:deep(.sub-tabs > .el-tabs__header) {
  flex-shrink: 0;
}

:deep(.workspace-tabs > .el-tabs__content),
:deep(.sub-tabs > .el-tabs__content) {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
}

:deep(.workspace-tabs > .el-tabs__content > .el-tab-pane) {
  display: flex;
  min-height: 0;
  height: 100%;
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
  flex-direction: column;
}

:deep(.sub-tabs > .el-tabs__content > .el-tab-pane) {
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
}

@media (max-width: 1280px) {
  .overview-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .overview-actions {
    width: 100%;
  }

  .module-center-page {
    height: auto;
    min-height: calc(100vh - 84px);
  }

  :deep(.module-center-page > .el-row) {
    height: auto;
  }

  .module-tree-col,
  .module-workspace-col,
  .module-tree-card,
  .module-workspace-card {
    min-height: auto;
  }
}
</style>
