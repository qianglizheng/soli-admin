<template>
  <div class="app-container">
    <div class="dict-header">
      <div>
        <div class="dict-title">{{ dictInfo?.name || '字典数据' }}</div>
        <div class="dict-subtitle">字典类型：{{ dictInfo?.type || '-' }}</div>
      </div>
      <el-button icon="ArrowLeft" @click="handleBack">返回</el-button>
    </div>

    <el-form v-show="showSearch" :model="queryParams" :inline="true">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item
          v-if="listFieldConfigMap.label.visible"
          :label="listFieldConfigMap.label.label"
          prop="label"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.label"
            :placeholder="listFieldConfigMap.label.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.value.visible"
          :label="listFieldConfigMap.value.label"
          prop="value"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.value"
            :placeholder="listFieldConfigMap.value.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.status.visible"
          :label="listFieldConfigMap.status.label"
          prop="status"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-select
            v-model="queryParams.status"
            :placeholder="listFieldConfigMap.status.placeholder"
            clearable
            style="width: 240px"
          >
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.listClass.visible"
          :label="listFieldConfigMap.listClass.label"
          prop="listClassKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.listClassKeyword"
            :placeholder="listFieldConfigMap.listClass.placeholder"
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
            <el-icon class="el-icon--right">
              <component :is="showMoreSearch ? 'ArrowUp' : 'ArrowDown'" />
            </el-icon>
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
    </el-row>

    <el-table v-loading="loading" :data="dictDataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据编号" prop="id" width="180" />
      <el-table-column
        v-if="listFieldConfigMap.label.visible"
        :label="listFieldConfigMap.label.label"
        prop="label"
        min-width="150"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-if="listFieldConfigMap.value.visible"
        :label="listFieldConfigMap.value.label"
        prop="value"
        min-width="140"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        v-if="listFieldConfigMap.sort.visible"
        :label="listFieldConfigMap.sort.label"
        prop="sort"
        align="center"
        width="90"
      />
      <el-table-column
        v-if="listFieldConfigMap.defaultFlag.visible"
        :label="listFieldConfigMap.defaultFlag.label"
        align="center"
        width="90"
      >
        <template #default="{ row }">
          <el-tag :type="getEnumCode(row.defaultFlag) === 'Y' ? 'success' : 'info'">
            {{ getEnumName(row.defaultFlag) || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-if="listFieldConfigMap.status.visible"
        :label="listFieldConfigMap.status.label"
        align="center"
        width="100"
      >
        <template #default="{ row }">
          <el-tag :type="getEnumCode(row.status) === '0' ? 'success' : 'danger'">
            {{ getEnumName(row.status) || '-' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-if="listFieldConfigMap.listClass.visible"
        :label="listFieldConfigMap.listClass.label"
        prop="listClass"
        min-width="120"
      />
      <el-table-column
        v-if="listFieldConfigMap.note.visible"
        :label="listFieldConfigMap.note.label"
        prop="note"
        min-width="180"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width">
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

    <dict-data-form
      v-model="formVisible"
      :mode="formMode"
      :context="formContext"
      :initial-data="formInitial"
      :submitting="submitLoading"
      @submit="onFormSubmit"
      @cancel="onFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createDictData,
  deleteDictData,
  getDictDataModuleContext,
  getDictDataPage,
  getDictDetail,
  updateDictData,
  type DictDataPageQuery
} from '@/api/dict';
import { useStatefulModuleContext } from '@/composables/useStatefulModuleContext';
import type { SysDictData, SysDictType } from '@/types/global';
import { getEnumCode, getEnumName } from '@/utils/enum';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  pickWritableModuleValue,
  type ModuleButtonFallback,
  type ModuleFieldFallback
} from '@/utils/moduleContext';
import { useSearchCollapse } from '@/utils/useSearchCollapse';
import DictDataForm, { type DictDataFormModel } from './components/DictDataForm.vue';

defineOptions({
  name: 'SystemDictData'
});

type DictDataFieldCode = 'label' | 'value' | 'sort' | 'listClass' | 'cssClass' | 'defaultFlag' | 'status' | 'note';
type DictDataButtonCode = 'create' | 'modify' | 'remove';

const DICT_DATA_FORM_COMPONENT = 'dict_data_form';

type DictDataQueryParams = Omit<DictDataPageQuery, 'dictTypeId' | 'status'> & {
  listClassKeyword: string;
  status: DictDataPageQuery['status'] | '';
};

const dictDataFieldFallbackMap: Record<DictDataFieldCode, ModuleFieldFallback> = {
  cssClass: { label: '样式属性', placeholder: '请输入样式属性', required: false, visible: true },
  defaultFlag: { label: '是否默认', placeholder: '请选择是否默认', required: true, visible: true },
  label: { label: '字典标签', placeholder: '请输入字典标签', required: true, visible: true },
  listClass: { label: '回显样式', placeholder: '请输入回显样式', required: false, visible: true },
  note: { label: '备注', placeholder: '请输入备注', required: false, visible: true },
  sort: { label: '显示排序', placeholder: '请输入显示排序', required: false, visible: true },
  status: { label: '状态', placeholder: '请选择状态', required: true, visible: true },
  value: { label: '字典键值', placeholder: '请输入字典键值', required: true, visible: true }
};

const dictDataButtonFallbackMap: Record<DictDataButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  remove: { disabled: false, label: '删除', visible: true }
};

const route = useRoute();
const router = useRouter();
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
const dictInfo = ref<SysDictType | null>(null);
const dictDataList = ref<SysDictData[]>([]);
const selectedRows = ref<SysDictData[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<DictDataFormModel>>({});

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
      const { data } = await getDictDataModuleContext(stateCode);
      return data;
    } catch {
      return null;
    }
  }
});

