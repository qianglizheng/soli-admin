<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch">
      <el-form-item label="字典名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入字典名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="type">
        <el-input
          v-model="queryParams.type"
          placeholder="请输入字典类型"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="字典状态" clearable style="width: 240px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
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

    <el-table v-loading="loading" :data="dictList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" prop="id" width="180" />
      <el-table-column label="字典名称" prop="name" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="字典类型" min-width="180">
        <template #default="scope">
          <button class="link-type" type="button" @click="handleData(scope.row)">
            {{ scope.row.type }}
          </button>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="note" :show-overflow-tooltip="true" min-width="200" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="260" fixed="right" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Tickets" @click="handleData(scope.row)">数据</el-button>
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

    <dict-type-form
      v-model="formVisible"
      :mode="formMode"
      :initialData="formInitial"
      @submit="onFormSubmit"
      @cancel="onFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import { createDict, deleteDict, getDictPage, updateDict } from '@/api/dict';
import type { SysDictType } from '@/types/global';
import DictTypeForm from './components/DictTypeForm.vue';

defineOptions({
  name: 'SystemDict'
});

const router = useRouter();
const showSearch = ref(true);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dictList = ref<SysDictType[]>([]);
const selectedRows = ref<SysDictType[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<SysDictType>>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: '',
  type: ''
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getDictPage({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      name: queryParams.name || undefined,
      status: queryParams.status || undefined,
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

const handleAdd = () => {
  formMode.value = 'create';
  formInitial.value = {};
  formVisible.value = true;
};

const handleUpdate = (row?: SysDictType) => {
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = { ...currentRow };
  formVisible.value = true;
};

const handleData = (row: SysDictType) => {
  router.push(`/system/dict/${row.id}/data`);
};

const handleDelete = async (row?: SysDictType) => {
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map(item => item.name).join('、');
  await ElMessageBox.confirm(`确认要删除字典"${names}"吗？`, '警告', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  for (const item of rows) {
    await deleteDict(item.id);
  }

  ElMessage.success('删除成功');
  if (dictList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const onFormSubmit = async (data: Partial<SysDictType>) => {
  loading.value = true;
  try {
    if (formMode.value === 'create') {
      await createDict({
        name: data.name || '',
        note: data.note,
        status: data.status,
        type: data.type || ''
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateDict({
        id: data.id,
        name: data.name || '',
        note: data.note,
        status: data.status,
        type: data.type || ''
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

onMounted(() => {
  void getList();
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
