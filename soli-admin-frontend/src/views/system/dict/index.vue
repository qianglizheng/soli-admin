<template>
  <div class="app-container">
    <el-form v-show="showSearch" :model="queryParams" :inline="true">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item
          v-if="listFieldConfigMap.name.visible"
          :label="listFieldConfigMap.name.label"
          prop="name"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.name"
            :placeholder="listFieldConfigMap.name.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.type.visible"
          :label="listFieldConfigMap.type.label"
          prop="type"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.type"
            :placeholder="listFieldConfigMap.type.placeholder"
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
          v-if="listFieldConfigMap.note.visible"
          :label="listFieldConfigMap.note.label"
          prop="noteKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.noteKeyword"
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

    <el-table v-loading="loading" :data="dictList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" prop="id" width="180" />
      <el-table-column
        v-if="listFieldConfigMap.name.visible"
        :label="listFieldConfigMap.name.label"
        prop="name"
        :show-overflow-tooltip="true"
        min-width="180"
      />
      <el-table-column
        v-if="listFieldConfigMap.type.visible"
        :label="listFieldConfigMap.type.label"
        min-width="180"
      >
        <template #default="{ row }">
          <button class="link-type" type="button" @click="handleData(row)">
            {{ row.type }}
          </button>
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
        v-if="listFieldConfigMap.note.visible"
        :label="listFieldConfigMap.note.label"
        prop="note"
        :show-overflow-tooltip="true"
        min-width="200"
      />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column
        label="操作"
        align="center"
        fixed="right"
        min-width="260"
        class-name="small-padding fixed-width"
      >
        <template #default="{ row }">
          <el-button link type="primary" icon="Tickets" @click="handleData(row)">数据</el-button>
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

    <dict-type-form
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
import { computed, onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createDict, deleteDict, getDictModuleContext, getDictPage, updateDict, type DictPageQuery } from '@/api/dict';
import { useStatefulModuleContext } from '@/composables/useStatefulModuleContext';
import type { SysDictType } from '@/types/global';
import { getEnumCode, getEnumName } from '@/utils/enum';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  pickWritableModuleValue,
  type ModuleButtonFallback,
  type ModuleFieldFallback
} from '@/utils/moduleContext';
import { useSearchCollapse } from '@/utils/useSearchCollapse';
import DictTypeForm, { type DictTypeFormModel } from './components/DictTypeForm.vue';

defineOptions({
  name: 'SystemDict'
});

type DictFieldCode = 'name' | 'type' | 'status' | 'note';
type DictButtonCode = 'create' | 'modify' | 'remove';

const DICT_FORM_COMPONENT = 'dict_form';

type DictQueryParams = Omit<DictPageQuery, 'status'> & {
  noteKeyword: string;
  status: DictPageQuery['status'] | '';
};

const dictFieldFallbackMap: Record<DictFieldCode, ModuleFieldFallback> = {
  name: { label: '字典名称', placeholder: '请输入字典名称', required: true, visible: true },
  note: { label: '备注', placeholder: '请输入备注', required: false, visible: true },
  status: { label: '状态', placeholder: '请选择状态', required: true, visible: true },
  type: { label: '字典类型', placeholder: '请输入字典类型', required: true, visible: true }
};

const dictButtonFallbackMap: Record<DictButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  remove: { disabled: false, label: '删除', visible: true }
};

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
const dictList = ref<SysDictType[]>([]);
const selectedRows = ref<SysDictType[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<DictTypeFormModel>>({});

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
      const { data } = await getDictModuleContext(stateCode);
      return data;
    } catch {
      return null;
    }
  }
});

const queryParams = reactive<DictQueryParams>({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: '',
  type: '',
  noteKeyword: ''
});

const createContext = computed(() => getStateContext('create'));
const editContext = computed(() => getStateContext('edit'));

const listFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(listContext.value?.fieldConfigs || {}, DICT_FORM_COMPONENT, dictFieldFallbackMap);
});

const listButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(listContext.value?.fieldConfigs || {}, dictButtonFallbackMap);
});

const createButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(createContext.value?.fieldConfigs || {}, dictButtonFallbackMap);
});

const editButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(editContext.value?.fieldConfigs || {}, dictButtonFallbackMap);
});

const formFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(formContext.value?.fieldConfigs || {}, DICT_FORM_COMPONENT, dictFieldFallbackMap);
});

const createButtonConfig = computed(() => createButtonConfigMap.value.create);
const editButtonConfig = computed(() => editButtonConfigMap.value.modify);

const getList = async () => {
  loading.value = true;
  try {
    const res = await getDictPage({
      name: queryParams.name || undefined,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      status: queryParams.status === '' ? undefined : queryParams.status,
      type: queryParams.type || undefined
    });
    dictList.value = res.data.list || [];
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
  queryParams.name = '';
  queryParams.status = '';
  queryParams.type = '';
  queryParams.noteKeyword = '';
  handleQuery();
};

const handlePageSizeChange = () => {
  queryParams.pageNum = 1;
  void getList();
};

const handleSelectionChange = (selection: SysDictType[]) => {
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
    name: '',
    note: '',
    status: '0',
    type: ''
  };
  formVisible.value = true;
};

const handleUpdate = async (row?: SysDictType) => {
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
    name: currentRow.name,
    note: currentRow.note || '',
    status: getEnumCode(currentRow.status) || '0',
    type: currentRow.type
  };
  formVisible.value = true;
};

const handleData = (row: SysDictType) => {
  router.push(`/system/dict/${row.id}/data`);
};

const handleDelete = async (row?: SysDictType) => {
  if (!listButtonConfigMap.value.remove.visible || listButtonConfigMap.value.remove.disabled) {
    return;
  }
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map((item) => item.name).join('、');
  try {
    await ElMessageBox.confirm(`确认要删除字典“${names}”吗？`, '警告', {
      cancelButtonText: '取消',
      confirmButtonText: '确定',
      type: 'warning'
    });
  } catch {
    return;
  }
  await Promise.all(rows.map((item) => deleteDict(item.id)));
  ElMessage.success('删除成功');
  if (dictList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const pickWritableFormValue = <T,>(fieldCode: DictFieldCode, value: T | undefined) => {
  return pickWritableModuleValue(formFieldConfigMap.value, fieldCode, value);
};

const onFormSubmit = async (data: DictTypeFormModel) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'create') {
      await createDict({
        name: pickWritableFormValue('name', data.name) || '',
        note: pickWritableFormValue('note', data.note || undefined),
        status: pickWritableFormValue('status', data.status) || '0',
        type: pickWritableFormValue('type', data.type) || ''
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateDict({
        id: data.id,
        name: pickWritableFormValue('name', data.name) || '',
        note: pickWritableFormValue('note', data.note || undefined),
        status: pickWritableFormValue('status', data.status) || '0',
        type: pickWritableFormValue('type', data.type) || ''
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

onMounted(() => {
  void Promise.allSettled([
    loadListContext(),
    preloadStateContexts(['create', 'edit']),
    getList()
  ]);
});
</script>

<style scoped>
.link-type {
  padding: 0;
  border: 0;
  background: transparent;
  color: #409eff;
  cursor: pointer;
}

.link-type:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>