const currentDictId = computed(() => Number(route.params.dictId));

const queryParams = reactive<DictDataQueryParams>({
  pageNum: 1,
  pageSize: 10,
  label: '',
  status: '',
  value: '',
  listClassKeyword: ''
});

const createContext = computed(() => getStateContext('create'));
const editContext = computed(() => getStateContext('edit'));

const listFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(listContext.value?.fieldConfigs || {}, DICT_DATA_FORM_COMPONENT, dictDataFieldFallbackMap);
});

const listButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(listContext.value?.fieldConfigs || {}, dictDataButtonFallbackMap);
});

const createButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(createContext.value?.fieldConfigs || {}, dictDataButtonFallbackMap);
});

const editButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(editContext.value?.fieldConfigs || {}, dictDataButtonFallbackMap);
});

const formFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(formContext.value?.fieldConfigs || {}, DICT_DATA_FORM_COMPONENT, dictDataFieldFallbackMap);
});

const createButtonConfig = computed(() => createButtonConfigMap.value.create);
const editButtonConfig = computed(() => editButtonConfigMap.value.modify);

const loadDictInfo = async () => {
  if (!Number.isFinite(currentDictId.value)) {
    dictInfo.value = null;
    return;
  }
  const res = await getDictDetail(currentDictId.value);
  dictInfo.value = res.data;
};

