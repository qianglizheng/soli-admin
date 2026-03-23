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
                    当前组织：{{ selectedPost.orgName }}，左侧岗位树和右侧模块树均为 mock 数据，当前页只配置岗位基线权限。
                  </div>
                </div>

                <div class="overview-actions">
                  <el-button icon="RefreshLeft" :disabled="!selectedModule" @click="handleResetCurrentModule">恢复当前模块</el-button>
                  <el-button type="primary" icon="DocumentChecked" @click="handleSaveMock">保存 Mock</el-button>
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
                <el-empty v-if="!selectedModule" description="请选择右侧模块后配置权限" />

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
                                title="当前页配置的是岗位基线权限，不放大权限。状态权限中心只允许在岗位基线上继续收紧。"
                                type="info"
                                :closable="false"
                                show-icon
                              />
                              <div class="tips-list">
                                <div>1. 模块隐藏后，运行时模块入口直接不可见。</div>
                                <div>2. 按钮权限支持不可见、禁用、可用三档。</div>
                                <div>3. 字段权限支持不可见、只读、可写三档。</div>
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
                          <el-tab-pane label="单头字段" name="header">
                            <el-empty v-if="!selectedModule.headerTabs.length" description="当前模块暂无单头字段" />
                            <template v-else>
                              <el-tabs v-model="activeHeaderTab" type="border-card" class="field-tabs">
                                <el-tab-pane
                                  v-for="tab in headerTabs"
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
  findModuleNode,
  type ModuleNode
} from '../module-center/moduleCenterMock';
import {
  buildFunctionAuthPreview,
  buttonPermissionLabelMap,
  buttonPermissionLevelOptions,
  buttonPermissionTagTypeMap,
  cloneFunctionAuthModuleTree,
  cloneFunctionAuthPermissionStore,
  cloneOrgPostTree,
  fieldPermissionLabelMap,
  fieldPermissionLevelOptions,
  fieldPermissionTagTypeMap,
  findFirstModuleLeafInNode,
  findFirstPostLeaf,
  findFirstPostLeafInNode,
  findOrgPostNode,
  getOrgNodeTypeLabel,
  groupModuleButtons,
  type ButtonPermissionLevel,
  type FieldPermissionLevel,
  type ModulePermissionConfig,
  type OrgNodeType,
  type OrgPostTreeNode
} from './functionAuthMock';

defineOptions({
  name: 'SystemFunctionAuth'
});

const moduleTypeLabelMap = {
  BILL: '单据模块',
  CATALOG: '目录',
  PAGE: '页面'
} as const;

const postTreeRef = ref();
const moduleTreeRef = ref();
const postKeyword = ref('');
const moduleKeyword = ref('');
const orgPostTree = ref(cloneOrgPostTree());
const moduleTree = ref(cloneFunctionAuthModuleTree());
const savedPermissionStore = ref(cloneFunctionAuthPermissionStore());
const permissionStore = ref(cloneFunctionAuthPermissionStore());
const selectedPostId = ref<number>();
const selectedModuleId = ref<number>();
const activePane = ref<'module' | 'button' | 'field'>('module');
const activeFieldScope = ref<'header' | 'detail'>('header');
const activeHeaderTab = ref('');
const activeDetailTab = ref('');
const previewVisible = ref(false);

const sortBySort = <T extends { sort: number }>(left: T, right: T) => left.sort - right.sort;

const selectedPost = computed(() => {
  const node = findOrgPostNode(orgPostTree.value, selectedPostId.value);
  return node?.nodeType === 'POST' ? node : undefined;
});

const selectedModule = computed(() => {
  return findModuleNode(moduleTree.value, selectedModuleId.value);
});

const currentModuleConfig = computed<ModulePermissionConfig | undefined>(() => {
  if (!selectedPost.value || !selectedModule.value) {
    return undefined;
  }
  return permissionStore.value[selectedPost.value.id]?.[selectedModule.value.moduleCode];
});

const headerTabs = computed(() => {
  return selectedModule.value?.headerTabs.slice().sort(sortBySort) || [];
});

const detailTabs = computed(() => {
  return selectedModule.value?.detailTabs.slice().sort(sortBySort) || [];
});

const buttonGroups = computed(() => {
  return selectedModule.value ? groupModuleButtons(selectedModule.value) : [];
});

const postLeafCount = computed(() => {
  const countLeaves = (nodes: OrgPostTreeNode[]): number => {
    return nodes.reduce((sum, node) => {
      if (node.nodeType === 'POST') {
        return sum + 1;
      }
      return sum + (node.children?.length ? countLeaves(node.children) : 0);
    }, 0);
  };
  return countLeaves(orgPostTree.value);
});

const visibleModuleCount = computed(() => {
  if (!selectedPost.value) {
    return 0;
  }
  return Object.values(permissionStore.value[selectedPost.value.id] || {}).filter((item) => item.moduleVisible).length;
});

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
  return JSON.stringify(
    buildFunctionAuthPreview(selectedPost.value, selectedModule.value, currentModuleConfig.value),
    null,
    2
  );
});

const getModuleTypeLabel = (moduleType: ModuleNode['moduleType']) => {
  return moduleTypeLabelMap[moduleType];
};

const getModuleTypeTagType = (moduleType: ModuleNode['moduleType']) => {
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

const filterModuleNode = (value: string, data: ModuleNode) => {
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

const handleModuleNodeClick = (node: ModuleNode) => {
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
  if (!selectedPost.value || !selectedModule.value) {
    return;
  }
  const currentPost = selectedPost.value;
  const currentModule = selectedModule.value;
  const postModuleMap = permissionStore.value[currentPost.id];
  const savedPostModuleMap = savedPermissionStore.value[currentPost.id];
  if (!postModuleMap || !savedPostModuleMap) {
    return;
  }
  postModuleMap[currentModule.moduleCode] = JSON.parse(JSON.stringify(
    savedPostModuleMap[currentModule.moduleCode]
  )) as ModulePermissionConfig;
  ElMessage.success('当前模块权限已恢复到最近一次保存结果');
};

const handleSaveMock = () => {
  savedPermissionStore.value = JSON.parse(JSON.stringify(permissionStore.value));
  ElMessage.success('功能授权 mock 数据已保存');
};

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
  activeHeaderTab.value = module?.headerTabs[0]?.tabCode || '';
  activeDetailTab.value = module?.detailTabs[0]?.tabCode || '';
}, { immediate: true });

const firstPost = findFirstPostLeaf(orgPostTree.value);
const firstModuleRoot = moduleTree.value[0];
const firstModule = firstModuleRoot ? (findFirstModuleLeafInNode(firstModuleRoot) || firstModuleRoot) : undefined;

if (firstPost) {
  selectPost(firstPost.id);
}

if (firstModule) {
  selectModule(firstModule.id);
}

onMounted(() => {
  nextTick(() => {
    if (selectedPostId.value !== undefined) {
      postTreeRef.value?.setCurrentKey(selectedPostId.value);
    }
    if (selectedModuleId.value !== undefined) {
      moduleTreeRef.value?.setCurrentKey(selectedModuleId.value);
    }
  });
});
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
