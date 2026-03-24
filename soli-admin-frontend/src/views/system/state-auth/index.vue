<template>
  <div class="app-container state-auth-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="6" class="module-tree-col">
        <el-card shadow="never" class="module-tree-card">
          <template #header>
            <div class="card-header">
              <span>状态型模块</span>
              <el-tag type="info" effect="plain">{{ moduleLeafCount }} 个模块</el-tag>
            </div>
          </template>

          <el-alert
            title="当前页只维护状态维度的附加限制，只展示状态型模块，不维护岗位基线。"
            type="info"
            :closable="false"
            show-icon
            class="panel-alert"
          />

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
        </el-card>
      </el-col>

      <el-col :span="18" class="workspace-col">
        <el-empty v-if="!selectedModule" description="请选择左侧状态型模块后维护状态权限" />

        <template v-else>
          <el-card shadow="never" class="workspace-card">
            <div class="workspace-overview">
              <div class="overview-header">
                <div>
                  <div class="overview-title">
                    <span>{{ selectedModule.moduleName }}</span>
                    <el-tag size="small" :type="getModuleTypeTagType(selectedModule.moduleType)" effect="plain">
                      {{ getModuleTypeLabel(selectedModule.moduleType) }}
                    </el-tag>
                    <el-tag size="small" type="warning" effect="plain">状态字段：{{ selectedModule.stateFieldCode || '-' }}</el-tag>
                    <el-tag size="small" :type="selectedModule.status === '0' ? 'success' : 'danger'" effect="plain">
                      {{ selectedModule.status === '0' ? '启用' : '停用' }}
                    </el-tag>
                  </div>
                  <div class="overview-subtitle">
                    当前页面只维护状态附加限制，选择“不收紧”表示保留岗位基线结果；最终权限按 `min(岗位基线, 状态限制)` 合并。
                  </div>
                </div>

                <div class="overview-actions">
                  <el-button icon="RefreshLeft" @click="handleResetCurrentModule">恢复当前模块</el-button>
                  <el-button type="primary" icon="DocumentChecked" @click="handleSaveMock">保存 Mock</el-button>
                  <el-button type="primary" plain icon="View" @click="openPreviewDrawer">预览规则</el-button>
                </div>
              </div>

              <el-row :gutter="12" class="metric-row">
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">标准状态</div>
                    <div class="metric-card__value">{{ stateCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">启用流转</div>
                    <div class="metric-card__value">{{ enabledTransitionCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">当前收紧字段</div>
                    <div class="metric-card__value">{{ currentRestrictedFieldCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="metric-card">
                    <div class="metric-card__label">当前收紧按钮</div>
                    <div class="metric-card__value">{{ currentRestrictedButtonCount }}</div>
                  </div>
                </el-col>
              </el-row>
            </div>

            <div class="workspace-main">
              <div class="state-panel">
                <div class="panel-header">
                  <span>状态列表</span>
                  <el-tag size="small" effect="plain">{{ stateCount }} 个状态</el-tag>
                </div>

                <el-alert
                  title="create 已并入未审核，状态权限只做收紧，不允许放大岗位基线。"
                  type="warning"
                  :closable="false"
                  show-icon
                  class="panel-alert"
                />

                <el-scrollbar class="state-scrollbar">
                  <div class="state-list">
                    <button
                      v-for="state in stateList"
                      :key="state.stateCode"
                      type="button"
                      class="state-card"
                      :class="{ 'is-active': selectedState?.stateCode === state.stateCode }"
                      @click="selectState(state.stateCode)"
                    >
                      <div class="state-card__header">
                        <span>{{ state.stateName }}</span>
                        <el-tag size="small" :type="getStateSummaryTagType(state)" effect="plain">
                          {{ state.stateCode }}
                        </el-tag>
                      </div>
                      <div class="state-card__meta">
                        <el-tag v-if="state.isInitial === '1'" size="small" type="primary" effect="plain">初始</el-tag>
                        <el-tag v-if="state.isFinal === '1'" size="small" type="success" effect="plain">终态</el-tag>
                        <el-tag size="small" :type="state.status === '0' ? 'success' : 'danger'" effect="plain">
                          {{ state.status === '0' ? '启用' : '停用' }}
                        </el-tag>
                      </div>
                      <div class="state-card__note">{{ state.note || '当前状态未填写说明。' }}</div>
                      <div class="state-card__stats">
                        <span>字段收紧 {{ getStateFieldTightenCount(state.stateCode) }}</span>
                        <span>按钮收紧 {{ getStateButtonTightenCount(state.stateCode) }}</span>
                      </div>
                    </button>
                  </div>
                </el-scrollbar>
              </div>

              <div class="editor-panel">
                <div class="editor-header">
                  <div>
                    <div class="editor-title">
                      <span>{{ selectedState?.stateName || '-' }}</span>
                      <el-tag v-if="selectedState" size="small" :type="getStateSummaryTagType(selectedState)" effect="plain">
                        {{ selectedState.stateCode }}
                      </el-tag>
                      <el-tag v-if="selectedState?.isInitial === '1'" size="small" type="primary" effect="plain">初始状态</el-tag>
                      <el-tag v-if="selectedState?.isFinal === '1'" size="small" type="success" effect="plain">最终状态</el-tag>
                    </div>
                    <div class="editor-subtitle">
                      {{ selectedState?.note || '当前状态未配置说明。' }}
                    </div>
                  </div>
                  <el-tag type="info" effect="plain">模块编码：{{ selectedModule.moduleCode }}</el-tag>
                </div>

                <el-tabs v-model="activePane" class="editor-tabs">
                  <el-tab-pane label="状态定义" name="state">
                    <div class="editor-pane">
                      <el-row :gutter="16">
                        <el-col :span="14">
                          <el-card shadow="never" class="inner-card">
                            <template #header>标准状态定义</template>
                            <el-table :data="stateList" border>
                              <el-table-column prop="stateCode" label="状态编码" min-width="140" />
                              <el-table-column prop="stateName" label="状态名称" min-width="120" />
                              <el-table-column label="初始" width="90" align="center">
                                <template #default="scope">
                                  <el-tag size="small" :type="scope.row.isInitial === '1' ? 'primary' : 'info'" effect="plain">
                                    {{ scope.row.isInitial === '1' ? '是' : '否' }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column label="终态" width="90" align="center">
                                <template #default="scope">
                                  <el-tag size="small" :type="scope.row.isFinal === '1' ? 'success' : 'info'" effect="plain">
                                    {{ scope.row.isFinal === '1' ? '是' : '否' }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column prop="sort" label="排序" width="80" align="center" />
                              <el-table-column label="状态" width="90" align="center">
                                <template #default="scope">
                                  <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                                    {{ scope.row.status === '0' ? '启用' : '停用' }}
                                  </el-tag>
                                </template>
                              </el-table-column>
                              <el-table-column prop="note" label="说明" min-width="220" show-overflow-tooltip />
                            </el-table>
                          </el-card>
                        </el-col>
                        <el-col :span="10">
                          <el-card shadow="never" class="inner-card">
                            <template #header>当前状态说明</template>
                            <el-descriptions :column="1" size="large">
                              <el-descriptions-item label="模块名称">{{ selectedModule.moduleName }}</el-descriptions-item>
                              <el-descriptions-item label="状态名称">{{ selectedState?.stateName || '-' }}</el-descriptions-item>
                              <el-descriptions-item label="状态编码">{{ selectedState?.stateCode || '-' }}</el-descriptions-item>
                              <el-descriptions-item label="状态字段">{{ selectedModule.stateFieldCode || '-' }}</el-descriptions-item>
                              <el-descriptions-item label="收紧字段">{{ currentRestrictedFieldCount }}</el-descriptions-item>
                              <el-descriptions-item label="收紧按钮">{{ currentRestrictedButtonCount }}</el-descriptions-item>
                            </el-descriptions>
                            <el-alert
                              title="状态中心只负责附加限制。岗位不可见的资源，状态权限不能反向改成可见。"
                              type="info"
                              :closable="false"
                              show-icon
                              class="state-tip"
                            />
                          </el-card>
                        </el-col>
                      </el-row>
                    </div>
                  </el-tab-pane>

                  <el-tab-pane label="流转规则" name="transition">
                    <div class="editor-pane">
                      <el-table :data="transitionList" border>
                        <el-table-column prop="actionButtonCode" label="动作编码" min-width="140" />
                        <el-table-column prop="actionButtonName" label="动作名称" min-width="120" />
                        <el-table-column label="流转路径" min-width="220">
                          <template #default="scope">
                            <div class="transition-cell">
                              <el-tag size="small" effect="plain">{{ scope.row.fromStateCode }}</el-tag>
                              <span class="transition-cell__arrow">-&gt;</span>
                              <el-tag size="small" type="success" effect="plain">{{ scope.row.toStateCode }}</el-tag>
                            </div>
                          </template>
                        </el-table-column>
                        <el-table-column prop="sort" label="排序" width="80" align="center" />
                        <el-table-column label="状态" width="90" align="center">
                          <template #default="scope">
                            <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
                              {{ scope.row.status === '0' ? '启用' : '停用' }}
                            </el-tag>
                          </template>
                        </el-table-column>
                        <el-table-column prop="note" label="说明" min-width="220" show-overflow-tooltip />
                      </el-table>
                    </div>
                  </el-tab-pane>

                  <el-tab-pane label="按钮权限" name="button">
                    <div class="editor-pane">
                      <el-empty v-if="!selectedModule.buttons.length" description="当前模块暂无按钮定义" />
                      <template v-else>
                        <div v-for="group in buttonGroups" :key="group.area" class="group-card">
                          <div class="group-card__header">
                            <span>{{ buttonAreaLabelMap[group.area] }}</span>
                            <el-tag size="small" effect="plain">{{ group.buttons.length }} 个按钮</el-tag>
                          </div>
                          <el-table :data="group.buttons" border>
                            <el-table-column prop="buttonCode" label="按钮编码" min-width="150" />
                            <el-table-column prop="defaultTitle" label="按钮标题" min-width="120" />
                            <el-table-column label="状态限制" width="180">
                              <template #default="scope">
                                <el-select
                                  :model-value="getButtonPermission(scope.row.buttonCode)"
                                  style="width: 100%"
                                  @update:model-value="handleButtonPermissionChange(scope.row.buttonCode, $event as StateButtonLimitLevel)"
                                >
                                  <el-option
                                    v-for="item in stateButtonLimitLevelOptions"
                                    :key="item.value"
                                    :label="item.label"
                                    :value="item.value"
                                  />
                                </el-select>
                              </template>
                            </el-table-column>
                            <el-table-column label="效果预览" width="120" align="center">
                              <template #default="scope">
                                <el-tag :type="stateButtonLimitTagTypeMap[getButtonPermission(scope.row.buttonCode)]" effect="plain">
                                  {{ stateButtonLimitLabelMap[getButtonPermission(scope.row.buttonCode)] }}
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
                          <el-empty v-if="!headerTabs.length" description="当前模块暂无表头字段" />
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
                                  <el-table-column label="状态限制" width="180">
                                    <template #default="scope">
                                      <el-select
                                        :model-value="getFieldPermission(scope.row.fieldCode)"
                                        style="width: 100%"
                                        @update:model-value="handleFieldPermissionChange(scope.row.fieldCode, $event as StateFieldLimitLevel)"
                                      >
                                        <el-option
                                          v-for="item in stateFieldLimitLevelOptions"
                                          :key="item.value"
                                          :label="item.label"
                                          :value="item.value"
                                        />
                                      </el-select>
                                    </template>
                                  </el-table-column>
                                  <el-table-column label="效果预览" width="120" align="center">
                                    <template #default="scope">
                                      <el-tag :type="stateFieldLimitTagTypeMap[getFieldPermission(scope.row.fieldCode)]" effect="plain">
                                        {{ stateFieldLimitLabelMap[getFieldPermission(scope.row.fieldCode)] }}
                                      </el-tag>
                                    </template>
                                  </el-table-column>
                                </el-table>
                              </el-tab-pane>
                            </el-tabs>
                          </template>
                        </el-tab-pane>

                        <el-tab-pane label="明细字段" name="detail">
                          <el-empty v-if="!detailTabs.length" description="当前模块暂无明细字段" />
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
                                  <el-table-column label="状态限制" width="180">
                                    <template #default="scope">
                                      <el-select
                                        :model-value="getFieldPermission(scope.row.fieldCode)"
                                        style="width: 100%"
                                        @update:model-value="handleFieldPermissionChange(scope.row.fieldCode, $event as StateFieldLimitLevel)"
                                      >
                                        <el-option
                                          v-for="item in stateFieldLimitLevelOptions"
                                          :key="item.value"
                                          :label="item.label"
                                          :value="item.value"
                                        />
                                      </el-select>
                                    </template>
                                  </el-table-column>
                                  <el-table-column label="效果预览" width="120" align="center">
                                    <template #default="scope">
                                      <el-tag :type="stateFieldLimitTagTypeMap[getFieldPermission(scope.row.fieldCode)]" effect="plain">
                                        {{ stateFieldLimitLabelMap[getFieldPermission(scope.row.fieldCode)] }}
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
              </div>
            </div>
          </el-card>
        </template>
      </el-col>
    </el-row>

    <el-drawer v-model="previewVisible" title="状态权限规则预览" size="46%">
      <div class="preview-header">
        <el-tag type="primary" effect="plain">{{ selectedModule?.moduleName }}</el-tag>
        <el-tag v-if="selectedState" :type="getStateSummaryTagType(selectedState)" effect="plain">
          状态：{{ selectedState.stateName }}
        </el-tag>
        <el-tag type="warning" effect="plain">仅状态收紧规则</el-tag>
      </div>
      <pre class="context-preview">{{ previewJson }}</pre>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import type { ModuleNode, ModuleTabDefinition } from '../module-center/moduleCenterFixture';
import { buttonAreaLabelMap } from '../module-center/moduleCenterFixture';
import { groupModuleButtons } from '../function-auth/functionAuthMock';
import {
  buildStateAuthPreview,
  cloneStateAuthModuleTree,
  cloneStateAuthStore,
  findFirstStatefulModule,
  findFirstStatefulModuleInNode,
  findStateAuthModuleNode,
  getRestrictedButtonCount,
  getRestrictedFieldCount,
  getStateSummaryTagType,
  stateButtonLimitLabelMap,
  stateButtonLimitLevelOptions,
  stateButtonLimitTagTypeMap,
  stateFieldLimitLabelMap,
  stateFieldLimitLevelOptions,
  stateFieldLimitTagTypeMap,
  type ModuleStateAuthConfig,
  type ModuleStateDefinition,
  type StateButtonLimitLevel,
  type StateFieldLimitLevel
} from './stateAuthMock';

defineOptions({
  name: 'SystemStateAuth'
});

const moduleTypeLabelMap = {
  BILL: '单据模块',
  CATALOG: '目录',
  PAGE: '页面'
} as const;

const moduleTreeRef = ref();
const moduleKeyword = ref('');
const moduleTree = ref(cloneStateAuthModuleTree());
const savedStateAuthStore = ref(cloneStateAuthStore());
const stateAuthStore = ref(cloneStateAuthStore());
const selectedModuleId = ref<number>();
const selectedStateCode = ref('');
const activePane = ref<'state' | 'transition' | 'button' | 'field'>('state');
const activeFieldScope = ref<'header' | 'detail'>('header');
const activeHeaderTab = ref('');
const activeDetailTab = ref('');
const previewVisible = ref(false);

const sortBySort = <T extends { sort: number }>(left: T, right: T) => left.sort - right.sort;
const sortTabsByInfo = (left: ModuleTabDefinition, right: ModuleTabDefinition) => left.tabInfo.sort - right.tabInfo.sort;

const selectedModule = computed(() => {
  return findStateAuthModuleNode(moduleTree.value, selectedModuleId.value);
});

const currentModuleConfig = computed<ModuleStateAuthConfig | undefined>(() => {
  if (!selectedModule.value) {
    return undefined;
  }
  return stateAuthStore.value[selectedModule.value.moduleCode];
});

const selectedState = computed<ModuleStateDefinition | undefined>(() => {
  if (!currentModuleConfig.value) {
    return undefined;
  }
  return currentModuleConfig.value.states.find((item) => item.stateCode === selectedStateCode.value)
    || currentModuleConfig.value.states[0];
});

const currentStatePermission = computed(() => {
  if (!currentModuleConfig.value || !selectedState.value) {
    return undefined;
  }
  return currentModuleConfig.value.permissionsByState[selectedState.value.stateCode];
});

const stateList = computed(() => {
  return currentModuleConfig.value?.states.slice().sort(sortBySort) || [];
});

const transitionList = computed(() => {
  return currentModuleConfig.value?.transitions.slice().sort(sortBySort) || [];
});

const headerTabs = computed(() => {
  return selectedModule.value?.headerTabs.slice().sort(sortTabsByInfo) || [];
});

const detailTabs = computed(() => {
  return selectedModule.value?.detailTabs.slice().sort(sortTabsByInfo) || [];
});

const buttonGroups = computed(() => {
  return selectedModule.value ? groupModuleButtons(selectedModule.value) : [];
});

const moduleLeafCount = computed(() => {
  const countLeaves = (nodes: ModuleNode[]): number => {
    return nodes.reduce((sum, node) => {
      if (node.moduleType !== 'CATALOG') {
        return sum + 1;
      }
      return sum + (node.children?.length ? countLeaves(node.children) : 0);
    }, 0);
  };
  return countLeaves(moduleTree.value);
});

const stateCount = computed(() => stateList.value.length);

const enabledTransitionCount = computed(() => {
  return transitionList.value.filter((item) => item.status === '0').length;
});

const currentRestrictedFieldCount = computed(() => {
  if (!currentModuleConfig.value || !selectedState.value) {
    return 0;
  }
  return getRestrictedFieldCount(currentModuleConfig.value, selectedState.value.stateCode);
});

const currentRestrictedButtonCount = computed(() => {
  if (!currentModuleConfig.value || !selectedState.value) {
    return 0;
  }
  return getRestrictedButtonCount(currentModuleConfig.value, selectedState.value.stateCode);
});

const previewJson = computed(() => {
  if (!selectedModule.value || !selectedState.value || !currentModuleConfig.value) {
    return '';
  }
  return JSON.stringify(
    buildStateAuthPreview(selectedModule.value, selectedState.value, currentModuleConfig.value),
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

const filterModuleNode = (value: string, data: ModuleNode) => {
  if (!value) {
    return true;
  }
  return data.moduleName.includes(value) || data.moduleCode.includes(value);
};

const selectModule = (id?: number) => {
  selectedModuleId.value = id;
  if (id !== undefined) {
    moduleTreeRef.value?.setCurrentKey(id);
  }
};

const selectState = (stateCode: string) => {
  selectedStateCode.value = stateCode;
};

const handleModuleNodeClick = (node: ModuleNode) => {
  const targetNode = node.moduleType === 'CATALOG' ? findFirstStatefulModuleInNode(node) : node;
  if (targetNode) {
    selectModule(targetNode.id);
  }
};

const getFieldPermission = (fieldCode: string): StateFieldLimitLevel => {
  return currentStatePermission.value?.fieldPermissions[fieldCode] ?? 2;
};

const getButtonPermission = (buttonCode: string): StateButtonLimitLevel => {
  return currentStatePermission.value?.buttonPermissions[buttonCode] ?? 2;
};

const handleFieldPermissionChange = (fieldCode: string, level: StateFieldLimitLevel) => {
  if (!currentStatePermission.value) {
    return;
  }
  currentStatePermission.value.fieldPermissions[fieldCode] = level;
};

const handleButtonPermissionChange = (buttonCode: string, level: StateButtonLimitLevel) => {
  if (!currentStatePermission.value) {
    return;
  }
  currentStatePermission.value.buttonPermissions[buttonCode] = level;
};

const getStateFieldTightenCount = (stateCode: string) => {
  return getRestrictedFieldCount(currentModuleConfig.value, stateCode);
};

const getStateButtonTightenCount = (stateCode: string) => {
  return getRestrictedButtonCount(currentModuleConfig.value, stateCode);
};

const handleResetCurrentModule = () => {
  if (!selectedModule.value) {
    return;
  }
  stateAuthStore.value[selectedModule.value.moduleCode] = JSON.parse(JSON.stringify(
    savedStateAuthStore.value[selectedModule.value.moduleCode]
  )) as ModuleStateAuthConfig;
  ElMessage.success('当前模块状态权限已恢复到最近一次保存结果');
};

const handleSaveMock = () => {
  savedStateAuthStore.value = JSON.parse(JSON.stringify(stateAuthStore.value));
  ElMessage.success('状态权限 mock 数据已保存');
};

const openPreviewDrawer = () => {
  if (!selectedModule.value || !selectedState.value) {
    return;
  }
  previewVisible.value = true;
};

watch(moduleKeyword, (value) => {
  moduleTreeRef.value?.filter(value);
});

watch(selectedModule, (module) => {
  if (!module) {
    selectedStateCode.value = '';
    activeHeaderTab.value = '';
    activeDetailTab.value = '';
    return;
  }
  const moduleConfig = stateAuthStore.value[module.moduleCode];
  selectedStateCode.value = moduleConfig?.states[0]?.stateCode || '';
  activeHeaderTab.value = module.headerTabs[0]?.tabInfo.tabCode || '';
  activeDetailTab.value = module.detailTabs[0]?.tabInfo.tabCode || '';
}, { immediate: true });

const firstModule = findFirstStatefulModule(moduleTree.value);
if (firstModule) {
  selectModule(firstModule.id);
}

onMounted(() => {
  nextTick(() => {
    if (selectedModuleId.value !== undefined) {
      moduleTreeRef.value?.setCurrentKey(selectedModuleId.value);
    }
  });
});
</script>

<style scoped>
.state-auth-page {
  box-sizing: border-box;
  height: 100%;
  overflow: hidden;
}

:deep(.state-auth-page > .el-row) {
  height: 100%;
}

.module-tree-col,
.workspace-col {
  display: flex;
  height: 100%;
  min-height: 0;
  flex-direction: column;
}

.module-tree-card,
.workspace-card {
  display: flex;
  height: 100%;
  min-height: 0;
  flex: 1;
  border-radius: 12px;
  flex-direction: column;
}

:deep(.module-tree-card .el-card__body),
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

.panel-alert,
.panel-search {
  margin-bottom: 12px;
}

.panel-scrollbar,
.state-scrollbar {
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

.state-panel,
.editor-panel {
  display: flex;
  min-height: 0;
  overflow: hidden;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: #fff;
  flex-direction: column;
}

.state-panel {
  padding: 16px;
}

.editor-panel {
  padding: 16px 16px 0;
}

.state-list {
  display: flex;
  padding-top: 2px;
  padding-right: 2px;
  padding-bottom: 4px;
  flex-direction: column;
  gap: 12px;
}

.state-card {
  width: 100%;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fafc 100%);
  padding: 14px;
  color: inherit;
  cursor: pointer;
  text-align: left;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.state-card:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 10px 24px rgb(15 23 42 / 8%);
  transform: translateY(-1px);
}

.state-card.is-active {
  border-color: var(--el-color-primary);
  box-shadow: 0 12px 28px rgb(64 158 255 / 16%);
}

.state-card__header,
.state-card__meta,
.state-card__stats,
.editor-header,
.transition-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.state-card__header,
.editor-header {
  justify-content: space-between;
}

.state-card__meta {
  flex-wrap: wrap;
  margin-top: 10px;
}

.state-card__note {
  margin-top: 10px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
  line-height: 1.65;
}

.state-card__stats {
  justify-content: space-between;
  margin-top: 12px;
  color: var(--el-text-color-regular);
  font-size: 12px;
}

.editor-header {
  flex-shrink: 0;
  align-items: flex-start;
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

.sub-tab-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 14px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.state-tip {
  margin-top: 16px;
}

.transition-cell__arrow {
  color: var(--el-text-color-secondary);
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

  .state-auth-page {
    height: auto;
    min-height: calc(100vh - 84px);
  }

  :deep(.state-auth-page > .el-row) {
    height: auto;
  }

  .module-tree-col,
  .workspace-col,
  .module-tree-card,
  .workspace-card {
    min-height: auto;
  }
}
</style>

