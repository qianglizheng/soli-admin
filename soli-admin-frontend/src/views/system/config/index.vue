<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch">
      <el-form-item label="参数名称" prop="configName">
        <el-input
          v-model="queryParams.configName"
          placeholder="请输入参数名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input
          v-model="queryParams.configKey"
          placeholder="请输入参数键名"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="请选择系统内置" clearable style="width: 240px">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
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
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参数编号" prop="id" width="180" />
      <el-table-column label="参数名称" prop="configName" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="参数键名" prop="configKey" min-width="180" :show-overflow-tooltip="true" />
      <el-table-column label="参数键值" prop="configValue" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="系统内置" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.configType === 'Y' ? 'danger' : 'info'">
            {{ row.configType === 'Y' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" prop="note" min-width="220" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" width="180" fixed="right" class-name="small-padding fixed-width">
        <template #default="{ row }">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(row)">删除</el-button>
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
      :initial-data="formInitial"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createConfig,
  deleteConfig,
  getConfigDetail,
  getConfigPage,
  refreshConfigCache,
  updateConfig
} from '@/api/config';
import type { SysConfig } from '@/types/global';
import ConfigForm from './ConfigForm.vue';

defineOptions({
  name: 'SystemConfig'
});

const showSearch = ref(true);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const configList = ref<SysConfig[]>([]);
const selectedRows = ref<SysConfig[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<SysConfig>>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: '',
  configKey: '',
  configType: ''
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getConfigPage({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      configKey: queryParams.configKey || undefined,
      configName: queryParams.configName || undefined,
      configType: queryParams.configType || undefined
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

const handleAdd = () => {
  formMode.value = 'create';
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
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  loading.value = true;
  try {
    const res = await getConfigDetail(currentRow.id);
    formMode.value = 'edit';
    formInitial.value = { ...res.data };
    formVisible.value = true;
  } finally {
    loading.value = false;
  }
};

const handleDelete = async (row?: SysConfig) => {
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map(item => item.configName).join('、');
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
  await ElMessageBox.confirm('确认要刷新参数缓存吗？', '提示', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });
  await refreshConfigCache();
  ElMessage.success('刷新缓存成功');
};

const handleFormSubmit = async (data: Partial<SysConfig>) => {
  loading.value = true;
  try {
    if (formMode.value === 'create') {
      await createConfig({
        configKey: data.configKey || '',
        configName: data.configName || '',
        configType: data.configType || 'N',
        configValue: data.configValue,
        note: data.note
      });
      ElMessage.success('新增成功');
    } else if (data.id) {
      await updateConfig({
        id: data.id,
        configKey: data.configKey || '',
        configName: data.configName || '',
        configType: data.configType || 'N',
        configValue: data.configValue,
        note: data.note
      });
      ElMessage.success('修改成功');
    }
    await getList();
  } finally {
    loading.value = false;
  }
};

const handleFormCancel = () => {
  formVisible.value = false;
};

onMounted(() => {
  void getList();
});
</script>
