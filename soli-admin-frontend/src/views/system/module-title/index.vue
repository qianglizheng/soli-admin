<template>
  <div class="app-container module-title-page">
    <el-row :gutter="16" style="height: 100%">
      <el-col :span="6" class="module-tree-col">
        <el-card shadow="never" class="module-tree-card">
          <template #header>
            <div class="card-header">
              <span>模块树</span>
              <el-tag type="info" effect="plain">{{ moduleLeafCount }} 个模块</el-tag>
            </div>
          </template>
          <el-alert title="当前页按组件维护字段标题、占位提示和帮助说明。" type="info" :closable="false" show-icon class="panel-alert" />
          <el-input v-model="moduleKeyword" clearable placeholder="请输入模块名称或编码" prefix-icon="Search" class="panel-search" />
          <el-scrollbar class="panel-scrollbar">
            <el-tree ref="moduleTreeRef" :data="moduleTree" :expand-on-click-node="false" :filter-node-method="filterModuleNode" :highlight-current="true" node-key="id" @node-click="handleModuleNodeClick">
              <template #default="{ data }">
                <div class="tree-node">
                  <div class="tree-node__head">
                    <span class="tree-node__label">{{ data.moduleName }}</span>
                    <el-tag size="small" :type="getModuleTypeTagType(data.moduleType)" effect="plain">{{ getModuleTypeLabel(data.moduleType) }}</el-tag>
                  </div>
                  <div class="tree-node__code">{{ data.moduleCode }}</div>
                </div>
              </template>
            </el-tree>
          </el-scrollbar>
        </el-card>
      </el-col>

      <el-col :span="18" class="workspace-col">
        <el-empty v-if="!selectedModule" description="请选择左侧模块后维护字段标题信息" />
        <el-card v-else shadow="never" class="workspace-card">
          <div class="workspace-overview">
            <div class="overview-header">
              <div>
                <div class="overview-title">
                  <span>{{ selectedModule.moduleName }}</span>
                  <el-tag size="small" :type="getModuleTypeTagType(selectedModule.moduleType)" effect="plain">{{ getModuleTypeLabel(selectedModule.moduleType) }}</el-tag>
                  <el-tag size="small" :type="selectedModule.status === '0' ? 'success' : 'danger'" effect="plain">{{ selectedModule.status === '0' ? '启用' : '停用' }}</el-tag>
                </div>
                <div class="overview-subtitle">默认标题来源于模块字段定义，当前页只维护独立字段标题表中的覆盖配置。</div>
              </div>
              <div class="overview-actions">
                <el-button icon="RefreshLeft" @click="handleResetCurrentModule">恢复当前模块</el-button>
                <el-button type="primary" icon="DocumentChecked" @click="handleSave">保存</el-button>
                <el-button type="primary" plain icon="View" @click="previewVisible = true">预览上下文</el-button>
              </div>
            </div>
            <el-row :gutter="12" class="metric-row">
              <el-col :span="6"><div class="metric-card"><div class="metric-card__label">组件数</div><div class="metric-card__value">{{ components.length }}</div></div></el-col>
              <el-col :span="6"><div class="metric-card"><div class="metric-card__label">字段总数</div><div class="metric-card__value">{{ currentModuleFieldCount }}</div></div></el-col>
              <el-col :span="6"><div class="metric-card"><div class="metric-card__label">已改标题</div><div class="metric-card__value">{{ customDisplayTitleCount }}</div></div></el-col>
              <el-col :span="6"><div class="metric-card"><div class="metric-card__label">已改提示</div><div class="metric-card__value">{{ customPlaceholderCount + customHelpTextCount }}</div></div></el-col>
            </el-row>
          </div>

          <div class="workspace-main">
            <div class="editor-panel">
              <div class="editor-header">
                <div>
                  <div class="editor-title">
                    <span>{{ selectedModule.moduleName }}</span>
                    <el-tag size="small" type="info" effect="plain">{{ selectedModule.moduleCode }}</el-tag>
                  </div>
                  <div class="editor-subtitle">组件编码直接决定运行时 key，例如 `form:username`、`detail:qty`。</div>
                </div>
                <el-tag type="success" effect="plain">上下文版本 v{{ selectedModule.contextVersion }}</el-tag>
              </div>

              <el-empty v-if="!components.length" description="当前模块暂无组件定义" />
              <el-tabs v-else v-model="activeComponentCode" type="border-card" class="component-tabs">
                <el-tab-pane v-for="component in components" :key="component.componentInfo.componentCode" :label="`${component.componentInfo.componentName} (${component.fields.length})`" :name="component.componentInfo.componentCode">
                  <div class="component-summary">
                    <div>组件编码：{{ component.componentInfo.componentCode }}</div>
                    <div>排序：{{ component.componentInfo.sort }}</div>
                    <div>说明：{{ component.componentInfo.note || '-' }}</div>
                  </div>
                  <el-table :data="component.fields.slice().sort(sortBySort)" border style="width: 100%">
                    <el-table-column prop="fieldCode" label="字段编码" min-width="150" />
                    <el-table-column prop="defaultTitle" label="默认标题" min-width="130" />
                    <el-table-column label="显示标题" min-width="180">
                      <template #default="scope"><el-input v-model="getFieldConfig(scope.row.fieldCode).displayTitle" /></template>
                    </el-table-column>
                    <el-table-column label="占位提示" min-width="220">
                      <template #default="scope"><el-input v-model="getFieldConfig(scope.row.fieldCode).placeholder" :placeholder="getFieldDefaultPlaceholder(scope.row)" /></template>
                    </el-table-column>
                    <el-table-column label="帮助说明" min-width="240">
                      <template #default="scope"><el-input v-model="getFieldConfig(scope.row.fieldCode).helpText" /></template>
                    </el-table-column>
                    <el-table-column label="状态" width="100" align="center">
                      <template #default="scope">
                        <el-tag size="small" :type="isFieldCustomized(scope.row) ? 'warning' : 'info'" effect="plain">{{ isFieldCustomized(scope.row) ? '已改' : '默认' }}</el-tag>
                      </template>
                    </el-table-column>
                  </el-table>
                </el-tab-pane>
              </el-tabs>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-drawer v-model="previewVisible" title="字段标题上下文预览" size="46%">
      <pre class="context-preview">{{ previewJson }}</pre>
    </el-drawer>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onMounted, ref, watch } from 'vue';
