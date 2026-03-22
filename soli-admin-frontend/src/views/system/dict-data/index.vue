<template>
  <div class="app-container">
    <div class="dict-header">
      <div>
        <div class="dict-title">{{ dictInfo?.name || '字典数据' }}</div>
        <div class="dict-subtitle">字典类型：{{ dictInfo?.type || '-' }}</div>
      </div>
      <el-button icon="ArrowLeft" @click="handleBack">返回</el-button>
    </div>

    <el-form :model="queryParams" :inline="true" v-show="showSearch">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item label="字典标签" prop="label" data-search-item="true">
          <el-input
            v-model="queryParams.label"
            placeholder="请输入字典标签"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="字典键值" prop="value" data-search-item="true">
          <el-input
            v-model="queryParams.value"
            placeholder="请输入字典键值"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          label="状态"
          prop="status"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-select v-model="queryParams.status" placeholder="数据状态" clearable style="width: 240px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item
          label="回显样式"
          prop="listClassKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.listClassKeyword"
            placeholder="请输入回显样式"
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
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()">删除</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dictDataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据编号" prop="id" width="180" />
      <el-table-column label="字典标签" prop="label" min-width="150" :show-overflow-tooltip="true" />
      <el-table-column label="字典键值" prop="value" min-width="140" :show-overflow-tooltip="true" />
      <el-table-column label="排序" prop="sort" align="center" width="90" />
      <el-table-column label="默认" align="center" width="90">
        <template #default="scope">
          <el-tag :type="scope.row.defaultFlag === 'Y' ? 'success' : 'info'">
            {{ scope.row.defaultFlag === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="回显样式" prop="listClass" min-width="120" />
      <el-table-column label="备注" prop="note" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180" />
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
      :initialData="formInitial"
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
  getDictDataPage,
  getDictDetail,
  updateDictData
} from '@/api/dict';
import type { SysDictData, SysDictType } from '@/types/global';
import DictDataForm from './components/DictDataForm.vue';
import { useSearchCollapse } from '@/utils/useSearchCollapse';

defineOptions({
  name: 'SystemDictData'
});

const route = useRoute();
const router = useRouter();
const showSearch = ref(true);
const {
  searchCollapseRef,
  isSearchMeasured,
  showMoreButton,
  showMoreSearch,
  toggleMoreSearch,
} = useSearchCollapse(showSearch);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dictInfo = ref<SysDictType | null>(null);
const dictDataList = ref<SysDictData[]>([]);
const selectedRows = ref<SysDictData[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<SysDictData>>({});

const currentDictId = computed(() => Number(route.params.dictId));

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  label: '',
  status: '',
  value: '',
  listClassKeyword: ''
});

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
      status: queryParams.status || undefined,
      value: queryParams.value || undefined
    });
    dictDataList.value = res.data.list || [];
    total.value = res.data.total || 0;
  } finally {
    loading.value = false;
  }
};

const loadPage = async () => {
  await loadDictInfo();
  await getList();
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

const handleAdd = () => {
  formMode.value = 'create';
  formInitial.value = {
    dictTypeId: currentDictId.value
  };
  formVisible.value = true;
};

const handleUpdate = (row?: SysDictData) => {
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = { ...currentRow };
  formVisible.value = true;
};

const handleDelete = async (row?: SysDictData) => {
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const labels = rows.map(item => item.label).join('、');
  await ElMessageBox.confirm(`确认要删除字典数据"${labels}"吗？`, '警告', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  for (const item of rows) {
    await deleteDictData(item.id);
  }

  ElMessage.success('删除成功');
  if (dictDataList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const onFormSubmit = async (data: Partial<SysDictData>) => {
  loading.value = true;
  try {
    if (formMode.value === 'create') {
      await createDictData({
        cssClass: data.cssClass,
        dictTypeId: currentDictId.value,
        defaultFlag: data.defaultFlag,
        label: data.label || '',
        listClass: data.listClass,
        note: data.note,
        sort: data.sort,
        status: data.status,
        value: data.value || ''
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateDictData({
        cssClass: data.cssClass,
        dictTypeId: currentDictId.value,
        id: data.id,
        defaultFlag: data.defaultFlag,
        label: data.label || '',
        listClass: data.listClass,
        note: data.note,
        sort: data.sort,
        status: data.status,
        value: data.value || ''
      });
      ElMessage.success('修改成功');
    }
    await getList();
  } finally {
    loading.value = false;
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
