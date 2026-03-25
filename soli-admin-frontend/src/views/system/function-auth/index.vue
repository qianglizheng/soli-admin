<template>
  <div class="app-container function-auth-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="6" class="org-tree-col">
        <el-card shadow="never" class="org-tree-card">
          <template #header>
            <div class="card-header">
              <span>岗位树</span>
              <el-tag type="info" effect="plain">{{ postLeafCount }} 个岗位</el-tag>
            </div>
          </template>

          <el-input
            v-model="postKeyword"
            clearable
            placeholder="请输入岗位名称或编码"
            prefix-icon="Search"
            class="panel-search"
          />

          <el-scrollbar class="panel-scrollbar">
            <el-tree
              ref="postTreeRef"
              :data="orgPostTree"
              :expand-on-click-node="false"
              :filter-node-method="filterPostNode"
              :highlight-current="true"
              node-key="id"
              @node-click="handlePostNodeClick"
            >
              <template #default="{ data }">
                <div class="tree-node">
                  <div class="tree-node__head">
                    <span class="tree-node__label">{{ data.nodeName }}</span>
                    <el-tag size="small" :type="getOrgNodeTagType(data.nodeType)" effect="plain">
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

      <el-col :span="18" class="workspace-col">
        <el-empty v-if="!selectedPost" description="请选择左侧岗位后配置功能授权" />

        <template v-else>
          <el-card shadow="never" class="workspace-card">
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
                  <div class="overview-subtitle">
                    当前组织：{{ selectedPost.orgName }}，当前页用于配置岗位基础功能权限，状态权限中心会在此基础上继续收紧。
                  </div>
                </div>

                <div class="overview-actions">
                  <el-button icon="RefreshLeft" :disabled="!selectedModule" @click="handleResetCurrentModule">恢复当前模块</el-button>
                  <el-button type="primary" icon="DocumentChecked" @click="handleSave">保存</el-button>
                  <el-button type="primary" plain icon="View" :disabled="!selectedModule" @click="openPreviewDrawer">预览岗位基线</el-button>
                </div>
              </div>

              <el-row :gutter="12" class="metric-row">
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">可见模块</div>
                    <div class="metric-card__value">{{ visibleModuleCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">当前字段</div>
                    <div class="metric-card__value">{{ currentModuleFieldCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">可写字段</div>
                    <div class="metric-card__value">{{ currentWritableFieldCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">可用按钮</div>
                    <div class="metric-card__value">{{ currentEnabledButtonCount }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <div class="workspace-main">
              <div class="module-panel">
                <div class="panel-header">
                  <span>模块树</span>
                  <el-tag v-if="selectedModule" size="small" type="warning" effect="plain">{{ selectedModule.moduleCode }}</el-tag>
                </div>

                <el-input
                  v-model="moduleKeyword"
                  clearable
                  placeholder="请输入模块名称或编码"
                  prefix-icon="Search"
                  class="panel-search"
                />

                <el-scrollbar class="panel-scrollbar">
                  <el-tree
                    ref="moduleTreeRef"
                    :data="moduleTree"
                    :expand-on-click-node="false"
                    :filter-node-method="filterModuleNode"
                    :highlight-current="true"
                    node-key="id"
                    @node-click="handleModuleNodeClick"
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
              </div>

              <div class="editor-panel">
                <el-empty v-if="!selectedModule" description="请选择模块后配置权限" />

                <template v-else>
                  <div class="editor-header">
                    <div>
                      <div class="editor-title">
                        <span>{{ selectedModule.moduleName }}</span>
                        <el-tag size="small" :type="getModuleTypeTagType(selectedModule.moduleType)" effect="plain">
                          {{ getModuleTypeLabel(selectedModule.moduleType) }}
                        </el-tag>
                      </div>
                      <div class="editor-subtitle">
                        模块可见性、按钮权限、字段权限均在当前岗位下独立维护；状态权限由状态权限中心继续叠加。
                      </div>
                    </div>
                    <el-tag type="info" effect="plain">当前组织：{{ selectedPost.orgName }}</el-tag>
                  </div>

                  <el-tabs v-model="activePane" class="editor-tabs">
                    <el-tab-pane label="模块授权" name="module">
                      <div class="editor-pane">
                        <el-row :gutter="16">
                          <el-col :span="12">
                            <el-card shadow="never" class="inner-card">
                              <template #header>基础开关</template>
                              <el-form label-width="110px">
                                <el-form-item label="模块可见">
                                  <el-switch v-model="moduleVisibleModel" />
                                </el-form-item>
                                <el-form-item label="导航可见">
                                  <el-switch v-model="navVisibleModel" :disabled="!moduleVisibleModel" />
                                </el-form-item>
                                <el-form-item label="模块编码">
                                  <span>{{ selectedModule.moduleCode }}</span>
                                </el-form-item>
                                <el-form-item label="路由地址">
                                  <span>{{ selectedModule.routePath || '-' }}</span>
                                </el-form-item>
                              </el-form>
                            </el-card>
                          </el-col>
                          <el-col :span="12">
                            <el-card shadow="never" class="inner-card">
                              <template #header>权限说明</template>
                              <el-alert
                                title="当前页面配置的是岗位基线权限，不放大权限。状态权限中心只允许在岗位基线上继续收紧。"
                                type="info"
                                :closable="false"
                                show-icon
                              />
                              <div class="tips-list">
                                <div>1. 模块隐藏后，运行时模块入口将不可见。</div>
                                <div>2. 按钮权限支持不可见、禁用、可用三个级别。</div>
                                <div>3. 字段权限支持不可见、只读、可写三个级别。</div>
                              </div>
                            </el-card>
                          </el-col>
                        </el-row>
                      </div>
                    </el-tab-pane>

                    <el-tab-pane label="按钮权限" name="button" style="width: 100%">
                      <div class="editor-pane">
                        <el-empty v-if="!selectedModule.buttons.length" description="当前模块暂无按钮定义" />
                        <template v-else>
                          <div v-for="group in buttonGroups" :key="group.area" class="group-card">
                            <div class="group-card__header">
                              <span>{{ buttonAreaLabelMap[group.area] }}</span>
                              <el-tag size="small" effect="plain">{{ group.buttons.length }} 个按钮</el-tag>
                            </div>
                            <el-table :data="group.buttons" border>
                              <el-table-column prop="buttonCode" label="按钮编码" min-width="160" />
                              <el-table-column prop="defaultTitle" label="按钮标题" min-width="140" />
                              <el-table-column label="权限级别" width="180">
                                <template #default="scope">
                                  <el-select
                                    :model-value="getButtonPermission(scope.row.buttonCode)"
                                    style="width: 100%"
                                    @update:model-value="handleButtonPermissionChange(scope.row.buttonCode, $event as ButtonPermissionLevel)"
                                  >
                                    <el-option
                                      v-for="item in buttonPermissionLevelOptions"
                                      :key="item.value"
                                      :label="item.label"
                                      :value="item.value"
                                    />
                                  </el-select>
                                </template>
                              </el-table-column>
                              <el-table-column label="效果预览" width="120" align="center">
                                <template #default="scope">
                                  <el-tag :type="buttonPermissionTagTypeMap[getButtonPermission(scope.row.buttonCode)]" effect="plain">
                                    {{ buttonPermissionLabelMap[getButtonPermission(scope.row.buttonCode)] }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column prop="note" label="说明" min-width="180" show-overflow-tooltip />
                            </el-table>
                          </div>
                        </template>
                      </div>
                    </el-tab-pane>

                    <el-tab-pane label="字段权限" name="field">
                      <div class="editor-pane">
                        <el-tabs v-model="activeFieldScope" class="field-scope-tabs">
                          <el-tab-pane label="表头字段" name="header">
                            <el-empty v-if="!selectedModule.headerTabs.length" description="当前模块暂无表头字段" />
                            <template v-else>
                              <el-tabs v-model="activeHeaderTab" type="border-card" class="field-tabs">
                                <el-tab-pane
                                  v-for="tab in headerTabs"
                                  :key="tab.tabInfo.tabCode"
                                  :label="`${tab.tabInfo.tabName} (${tab.fields.length})`"
                                  :name="tab.tabInfo.tabCode"
                                >
                                  <div class="sub-tab-summary">
                                    <div>Tab 编码：{{ tab.tabInfo.tabCode }}</div>
                                    <div>排序：{{ tab.tabInfo.sort }}</div>
                                    <div>说明：{{ tab.tabInfo.note || '-' }}</div>
                                  </div>
                                  <el-table :data="tab.fields.slice().sort(sortBySort)" border>
                                    <el-table-column prop="fieldCode" label="字段编码" min-width="160" />
                                    <el-table-column prop="defaultTitle" label="字段标题" min-width="140" />
                                    <el-table-column prop="dataPath" label="数据路径" min-width="220" show-overflow-tooltip />
                                    <el-table-column label="权限级别" width="180">
                                      <template #default="scope">
                                        <el-select
                                          :model-value="getFieldPermission(scope.row.fieldCode)"
                                          style="width: 100%"
                                          @update:model-value="handleFieldPermissionChange(scope.row.fieldCode, $event as FieldPermissionLevel)"
                                        >
                                          <el-option
                                            v-for="item in fieldPermissionLevelOptions"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value"
                                          />
                                        </el-select>
                                      </template>
                                    </el-table-column>
                                    <el-table-column label="效果预览" width="120" align="center">
                                      <template #default="scope">
                                        <el-tag :type="fieldPermissionTagTypeMap[getFieldPermission(scope.row.fieldCode)]" effect="plain">
                                          {{ fieldPermissionLabelMap[getFieldPermission(scope.row.fieldCode)] }}
                                        </el-tag>
                                      </template>
                                    </el-table-column>
                                  </el-table>
                                </el-tab-pane>
                              </el-tabs>
                            </template>
                          </el-tab-pane>

                          <el-tab-pane label="明细字段" name="detail">
                            <el-empty v-if="!selectedModule.detailTabs.length" description="当前模块暂无明细字段" />
                            <template v-else>
                              <el-tabs v-model="activeDetailTab" type="border-card" class="field-tabs">
                                <el-tab-pane
                                  v-for="tab in detailTabs"
                                  :key="tab.tabInfo.tabCode"
                                  :label="`${tab.tabInfo.tabName} (${tab.fields.length})`"
                                  :name="tab.tabInfo.tabCode"
                                >
                                  <div class="sub-tab-summary">
                                    <div>Tab 编码：{{ tab.tabInfo.tabCode }}</div>
                                    <div>排序：{{ tab.tabInfo.sort }}</div>
                                    <div>说明：{{ tab.tabInfo.note || '-' }}</div>
                                  </div>
                                  <el-table :data="tab.fields.slice().sort(sortBySort)" border>
                                    <el-table-column prop="fieldCode" label="字段编码" min-width="160" />
                                    <el-table-column prop="defaultTitle" label="字段标题" min-width="140" />
                                    <el-table-column prop="dataPath" label="数据路径" min-width="220" show-overflow-tooltip />
                                    <el-table-column label="权限级别" width="180">
                                      <template #default="scope">
                                        <el-select
                                          :model-value="getFieldPermission(scope.row.fieldCode)"
                                          style="width: 100%"
                                          @update:model-value="handleFieldPermissionChange(scope.row.fieldCode, $event as FieldPermissionLevel)"
                                        >
                                          <el-option
                                            v-for="item in fieldPermissionLevelOptions"
                                            :key="item.value"
                                            :label="item.label"
                                            :value="item.value"
                                          />
                                        </el-select>
                                      </template>
                                    </el-table-column>
                                    <el-table-column label="效果预览" width="120" align="center">
                                      <template #default="scope">
                                        <el-tag :type="fieldPermissionTagTypeMap[getFieldPermission(scope.row.fieldCode)]" effect="plain">
                                          {{ fieldPermissionLabelMap[getFieldPermission(scope.row.fieldCode)] }}
                                        </el-tag>
                                      </template>
                                    </el-table-column>
                                  </el-table>
                                </el-tab-pane>
                              </el-tabs>
                            </template>
                          </el-tab-pane>
                        </el-tabs>
                      </div>
                    </el-tab-pane>
                  </el-tabs>
                </template>
              </div>
            </div>
          </el-card>
        </template>
      </el-col>
    </el-row>

    <el-drawer v-model="previewVisible" title="岗位模块基线预览" size="46%">
      <div class="preview-header">
        <el-tag type="primary" effect="plain">{{ selectedPost?.nodeName }}</el-tag>
        <el-tag type="info" effect="plain">{{ selectedModule?.moduleName }}</el-tag>
        <el-tag type="success" effect="plain">岗位基线</el-tag>
      </div>
      <pre class="context-preview">{{ previewJson }}</pre>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import {
  buttonAreaLabelMap,
  moduleTypeLabelMap,
  type ModuleButtonArea,
  type ModuleButtonDefinition,
  type ModuleDetail,
  type ModuleFieldDefinition,
  type ModuleTabDefinition,
  type ModuleTreeNode,
  type ModuleType,
  type YesNo
} from '@/api/moduleCenter';
import {
  getFunctionAuthDetail,
  getFunctionAuthModuleTree,
  getFunctionAuthPostTree,
  saveFunctionAuth,
  type FunctionAuthConfig,
  type FunctionAuthPageDetail
} from '@/api/functionAuth';
import {
  getOrgNodeTypeLabel,
  getPostTypeLabel,
  type OrgNodeType,
  type OrgPostTreeNode
} from '@/api/orgPost';

defineOptions({
  name: 'SystemFunctionAuth'
});

type FieldPermissionLevel = 0 | 1 | 2;
type ButtonPermissionLevel = 0 | 1 | 2;

interface ModulePermissionConfig {
  moduleVisible: boolean;
  navVisible: boolean;
  fieldPermissions: Record<string, FieldPermissionLevel>;
  buttonPermissions: Record<string, ButtonPermissionLevel>;
}

interface SelectedPostSummary {
  id: number;
  nodeName: string;
  status: YesNo;
  orgName?: string;
  orgTypeLabel?: string;
  postTypeLabel?: string;
}

const fieldPermissionLevelOptions = [
  { label: '不可见', value: 0 },
  { label: '只读', value: 1 },
  { label: '可写', value: 2 }
] as const;

const buttonPermissionLevelOptions = [
  { label: '不可见', value: 0 },
  { label: '禁用', value: 1 },
  { label: '可用', value: 2 }
] as const;

const fieldPermissionTagTypeMap: Record<FieldPermissionLevel, '' | 'warning' | 'success'> = {
  0: '',
  1: 'warning',
  2: 'success'
};

const buttonPermissionTagTypeMap: Record<ButtonPermissionLevel, '' | 'warning' | 'success'> = {
  0: '',
  1: 'warning',
  2: 'success'
};

const fieldPermissionLabelMap: Record<FieldPermissionLevel, string> = {
  0: '不可见',
  1: '只读',
  2: '可写'
};

const buttonPermissionLabelMap: Record<ButtonPermissionLevel, string> = {
  0: '不可见',
  1: '禁用',
  2: '可用'
};

const postTreeRef = ref();
const moduleTreeRef = ref();
const postKeyword = ref('');
const moduleKeyword = ref('');
const orgPostTree = ref<OrgPostTreeNode[]>([]);
const moduleTree = ref<ModuleTreeNode[]>([]);
const pageDetail = ref<FunctionAuthPageDetail>();
const savedPermissionConfig = ref<ModulePermissionConfig>();
const permissionConfig = ref<ModulePermissionConfig>();
const selectedPostId = ref<number>();
const selectedModuleId = ref<number>();
const activePane = ref<'module' | 'button' | 'field'>('module');
const activeFieldScope = ref<'header' | 'detail'>('header');
const activeHeaderTab = ref('');
const activeDetailTab = ref('');
const previewVisible = ref(false);

const sortBySort = <T extends { sort: number }>(left: T, right: T) => left.sort - right.sort;
const sortTabsByInfo = (left: ModuleTabDefinition, right: ModuleTabDefinition) => left.tabInfo.sort - right.tabInfo.sort;

const selectedPost = computed<SelectedPostSummary | undefined>(() => {
  const detail = pageDetail.value?.postDetail;
  if (!detail || detail.id !== selectedPostId.value) {
    return undefined;
  }
  return {
    id: detail.id,
    nodeName: detail.postName,
    orgName: detail.orgName,
    orgTypeLabel: getOrgNodeTypeLabel(detail.orgType),
    postTypeLabel: getPostTypeLabel(detail.postType),
    status: (detail.status || '0') as YesNo
  };
});

const selectedModule = computed<ModuleDetail | undefined>(() => {
  const detail = pageDetail.value?.moduleDetail;
  if (!detail || detail.id !== selectedModuleId.value) {
    return undefined;
  }
  return detail;
});

const currentModuleConfig = computed<ModulePermissionConfig | undefined>(() => permissionConfig.value);

const headerTabs = computed(() => {
  return selectedModule.value?.headerTabs.slice().sort(sortTabsByInfo) || [];
});

const detailTabs = computed(() => {
  return selectedModule.value?.detailTabs.slice().sort(sortTabsByInfo) || [];
});

const buttonGroups = computed(() => {
  return selectedModule.value ? groupModuleButtons(selectedModule.value.buttons) : [];
});

const postLeafCount = computed(() => countPostLeaves(orgPostTree.value));

const visibleModuleCount = computed(() => pageDetail.value?.visibleModuleCount || 0);

const currentModuleFieldCount = computed(() => {
  if (!selectedModule.value) {
    return 0;
  }
  const headerCount = selectedModule.value.headerTabs.reduce((sum, tab) => sum + tab.fields.length, 0);
  const detailCount = selectedModule.value.detailTabs.reduce((sum, tab) => sum + tab.fields.length, 0);
  return headerCount + detailCount;
});

const currentWritableFieldCount = computed(() => {
  if (!currentModuleConfig.value) {
    return 0;
  }
  return Object.values(currentModuleConfig.value.fieldPermissions).filter((level) => level === 2).length;
});

const currentEnabledButtonCount = computed(() => {
  if (!currentModuleConfig.value) {
    return 0;
  }
  return Object.values(currentModuleConfig.value.buttonPermissions).filter((level) => level === 2).length;
});

const moduleVisibleModel = computed({
  get: () => currentModuleConfig.value?.moduleVisible ?? false,
  set: (value: boolean) => {
    if (!currentModuleConfig.value) {
      return;
    }
    currentModuleConfig.value.moduleVisible = value;
    if (!value) {
      currentModuleConfig.value.navVisible = false;
    }
  }
});

const navVisibleModel = computed({
  get: () => currentModuleConfig.value?.navVisible ?? false,
  set: (value: boolean) => {
    if (!currentModuleConfig.value) {
      return;
    }
    currentModuleConfig.value.navVisible = currentModuleConfig.value.moduleVisible ? value : false;
  }
});

const previewJson = computed(() => {
  if (!selectedPost.value || !selectedModule.value || !currentModuleConfig.value) {
    return '';
  }
  return JSON.stringify(buildFunctionAuthPreview(selectedPost.value, selectedModule.value, currentModuleConfig.value), null, 2);
});

const getModuleTypeLabel = (moduleType: ModuleType) => moduleTypeLabelMap[moduleType];

const getModuleTypeTagType = (moduleType: ModuleType) => {
  if (moduleType === 'CATALOG') {
    return 'info';
  }
  if (moduleType === 'BILL') {
    return 'warning';
  }
  return 'success';
};

const getOrgNodeTagType = (nodeType: OrgNodeType) => {
  if (nodeType === 'GROUP') {
    return 'info';
  }
  if (nodeType === 'POST') {
    return 'warning';
  }
  return 'success';
};

const filterPostNode = (value: string, data: OrgPostTreeNode) => {
  if (!value) {
    return true;
  }
  return data.nodeName.includes(value) || data.nodeCode.includes(value);
};

const filterModuleNode = (value: string, data: ModuleTreeNode) => {
  if (!value) {
    return true;
  }
  return data.moduleName.includes(value) || data.moduleCode.includes(value);
};

const selectPost = (id?: number) => {
  selectedPostId.value = id;
  if (id !== undefined) {
    postTreeRef.value?.setCurrentKey(id);
  }
};

const selectModule = (id?: number) => {
  selectedModuleId.value = id;
  if (id !== undefined) {
    moduleTreeRef.value?.setCurrentKey(id);
  }
};

const handlePostNodeClick = (node: OrgPostTreeNode) => {
  const targetNode = node.nodeType === 'POST' ? node : findFirstPostLeafInNode(node);
  if (targetNode) {
    selectPost(targetNode.id);
  }
};

const handleModuleNodeClick = (node: ModuleTreeNode) => {
  const targetNode = node.moduleType === 'CATALOG' ? findFirstModuleLeafInNode(node) : node;
  if (targetNode) {
    selectModule(targetNode.id);
  }
};

const getFieldPermission = (fieldCode: string): FieldPermissionLevel => {
  return currentModuleConfig.value?.fieldPermissions[fieldCode] ?? 0;
};

const getButtonPermission = (buttonCode: string): ButtonPermissionLevel => {
  return currentModuleConfig.value?.buttonPermissions[buttonCode] ?? 0;
};

const handleFieldPermissionChange = (fieldCode: string, level: FieldPermissionLevel) => {
  if (!currentModuleConfig.value) {
    return;
  }
  currentModuleConfig.value.fieldPermissions[fieldCode] = level;
};

const handleButtonPermissionChange = (buttonCode: string, level: ButtonPermissionLevel) => {
  if (!currentModuleConfig.value) {
    return;
  }
  currentModuleConfig.value.buttonPermissions[buttonCode] = level;
};

const handleResetCurrentModule = () => {
  if (!savedPermissionConfig.value) {
    return;
  }
  permissionConfig.value = deepClone(savedPermissionConfig.value);
  ElMessage.success('当前模块权限已恢复到最近一次保存结果');
};

async function handleSave() {
  if (!selectedPostId.value || !selectedModule.value || !currentModuleConfig.value) {
    return;
  }
  await saveFunctionAuth(buildFunctionAuthPayload(selectedPostId.value, selectedModule.value, currentModuleConfig.value));
  await loadPageDetail(selectedPostId.value, selectedModule.value.id);
  ElMessage.success('功能授权已保存');
}

const openPreviewDrawer = () => {
  if (!selectedPost.value || !selectedModule.value) {
    return;
  }
  previewVisible.value = true;
};

watch(postKeyword, (value) => {
  postTreeRef.value?.filter(value);
});

watch(moduleKeyword, (value) => {
  moduleTreeRef.value?.filter(value);
});

watch(selectedModule, (module) => {
  activeHeaderTab.value = module?.headerTabs[0]?.tabInfo.tabCode || '';
  activeDetailTab.value = module?.detailTabs[0]?.tabInfo.tabCode || '';
}, { immediate: true });

watch([selectedPostId, selectedModuleId], async ([postId, moduleId]) => {
  if (!postId || !moduleId) {
    return;
  }
  await loadPageDetail(postId, moduleId);
});

onMounted(async () => {
  await loadInitialData();
  nextTick(() => {
    if (selectedPostId.value !== undefined) {
      postTreeRef.value?.setCurrentKey(selectedPostId.value);
    }
    if (selectedModuleId.value !== undefined) {
      moduleTreeRef.value?.setCurrentKey(selectedModuleId.value);
    }
  });
});

async function loadInitialData() {
  const [{ data: postTreeData }, { data: moduleTreeData }] = await Promise.all([
    getFunctionAuthPostTree(),
    getFunctionAuthModuleTree()
  ]);
  orgPostTree.value = postTreeData;
  moduleTree.value = moduleTreeData;

  const firstPost = findFirstPostLeaf(orgPostTree.value);
  const firstModule = findFirstModuleLeaf(moduleTree.value);
  if (firstPost) {
    selectedPostId.value = firstPost.id;
  }
  if (firstModule) {
    selectedModuleId.value = firstModule.id;
  }
}

async function loadPageDetail(orgPostId: number, moduleId: number) {
  const { data } = await getFunctionAuthDetail(orgPostId, moduleId);
  pageDetail.value = data;
  permissionConfig.value = mapFunctionAuthConfig(data.config, data.moduleDetail);
  savedPermissionConfig.value = deepClone(permissionConfig.value);
}

function mapFunctionAuthConfig(config: FunctionAuthConfig, moduleDetail: ModuleDetail): ModulePermissionConfig {
  const fieldCodeMap = flattenFields(moduleDetail).reduce<Record<number, string>>((result, field) => {
    result[field.id] = field.fieldCode;
    return result;
  }, {});
  const buttonCodeMap = (moduleDetail.buttons || []).reduce<Record<number, string>>((result, button) => {
    result[button.id] = button.buttonCode;
    return result;
  }, {});

  const fieldPermissions = (config.fieldPermissions || []).reduce<Record<string, FieldPermissionLevel>>((result, item) => {
    const fieldCode = fieldCodeMap[item.fieldId];
    if (fieldCode) {
      result[fieldCode] = normalizePermissionLevel(item.permissionLevel);
    }
    return result;
  }, {});
  flattenFields(moduleDetail).forEach((field) => {
    if (fieldPermissions[field.fieldCode] === undefined) {
      fieldPermissions[field.fieldCode] = 0;
    }
  });

  const buttonPermissions = (config.buttonPermissions || []).reduce<Record<string, ButtonPermissionLevel>>((result, item) => {
    const buttonCode = buttonCodeMap[item.buttonId];
    if (buttonCode) {
      result[buttonCode] = normalizePermissionLevel(item.permissionLevel);
    }
    return result;
  }, {});
  (moduleDetail.buttons || []).forEach((button) => {
    if (buttonPermissions[button.buttonCode] === undefined) {
      buttonPermissions[button.buttonCode] = 0;
    }
  });

  return {
    buttonPermissions,
    fieldPermissions,
    moduleVisible: !!config.moduleVisible,
    navVisible: !!config.navVisible
  };
}

function buildFunctionAuthPayload(
  orgPostId: number,
  moduleDetail: ModuleDetail,
  config: ModulePermissionConfig
): FunctionAuthConfig {
  return {
    buttonPermissions: (moduleDetail.buttons || []).map((button) => ({
      buttonId: button.id,
      permissionLevel: config.buttonPermissions[button.buttonCode] ?? 0
    })),
    fieldPermissions: flattenFields(moduleDetail).map((field) => ({
      fieldId: field.id,
      permissionLevel: config.fieldPermissions[field.fieldCode] ?? 0
    })),
    moduleId: moduleDetail.id,
    moduleVisible: config.moduleVisible,
    navVisible: config.moduleVisible ? config.navVisible : false,
    orgPostId
  };
}

function buildFunctionAuthPreview(
  post: SelectedPostSummary,
  moduleDetail: ModuleDetail,
  config: ModulePermissionConfig
) {
  const fields = flattenFields(moduleDetail).reduce<Record<string, { label: string; visible: boolean; editable: boolean }>>((result, field) => {
    const level = config.fieldPermissions[field.fieldCode] ?? 0;
    result[field.fieldCode] = {
      editable: level === 2,
      label: field.defaultTitle,
      visible: level > 0
    };
    return result;
  }, {});

  const buttons = (moduleDetail.buttons || []).reduce<Record<string, { label: string; visible: boolean; disabled: boolean }>>((result, button) => {
    const level = config.buttonPermissions[button.buttonCode] ?? 0;
    result[button.buttonCode] = {
      disabled: level === 1,
      label: button.defaultTitle,
      visible: level > 0
    };
    return result;
  }, {});

  return {
    currentOrgPost: {
      orgName: post.orgName,
      orgPostId: post.id,
      orgPostName: post.nodeName
    },
    moduleCode: moduleDetail.moduleCode,
    moduleName: moduleDetail.moduleName,
    permissions: {
      buttons,
      fields,
      module: {
        navVisible: config.moduleVisible ? config.navVisible : false,
        visible: config.moduleVisible
      }
    }
  };
}

function groupModuleButtons(buttons: ModuleButtonDefinition[]) {
  const groupedMap = buttons.reduce<Record<ModuleButtonArea, ModuleButtonDefinition[]>>((result, button) => {
    if (!result[button.area]) {
      result[button.area] = [];
    }
    result[button.area].push(button);
    return result;
  }, {
    DETAIL_ROW_BUTTON: [],
    HEADER_TOOLBAR: [],
    LIST_ROW_BUTTON: [],
    LIST_TOOLBAR: []
  });
  return Object.entries(groupedMap)
    .map(([area, groupButtonsList]) => ({
      area: area as ModuleButtonArea,
      buttons: groupButtonsList.slice().sort(sortBySort)
    }))
    .filter((item) => item.buttons.length > 0);
}

function flattenFields(moduleDetail: ModuleDetail) {
  return [...moduleDetail.headerTabs, ...moduleDetail.detailTabs].flatMap((tab) => tab.fields);
}

function countPostLeaves(nodes: OrgPostTreeNode[]): number {
  return nodes.reduce((sum, node) => {
    if (node.nodeType === 'POST') {
      return sum + 1;
    }
    return sum + (node.children?.length ? countPostLeaves(node.children) : 0);
  }, 0);
}

function findFirstPostLeaf(nodes: OrgPostTreeNode[]): OrgPostTreeNode | undefined {
  for (const node of nodes) {
    if (node.nodeType === 'POST') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstPostLeaf(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
}

function findFirstPostLeafInNode(node: OrgPostTreeNode): OrgPostTreeNode | undefined {
  if (node.nodeType === 'POST') {
    return node;
  }
  return node.children?.length ? findFirstPostLeaf(node.children) : undefined;
}

function findFirstModuleLeaf(nodes: ModuleTreeNode[]): ModuleTreeNode | undefined {
  for (const node of nodes) {
    const matched = findFirstModuleLeafInNode(node);
    if (matched) {
      return matched;
    }
  }
  return undefined;
}

function findFirstModuleLeafInNode(node: ModuleTreeNode): ModuleTreeNode | undefined {
  if (node.moduleType !== 'CATALOG') {
    return node;
  }
  if (!node.children?.length) {
    return undefined;
  }
  for (const child of node.children) {
    const matched = findFirstModuleLeafInNode(child);
    if (matched) {
      return matched;
    }
  }
  return undefined;
}

function normalizePermissionLevel(level?: number): FieldPermissionLevel {
  if (level === 1 || level === 2) {
    return level;
  }
  return 0;
}

function deepClone<T>(value: T): T {
  return JSON.parse(JSON.stringify(value)) as T;
}
</script>

<style scoped>
.function-auth-page {
  box-sizing: border-box;
  height: 100%;
  overflow: hidden;
}

:deep(.function-auth-page > .el-row) {
  height: 100%;
}

.org-tree-col,
.workspace-col {
  display: flex;
  height: 100%;
  min-height: 0;
  flex-direction: column;
}

.org-tree-card,
.workspace-card {
  display: flex;
  height: 100%;
  min-height: 0;
  flex: 1;
  border-radius: 12px;
  flex-direction: column;
}

:deep(.org-tree-card .el-card__body),
:deep(.workspace-card .el-card__body) {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

.card-header,
.overview-header,
.panel-header,
.group-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-search {
  margin-bottom: 12px;
}

.panel-scrollbar {
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
}

:deep(.org-tree-card .el-tree-node__content),
:deep(.module-panel .el-tree-node__content) {
  height: auto;
  align-items: flex-start;
  padding: 8px 0 8px 2px;
}

:deep(.org-tree-card .el-tree-node__expand-icon),
:deep(.module-panel .el-tree-node__expand-icon) {
  margin-top: 10px;
}

:deep(.org-tree-card .el-tree-node),
:deep(.module-panel .el-tree-node) {
  min-width: 0;
}

.workspace-col :deep(.el-empty) {
  display: flex;
  height: 100%;
  align-items: center;
  justify-content: center;
}

.workspace-overview {
  flex-shrink: 0;
  overflow-y: auto;
  overflow-x: hidden;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.overview-title,
.editor-title {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  font-size: 20px;
  font-weight: 600;
}

.overview-subtitle,
.editor-subtitle {
  color: var(--el-text-color-secondary);
  line-height: 1.7;
}

.overview-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.metric-row {
  margin-top: 18px;
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

.workspace-main {
  display: grid;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  gap: 16px;
  padding-top: 16px;
  grid-template-columns: 320px minmax(0, 1fr);
}

.module-panel,
.editor-panel {
  display: flex;
  min-height: 0;
  overflow: hidden;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: #fff;
  flex-direction: column;
}

.module-panel {
  padding: 16px;
}

.editor-panel {
  padding: 16px 16px 0;
}

.editor-header {
  display: flex;
  flex-shrink: 0;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.editor-tabs,
.field-scope-tabs,
.field-tabs {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
  flex-direction: column;
}

.editor-pane {
  display: flex;
  min-height: 0;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 4px;
  flex-direction: column;
}

:deep(.editor-tabs > .el-tabs__header),
:deep(.field-scope-tabs > .el-tabs__header),
:deep(.field-tabs > .el-tabs__header) {
  flex-shrink: 0;
}

:deep(.editor-tabs > .el-tabs__content),
:deep(.field-scope-tabs > .el-tabs__content),
:deep(.field-tabs > .el-tabs__content) {
  display: flex;
  min-height: 0;
  flex: 1;
  overflow: hidden;
}

:deep(.editor-tabs > .el-tabs__content > .el-tab-pane),
:deep(.field-scope-tabs > .el-tabs__content > .el-tab-pane),
:deep(.field-tabs > .el-tabs__content > .el-tab-pane) {
  min-height: 0;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

.group-card + .group-card {
  margin-top: 18px;
}

.group-card__header {
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
  font-size: 14px;
  font-weight: 600;
}

.tips-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-top: 14px;
  color: var(--el-text-color-regular);
  line-height: 1.7;
}

.sub-tab-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 14px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
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

@media (max-width: 1400px) {
  .workspace-main {
    grid-template-columns: 1fr;
    grid-template-rows: 320px minmax(0, 1fr);
  }
}

@media (max-width: 1280px) {
  .overview-header,
  .editor-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .overview-actions {
    width: 100%;
    flex-wrap: wrap;
  }

  .function-auth-page {
    height: auto;
    min-height: calc(100vh - 84px);
  }

  :deep(.function-auth-page > .el-row) {
    height: auto;
  }

  .org-tree-col,
  .workspace-col,
  .org-tree-card,
  .workspace-card {
    min-height: auto;
  }
}
</style>