import { ElMessage } from 'element-plus';
import {
  moduleTypeLabelMap,
  type ModuleComponentDefinition,
  type ModuleDetail,
  type ModuleFieldDefinition,
  type ModuleTreeNode,
  type ModuleType
} from '@/api/moduleCenter';
import { getModuleTitleDetail, getModuleTitleTree, saveModuleTitle } from '@/api/moduleTitle';

defineOptions({ name: 'SystemModuleTitle' });

interface ModuleFieldTitleConfig {
  displayTitle: string;
  placeholder: string;
  helpText: string;
}

const moduleTreeRef = ref();
const moduleKeyword = ref('');
const moduleTree = ref<ModuleTreeNode[]>([]);
const currentDetail = ref<ModuleDetail>();
const savedTitleStore = ref<Record<string, ModuleFieldTitleConfig>>({});
const titleStore = ref<Record<string, ModuleFieldTitleConfig>>({});
const selectedModuleId = ref<number>();
const activeComponentCode = ref('');
const previewVisible = ref(false);

const sortBySort = <T extends { sort: number }>(left: T, right: T) => left.sort - right.sort;
const sortComponentsByInfo = (left: ModuleComponentDefinition, right: ModuleComponentDefinition) => left.componentInfo.sort - right.componentInfo.sort;

const selectedModule = computed<ModuleDetail | undefined>(() => currentDetail.value?.id === selectedModuleId.value ? currentDetail.value : undefined);
const components = computed(() => selectedModule.value?.components.slice().sort(sortComponentsByInfo) || []);
const currentModuleFields = computed(() => selectedModule.value ? flattenFields(selectedModule.value) : []);
const moduleLeafCount = computed(() => countModuleLeaves(moduleTree.value));
const currentModuleFieldCount = computed(() => currentModuleFields.value.length);
const customDisplayTitleCount = computed(() => currentModuleFields.value.filter((field) => getFieldConfig(field.fieldCode).displayTitle !== field.defaultTitle).length);
const customPlaceholderCount = computed(() => currentModuleFields.value.filter((field) => getFieldConfig(field.fieldCode).placeholder !== getFieldDefaultPlaceholder(field)).length);
const customHelpTextCount = computed(() => currentModuleFields.value.filter((field) => getFieldConfig(field.fieldCode).helpText !== getFieldDefaultHelpText(field)).length);
const previewJson = computed(() => selectedModule.value ? JSON.stringify(buildModuleTitlePreview(selectedModule.value, titleStore.value), null, 2) : '');

function filterModuleNode(value: string, data: ModuleTreeNode) {
  return !value || data.moduleName.includes(value) || data.moduleCode.includes(value);
}