const getList = async () => {
  if (!Number.isFinite(currentDictId.value)) {
    dictDataList.value = [];
    total.value = 0;
    return;
  }
  loading.value = true;
  try {
    const res = await getDictDataPage({
      dictTypeId: currentDictId.value,
      label: queryParams.label || undefined,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      status: queryParams.status === '' ? undefined : queryParams.status,
      value: queryParams.value || undefined
    });
    dictDataList.value = res.data.list || [];
    total.value = res.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const loadPage = async () => {
  await Promise.allSettled([
    loadDictInfo(),
    loadListContext(),
    preloadStateContexts(['create', 'edit']),
    getList()
  ]);
};

const handleQuery = () => {
  queryParams.pageNum = 1;
  void getList();
};

const resetQuery = () => {
  queryParams.label = '';
  queryParams.status = '';
  queryParams.value = '';
  queryParams.listClassKeyword = '';
  handleQuery();
};

const handlePageSizeChange = () => {
  queryParams.pageNum = 1;
  void getList();
};

const handleSelectionChange = (selection: SysDictData[]) => {
  selectedRows.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleAdd = async () => {
  if (!createButtonConfig.value.visible || createButtonConfig.value.disabled) {
    return;
  }
  formMode.value = 'create';
  formContext.value = await setActiveStateContext('create');
  formInitial.value = {
    cssClass: '',
    defaultFlag: 'N',
    dictTypeId: currentDictId.value,
    label: '',
    listClass: '',
    note: '',
    sort: '0',
    status: '0',
    value: ''
  };
  formVisible.value = true;
};

const handleUpdate = async (row?: SysDictData) => {
  if (!editButtonConfig.value.visible || editButtonConfig.value.disabled) {
    return;
  }
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  formMode.value = 'edit';
  formContext.value = await setActiveStateContext('edit');
  formInitial.value = {
    id: currentRow.id,
    dictTypeId: currentRow.dictTypeId,
    label: currentRow.label,
    value: currentRow.value,
    sort: currentRow.sort || '0',
    cssClass: currentRow.cssClass || '',
    listClass: currentRow.listClass || '',
    defaultFlag: getEnumCode(currentRow.defaultFlag) || 'N',
    status: getEnumCode(currentRow.status) || '0',
    note: currentRow.note || ''
  };
  formVisible.value = true;
};

const handleDelete = async (row?: SysDictData) => {
  if (!listButtonConfigMap.value.remove.visible || listButtonConfigMap.value.remove.disabled) {
    return;
  }
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const labels = rows.map((item) => item.label).join('、');
  try {
    await ElMessageBox.confirm(`确认要删除字典数据“${labels}”吗？`, '警告', {
      cancelButtonText: '取消',
      confirmButtonText: '确定',
      type: 'warning'
    });
  } catch {
    return;
  }
  await Promise.all(rows.map((item) => deleteDictData(item.id)));
  ElMessage.success('删除成功');
  if (dictDataList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const pickWritableFormValue = <T,>(fieldCode: DictDataFieldCode, value: T | undefined) => {
  return pickWritableModuleValue(formFieldConfigMap.value, fieldCode, value);
};

const onFormSubmit = async (data: DictDataFormModel) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'create') {
      await createDictData({
        cssClass: pickWritableFormValue('cssClass', data.cssClass || undefined),
        defaultFlag: pickWritableFormValue('defaultFlag', data.defaultFlag) || 'N',
        dictTypeId: currentDictId.value,
        label: pickWritableFormValue('label', data.label) || '',
        listClass: pickWritableFormValue('listClass', data.listClass || undefined),
        note: pickWritableFormValue('note', data.note || undefined),
        sort: pickWritableFormValue('sort', data.sort || undefined),
        status: pickWritableFormValue('status', data.status) || '0',
        value: pickWritableFormValue('value', data.value) || ''
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateDictData({
        cssClass: pickWritableFormValue('cssClass', data.cssClass || undefined),
        defaultFlag: pickWritableFormValue('defaultFlag', data.defaultFlag) || 'N',
        dictTypeId: currentDictId.value,
        id: data.id,
        label: pickWritableFormValue('label', data.label) || '',
        listClass: pickWritableFormValue('listClass', data.listClass || undefined),
        note: pickWritableFormValue('note', data.note || undefined),
        sort: pickWritableFormValue('sort', data.sort || undefined),
        status: pickWritableFormValue('status', data.status) || '0',
        value: pickWritableFormValue('value', data.value) || ''
      });
      ElMessage.success('修改成功');
    }
    formVisible.value = false;
    await getList();
  } finally {
    submitLoading.value = false;
  }
};

const onFormCancel = () => {
  formVisible.value = false;
};

const handleBack = () => {
  router.push('/system/dict');
};

watch(
  () => route.params.dictId,
  () => {
    queryParams.pageNum = 1;
    queryParams.label = '';
    queryParams.status = '';
    queryParams.value = '';
    queryParams.listClassKeyword = '';
    void loadPage();
  }
);

onMounted(() => {
  void loadPage();
});
</script>

<style scoped>
.dict-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 18px 20px;
  border-radius: 6px;
  background: #f5f7fa;
}

.dict-title {
  margin-bottom: 6px;
  color: #303133;
  font-size: 18px;
  font-weight: 600;
}

.dict-subtitle {
  color: #606266;
  font-size: 13px;
}
</style>
