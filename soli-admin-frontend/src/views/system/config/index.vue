<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item
          v-if="listFieldConfigMap.configName.visible"
          :label="listFieldConfigMap.configName.label"
          prop="configName"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.configName"
            :placeholder="listFieldConfigMap.configName.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.configKey.visible"
          :label="listFieldConfigMap.configKey.label"
          prop="configKey"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.configKey"
            :placeholder="listFieldConfigMap.configKey.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.configType.visible"
          :label="listFieldConfigMap.configType.label"
          prop="configType"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-select
            v-model="queryParams.configType"
            :placeholder="listFieldConfigMap.configType.placeholder"
            clearable
            style="width: 240px"
          >
            <el-option label="是" value="Y" />
            <el-option label="否" value="N" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.note.visible"
          :label="listFieldConfigMap.note.label"
          prop="remarkKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.remarkKeyword"
            :placeholder="listFieldConfigMap.note.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item data-search-actions="true">
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
          <el-button v-if="isSearchMeasured && showMoreButton" link @click="toggleMoreSearch">
            {{ showMoreSearch ? '收起' : '更多' }}
            <el-icon class="el-icon--right"><component :is="showMoreSearch ? 'ArrowUp' : 'ArrowDown'" /></el-icon>
          </el-button>
        </el-form-item>
      </div>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col v-if="createButtonConfig.visible" :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          :disabled="createButtonConfig.disabled"
          @click="handleAdd"
        >
          {{ createButtonConfig.label }}
        </el-button>
      </el-col>
      <el-col v-if="editButtonConfig.visible" :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single || editButtonConfig.disabled"
          @click="handleUpdate()"
        >
          {{ editButtonConfig.label }}
        </el-button>
      </el-col>
      <el-col v-if="listButtonConfigMap.remove.visible" :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple || listButtonConfigMap.remove.disabled"
          @click="handleDelete()"
        >
          {{ listButtonConfigMap.remove.label }}
        </el-button>
      </el-col>
      <el-col v-if="listButtonConfigMap.refreshCache.visible" :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Refresh"
          :disabled="listButtonConfigMap.refreshCache.disabled"
          @click="handleRefreshCache"
        >
          {{ listButtonConfigMap.refreshCache.label }}
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参数编号" prop="id" width="180" />
      <el-table-column
        v-if="listFieldConfigMap.configName.visible"
        :label="listFieldConfigMap.configName.label"
        prop="configName"
        min-width="180"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-if="listFieldConfigMap.configKey.visible"
        :label="listFieldConfigMap.configKey.label"
        prop="configKey"
        min-width="180"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-if="listFieldConfigMap.configValue.visible"
        :label="listFieldConfigMap.configValue.label"
        prop="configValue"
        min-width="220"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-if="listFieldConfigMap.configType.visible"
        :label="listFieldConfigMap.configType.label"
        align="center"
        width="100"
      >
        <template #default="{ row }">
          <el-tag :type="row.configType === 'Y' ? 'danger' : 'info'">
            {{ row.configType === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-if="listFieldConfigMap.note.visible"
        :label="listFieldConfigMap.note.label"
        prop="note"
        min-width="220"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" fixed="right" min-width="260" class-name="small-padding fixed-width">
        <template #default="{ row }">
          <el-button
            v-if="editButtonConfig.visible"
            link
            type="primary"
            icon="Edit"
            :disabled="editButtonConfig.disabled"
            @click="handleUpdate(row)"
          >
            {{ editButtonConfig.label }}
          </el-button>
          <el-button
            v-if="listButtonConfigMap.remove.visible"
            link
            type="primary"
            icon="Delete"
            :disabled="listButtonConfigMap.remove.disabled"
            @click="handleDelete(row)"
          >
            {{ listButtonConfigMap.remove.label }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handlePageSizeChange"
        @current-change="getList"
      />
    </div>

    <config-form
      v-model="formVisible"
      :mode="formMode"
      :context="formContext"
      :initial-data="formInitial"
      :submitting="submitLoading"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createConfig,
  deleteConfig,
  getConfigDetail,
  getConfigModuleContext,
  getConfigPage,
  refreshConfigCache,
  updateConfig
} from '@/api/config';
import { useStatefulModuleContext } from '@/composables/useStatefulModuleContext';
import type { SysConfig } from '@/types/global';
import ConfigForm from './components/ConfigForm.vue';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  type ModuleButtonFallback,
  type ModuleFieldFallback
} from '@/utils/moduleContext';
import { useSearchCollapse } from '@/utils/useSearchCollapse';

defineOptions({
  name: 'SystemConfig'
});

type ConfigFieldCode = 'configName' | 'configKey' | 'configValue' | 'configType' | 'note';
type ConfigButtonCode = 'create' | 'modify' | 'remove' | 'refreshCache';
const CONFIG_FORM_COMPONENT = 'config_form';

const configFieldFallbackMap: Record<ConfigFieldCode, ModuleFieldFallback> = {
  configKey: { label: '参数键名', placeholder: '请输入参数键名', required: true, visible: true },
  configName: { label: '参数名称', placeholder: '请输入参数名称', required: true, visible: true },
  configType: { label: '系统内置', placeholder: '请选择系统内置', required: true, visible: true },
  configValue: { label: '参数键值', placeholder: '请输入参数键值', required: false, visible: true },
  note: { label: '备注', placeholder: '请输入备注', required: false, visible: true }
};

const configButtonFallbackMap: Record<ConfigButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  refreshCache: { disabled: false, label: '刷新缓存', visible: true },
  remove: { disabled: false, label: '删除', visible: true }
};