function handleModuleNodeClick(node: ModuleTreeNode) {
  const targetNode = node.moduleType === 'CATALOG' ? findFirstModuleLeafInNode(node) : node;
  if (targetNode) {
    selectedModuleId.value = targetNode.id;
  }
}

function getModuleTypeLabel(moduleType: ModuleType) {
  return moduleTypeLabelMap[moduleType];
}

function getModuleTypeTagType(moduleType: ModuleType) {
  return moduleType === 'CATALOG' ? 'info' : 'success';
}

function getFieldConfig(fieldCode: string) {
  if (!titleStore.value[fieldCode]) {
    titleStore.value[fieldCode] = { displayTitle: '', helpText: '', placeholder: '' };
  }
  return titleStore.value[fieldCode];
}

function isFieldCustomized(field: ModuleFieldDefinition) {
  const config = getFieldConfig(field.fieldCode);
  return config.displayTitle !== field.defaultTitle || config.placeholder !== getFieldDefaultPlaceholder(field) || config.helpText !== getFieldDefaultHelpText(field);
}

function handleResetCurrentModule() {
  titleStore.value = deepClone(savedTitleStore.value);
  ElMessage.success('当前模块字段标题配置已恢复到最近一次保存结果');
}

async function handleSave() {
  if (!selectedModule.value) {
    return;
  }
  await saveModuleTitle({
    fields: flattenFields(selectedModule.value).map((field) => ({
      displayTitle: getFieldConfig(field.fieldCode).displayTitle,
      fieldId: field.id,
      helpText: getFieldConfig(field.fieldCode).helpText,
      placeholder: getFieldConfig(field.fieldCode).placeholder
    })),
    moduleId: selectedModule.value.id
  });
  await loadModuleDetail(selectedModule.value.id);
  ElMessage.success('字段标题配置已保存');
}

watch(moduleKeyword, (value) => {
  moduleTreeRef.value?.filter(value);
});

watch(selectedModuleId, async (moduleId) => {
  if (moduleId) {
    await loadModuleDetail(moduleId);
  }
});

watch(selectedModule, (module) => {
  activeComponentCode.value = module?.components.find((item) => item.componentInfo.componentCode === activeComponentCode.value)?.componentInfo.componentCode || module?.components[0]?.componentInfo.componentCode || '';
}, { immediate: true });

onMounted(async () => {
  await loadInitialData();
  nextTick(() => {
    if (selectedModuleId.value !== undefined) {
      moduleTreeRef.value?.setCurrentKey(selectedModuleId.value);
    }
  });
});

async function loadInitialData() {
  const { data } = await getModuleTitleTree();
  moduleTree.value = data;
  const firstModule = findFirstModuleLeaf(moduleTree.value);
  if (firstModule) {
    selectedModuleId.value = firstModule.id;
  }
}

async function loadModuleDetail(moduleId: number) {
  const { data } = await getModuleTitleDetail(moduleId);
  currentDetail.value = data;
  titleStore.value = buildTitleStore(data);
  savedTitleStore.value = deepClone(titleStore.value);
}

function buildTitleStore(moduleDetail: ModuleDetail) {
  return flattenFields(moduleDetail).reduce<Record<string, ModuleFieldTitleConfig>>((result, field) => {
    result[field.fieldCode] = {
      displayTitle: field.displayTitle ?? field.defaultTitle,
      helpText: field.helpText ?? getFieldDefaultHelpText(field),
      placeholder: field.placeholder ?? getFieldDefaultPlaceholder(field)
    };
    return result;
  }, {});
}

function buildModuleTitlePreview(moduleDetail: ModuleDetail, titleMap: Record<string, ModuleFieldTitleConfig>) {
  return {
    components: moduleDetail.components.map((component) => ({
      componentInfo: { ...component.componentInfo },
      fields: component.fields.map((field) => ({
        componentType: field.componentType,
        dataPath: field.dataPath,
        defaultTitle: field.defaultTitle,
        displayTitle: titleMap[field.fieldCode]?.displayTitle ?? field.defaultTitle,
        fieldCode: field.fieldCode,
        helpText: titleMap[field.fieldCode]?.helpText ?? getFieldDefaultHelpText(field),
        placeholder: titleMap[field.fieldCode]?.placeholder ?? getFieldDefaultPlaceholder(field),
        required: field.requiredFlag === '1'
      }))
    })),
    contextVersion: moduleDetail.contextVersion,
    moduleCode: moduleDetail.moduleCode,
    moduleName: moduleDetail.moduleName
  };
}

