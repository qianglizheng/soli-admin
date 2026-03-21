<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="角色名称" prop="roleName">
        <el-input
          v-model="queryParams.roleName"
          placeholder="请输入角色名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="权限字符" prop="roleKey">
        <el-input
          v-model="queryParams.roleKey"
          placeholder="请输入权限字符"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="角色状态" clearable style="width: 240px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮区域 -->
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
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 表格区域 -->
    <el-table v-loading="loading" :data="roleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="角色编号" prop="roleId" width="120" />
      <el-table-column label="角色名称" prop="roleName" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="权限字符" prop="roleKey" :show-overflow-tooltip="true" width="150" />
      <el-table-column label="显示顺序" prop="roleSort" width="100" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-switch
            :model-value="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusSwitchChange(scope.row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" fixed="right" min-width="260" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
          <el-button link type="primary" icon="CircleCheck" @click="handleDataScope(scope.row)">数据权限</el-button>
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

    <role-form
      v-model="formVisible"
      :mode="formMode"
      :initial-data="formInitial"
      :submitting="submitLoading"
      @submit="handleFormSubmit"
      @cancel="handleFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox, type FormInstance } from 'element-plus';
import { createRole, deleteRole, getRolePage, updateRole, type CreateRolePayload } from '@/api/role';
import RoleForm from './components/RoleForm.vue';

defineOptions({
  name: 'SystemRole'
});

interface RoleRow {
  roleId: number;
  roleName: string;
  roleKey: string;
  roleSort: string;
  dataScope: string;
  status: string;
  createTime?: string;
}

interface RoleFormData {
  roleId?: number;
  roleName: string;
  roleKey: string;
  roleSort: string;
  dataScope: string;
  status: string;
}

const queryRef = ref<FormInstance>();
const showSearch = ref(true);
const loading = ref(false);
const submitLoading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const roleList = ref<RoleRow[]>([]);
const selectedRows = ref<RoleRow[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<RoleFormData>>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: ''
});

const createDefaultForm = (): RoleFormData => ({
  roleName: '',
  roleKey: '',
  roleSort: '1',
  dataScope: '1',
  status: '0'
});

const mapRoleRow = (item: any): RoleRow => ({
  roleId: item.id,
  roleName: item.name,
  roleKey: item.code,
  roleSort: item.sort,
  dataScope: item.dataScope || '1',
  status: item.status || '0',
  createTime: item.createTime
});

const toFormData = (row: RoleRow): RoleFormData => ({
  roleId: row.roleId,
  roleName: row.roleName,
  roleKey: row.roleKey,
  roleSort: row.roleSort,
  dataScope: row.dataScope,
  status: row.status
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getRolePage({ ...queryParams });
    roleList.value = (res.data.list || []).map(mapRoleRow);
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
  queryParams.roleName = '';
  queryParams.roleKey = '';
  queryParams.status = '';
  handleQuery();
};

const handlePageSizeChange = () => {
  queryParams.pageNum = 1;
  void getList();
};

const handleSelectionChange = (selection: RoleRow[]) => {
  selectedRows.value = selection;
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleStatusChange = async (row: RoleRow, value: string) => {
  const previousStatus = row.status;
  const text = value === '0' ? '启用' : '停用';
  try {
    await ElMessageBox.confirm(`确认要${text}角色“${row.roleName}”吗？`, '警告', {
      cancelButtonText: '取消',
      confirmButtonText: '确定',
      type: 'warning'
    });
    row.status = value;
    await updateRole({
      id: row.roleId,
      name: row.roleName,
      code: row.roleKey,
      sort: row.roleSort,
      dataScope: row.dataScope,
      status: value
    });
    ElMessage.success(`${text}成功`);
  } catch {
    row.status = previousStatus;
  }
};

const handleStatusSwitchChange = (row: RoleRow, value: string | number | boolean) => {
  void handleStatusChange(row, String(value));
};

const handleAdd = () => {
  formMode.value = 'create';
  formInitial.value = createDefaultForm();
  formVisible.value = true;
};

const handleUpdate = (row?: RoleRow) => {
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  formMode.value = 'edit';
  formInitial.value = toFormData(currentRow);
  formVisible.value = true;
};

const handleFormSubmit = async (data: RoleFormData) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'edit' && data.roleId) {
      await updateRole({
        id: data.roleId,
        name: data.roleName,
        code: data.roleKey,
        sort: data.roleSort,
        dataScope: data.dataScope,
        status: data.status
      });
      ElMessage.success('修改成功');
    } else {
      const payload: CreateRolePayload = {
        name: data.roleName,
        code: data.roleKey,
        sort: data.roleSort,
        dataScope: data.dataScope,
        status: data.status
      };
      await createRole(payload);
      ElMessage.success('新增成功');
    }
    formVisible.value = false;
    await getList();
  } finally {
    submitLoading.value = false;
  }
};

const handleDelete = async (row?: RoleRow) => {
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map(item => item.roleName).join('、');
  await ElMessageBox.confirm(`确认要删除角色“${names}”吗？`, '警告', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
    type: 'warning'
  });

  for (const item of rows) {
    await deleteRole(item.roleId);
  }

  ElMessage.success('删除成功');
  if (roleList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const handleFormCancel = () => {
  formVisible.value = false;
};

const handleExport = () => {
  ElMessage.info('导出功能暂未实现');
};

const handleDataScope = (row?: RoleRow) => {
  ElMessage.info(`数据权限功能暂未实现${row ? `：${row.roleName}` : ''}`);
};

onMounted(() => {
  void getList();
});
</script>