const showSearch = ref(true);
const {
  searchCollapseRef,
  isSearchMeasured,
  showMoreButton,
  showMoreSearch,
  toggleMoreSearch
} = useSearchCollapse(showSearch);
const loading = ref(false);
const submitLoading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const configList = ref<SysConfig[]>([]);
const selectedRows = ref<SysConfig[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<SysConfig>>({});

const {
  activeContext: formContext,
  getStateContext,
  listContext,
  loadListContext,
  preloadStateContexts,
  setActiveStateContext
} = useStatefulModuleContext<'create' | 'edit'>({
  loadContext: async (stateCode) => {
    try {
      const { data } = await getConfigModuleContext(stateCode);
      return data;
    } catch {
      return null;
    }
  }
});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: '',
  configKey: '',
  configType: '',
  remarkKeyword: ''
});

const createContext = computed(() => getStateContext('create'));
const editContext = computed(() => getStateContext('edit'));

const listFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(listContext.value?.fieldConfigs || {}, CONFIG_FORM_COMPONENT, configFieldFallbackMap);
});

const listButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(listContext.value?.fieldConfigs || {}, configButtonFallbackMap);
});

const createButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(createContext.value?.fieldConfigs || {}, configButtonFallbackMap);
});

const editButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(editContext.value?.fieldConfigs || {}, configButtonFallbackMap);
});

const formFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(formContext.value?.fieldConfigs || {}, CONFIG_FORM_COMPONENT, configFieldFallbackMap);
});

const createButtonConfig = computed(() => createButtonConfigMap.value.create);
const editButtonConfig = computed(() => editButtonConfigMap.value.modify);

const getList = async () => {
  loading.value = true;
  try {
    const res = await getConfigPage({
      configKey: queryParams.configKey || undefined,
      configName: queryParams.configName || undefined,
      configType: queryParams.configType || undefined,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    });
    configList.value = res.data.list || [];
    total.value = res.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  void getList();
};

const resetQuery = () => {
  queryParams.configName = '';
  queryParams.configKey = '';
  queryParams.configType = '';
  queryParams.remarkKeyword = '';
  handleQuery();
};

const handlePageSizeChange = () => {
  queryParams.pageNum = 1;
  void getList();
};

const handleSelectionChange = (selection: SysConfig[]) => {
  selectedRows.value = selection;
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
};

const handleAdd = async () => {
  if (!createButtonConfig.value.visible || createButtonConfig.value.disabled) {
    return;
  }
  formMode.value = 'create';
  formContext.value = await setActiveStateContext('create');
  formInitial.value = {
    configKey: '',
    configName: '',
    configType: 'N',
    configValue: '',
    note: ''
  };
  formVisible.value = true;
};

const handleUpdate = async (row?: SysConfig) => {
  if (!editButtonConfig.value.visible || editButtonConfig.value.disabled) {
    return;
  }
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  loading.value = true;
  try {
    const [detailRes, context] = await Promise.all([
      getConfigDetail(currentRow.id),
      setActiveStateContext('edit')
    ]);
    formMode.value = 'edit';
    formContext.value = context;
    formInitial.value = { ...detailRes.data };
    formVisible.value = true;
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (row?: SysConfig) => {
  if (!listButtonConfigMap.value.remove.visible || listButtonConfigMap.value.remove.disabled) {
    return;
  }
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map((item) => item.configName).join('、');
  await ElMessageBox.confirm(`确认要删除参数"${names}"吗？`, '警告', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  for (const item of rows) {
    await deleteConfig(item.id);
  }

  ElMessage.success('删除成功');
  if (configList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const handleRefreshCache = async () => {
  if (!listButtonConfigMap.value.refreshCache.visible || listButtonConfigMap.value.refreshCache.disabled) {
    return;
  }
  await ElMessageBox.confirm('确认要刷新参数缓存吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });
  await refreshConfigCache();
  ElMessage.success('刷新缓存成功');
};

const pickWritableFormValue = <T,>(fieldCode: ConfigFieldCode, value: T | undefined) => {
  const fieldConfig = formFieldConfigMap.value[fieldCode];
  if (!fieldConfig.visible || !fieldConfig.editable) {
    return undefined;
  }
  return value;
};

const handleFormSubmit = async (data: Partial<SysConfig>) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'create') {
      await createConfig({
        configKey: pickWritableFormValue('configKey', data.configKey) || '',
        configName: pickWritableFormValue('configName', data.configName) || '',
        configType: pickWritableFormValue('configType', data.configType) || 'N',
        configValue: pickWritableFormValue('configValue', data.configValue || undefined),
        note: pickWritableFormValue('note', data.note || undefined)
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateConfig({
        id: data.id,
        configKey: pickWritableFormValue('configKey', data.configKey) || '',
        configName: pickWritableFormValue('configName', data.configName) || '',
        configType: pickWritableFormValue('configType', data.configType) || 'N',
        configValue: pickWritableFormValue('configValue', data.configValue || undefined),
        note: pickWritableFormValue('note', data.note || undefined)
      });
      ElMessage.success('修改成功');
    }
    formVisible.value = false;
    await getList();
  } finally {
    submitLoading.value = false;
  }
};

const handleFormCancel = () => {
  formVisible.value = false;
};

onMounted(() => {
  void Promise.allSettled([
    loadListContext(),
    preloadStateContexts(['create', 'edit']),
    getList()
  ]);
});
</script>