function getFieldDefaultPlaceholder(field: ModuleFieldDefinition) {
  if (field.componentType === 'tag' || field.componentType === 'text') {
    return '';
  }
  if (['date', 'datetime', 'search-select'].includes(field.componentType)) {
    return `请选择${field.defaultTitle}`;
  }
  return `请输入${field.defaultTitle}`;
}

function getFieldDefaultHelpText(field: ModuleFieldDefinition) {
  if (field.note) {
    return field.note;
  }
  if (field.requiredFlag === '1') {
    return `${field.defaultTitle}为必填项，请按业务规范维护。`;
  }
  return `${field.defaultTitle}用于补充当前业务信息。`;
}

function flattenFields(moduleDetail: ModuleDetail) {
  return moduleDetail.components.flatMap((component) => component.fields);
}

function countModuleLeaves(nodes: ModuleTreeNode[]): number {
  return nodes.reduce((sum, node) => node.moduleType !== 'CATALOG' ? sum + 1 : sum + (node.children?.length ? countModuleLeaves(node.children) : 0), 0);
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
  for (const child of node.children || []) {
    const matched = findFirstModuleLeafInNode(child);
    if (matched) {
      return matched;
    }
  }
  return undefined;
}

function deepClone<T>(value: T): T {
  return JSON.parse(JSON.stringify(value)) as T;
}
</script>

<style scoped>
.module-title-page { box-sizing: border-box; height: 100%; overflow: hidden; }
:deep(.module-title-page > .el-row) { height: 100%; }
.module-tree-col, .workspace-col { display: flex; height: 100%; min-height: 0; flex-direction: column; }
.module-tree-card, .workspace-card { display: flex; height: 100%; min-height: 0; flex: 1; border-radius: 12px; flex-direction: column; }
:deep(.module-tree-card .el-card__body), :deep(.workspace-card .el-card__body) { display: flex; min-height: 0; flex: 1; overflow: hidden; flex-direction: column; }
.card-header, .overview-header, .editor-header { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.panel-alert, .panel-search, .component-summary { margin-bottom: 12px; }
.panel-scrollbar { min-height: 0; flex: 1; }
.tree-node { display: block; width: 100%; min-width: 0; padding-right: 8px; }
.tree-node__head { display: flex; width: 100%; min-width: 0; align-items: center; justify-content: space-between; gap: 8px; }
.tree-node__label { min-width: 0; flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.tree-node__code, .overview-subtitle, .editor-subtitle, .component-summary { color: var(--el-text-color-secondary); font-size: 13px; }
:deep(.module-tree-card .el-tree-node__content) { height: auto; align-items: flex-start; padding: 8px 0 8px 2px; }
.workspace-col :deep(.el-empty) { display: flex; height: 100%; align-items: center; justify-content: center; }
.workspace-overview { flex-shrink: 0; padding-bottom: 16px; border-bottom: 1px solid var(--el-border-color-lighter); }
.overview-title, .editor-title { display: flex; flex-wrap: wrap; align-items: center; gap: 8px; margin-bottom: 8px; font-size: 20px; font-weight: 600; }
.overview-actions { display: flex; align-items: center; gap: 8px; }
.metric-row { margin-top: 18px; }
.metric-card { height: 100%; border: 1px solid var(--el-border-color-lighter); border-radius: 12px; background: linear-gradient(180deg, #fcfdff 0%, #f8fafc 100%); padding: 16px 18px; }
.metric-card__label { color: var(--el-text-color-secondary); font-size: 13px; }
.metric-card__value { margin-top: 10px; font-size: 28px; font-weight: 600; }
.workspace-main { display: flex; min-height: 0; flex: 1; overflow: hidden; padding-top: 16px; }
.editor-panel { display: flex; min-height: 0; width: 100%; overflow: hidden; border: 1px solid var(--el-border-color-lighter); border-radius: 12px; background: #fff; padding: 16px 16px 0; flex-direction: column; }
.editor-header { flex-shrink: 0; align-items: flex-start; margin-bottom: 16px; }
.component-tabs { display: flex; min-height: 0; flex: 1; overflow: hidden; flex-direction: column; }
:deep(.component-tabs > .el-tabs__content) { display: flex; min-height: 0; flex: 1; overflow: hidden; }
:deep(.component-tabs > .el-tabs__content > .el-tab-pane) { min-height: 0; height: 100%; overflow: auto; }
.context-preview { min-height: calc(100vh - 220px); overflow: auto; margin: 0; padding: 16px; border-radius: 12px; background: #0f172a; color: #dbeafe; font-size: 13px; line-height: 1.65; white-space: pre-wrap; word-break: break-word; }
</style>
