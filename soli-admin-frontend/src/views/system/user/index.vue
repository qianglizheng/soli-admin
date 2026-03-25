<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item label="用户名称" prop="username" data-search-item="true">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名称"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone" data-search-item="true">
          <el-input
            v-model="queryParams.phone"
            placeholder="请输入手机号码"
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
        >
          <el-select v-model="queryParams.status" placeholder="请选择用户状态" clearable style="width: 240px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item
          label="用户昵称"
          prop="nicknameKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.nicknameKeyword"
            placeholder="请输入用户昵称"
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

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="id" width="120" />
      <el-table-column label="用户名称" align="center" prop="username" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="用户昵称" align="center" prop="nickname" :show-overflow-tooltip="true" min-width="140" />
      <el-table-column label="手机号码" align="center" prop="phone" width="140" />
      <el-table-column label="用户邮箱" align="center" prop="email" :show-overflow-tooltip="true" min-width="180" />
      <el-table-column label="账号类型" align="center" width="120">
        <template #default="{ row }">
          <el-tag :type="row.type === '0' ? 'danger' : 'info'">
            {{ row.type === '0' ? '超级管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="100">
        <template #default="{ row }">
          <el-switch
            :model-value="row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusSwitchChange(row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" fixed="right" min-width="260" class-name="small-padding fixed-width">
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

    <user-form
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
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createUser,
  deleteUser,
  getUserDetail,
  getUserPage,
  updateUser,
  type CreateUserPayload
} from '@/api/user';
import type { SysUser } from '@/types/global';
import UserForm, { type UserFormModel } from './components/UserForm.vue';
import { useSearchCollapse } from '@/utils/useSearchCollapse';

defineOptions({
  name: 'SystemUser'
});

const showSearch = ref(true);
const {
  searchCollapseRef,
  isSearchMeasured,
  showMoreButton,
  showMoreSearch,
  toggleMoreSearch,
} = useSearchCollapse(showSearch);
const loading = ref(false);
const submitLoading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const userList = ref<SysUser[]>([]);
const selectedRows = ref<SysUser[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<UserFormModel>>({});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: '',
  nicknameKeyword: ''
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getUserPage({
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      username: queryParams.username,
      phone: queryParams.phone,
      status: queryParams.status
    });
    userList.value = res.data.list || [];
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
  queryParams.username = '';
  queryParams.phone = '';
  queryParams.status = '';
  queryParams.nicknameKeyword = '';
  handleQuery();
};

const handlePageSizeChange = () => {
  queryParams.pageNum = 1;
  void getList();
};

const handleSelectionChange = (selection: SysUser[]) => {
  selectedRows.value = selection;
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
};

const handleAdd = () => {
  formMode.value = 'create';
  formInitial.value = {
    username: '',
    password: '',
    nickname: '',
    email: '',
    phone: '',
    sex: '0',
    type: '1',
    status: '0'
  };
  formVisible.value = true;
};

const handleUpdate = async (row?: SysUser) => {
  const currentRow = row || selectedRows.value[0];
  if (!currentRow) {
    return;
  }
  loading.value = true;
  try {
    const detailRes = await getUserDetail(currentRow.id);
    const detail = detailRes.data;
    formMode.value = 'edit';
    formInitial.value = {
      id: detail.id,
      username: detail.username || '',
      password: '',
      nickname: detail.nickname || '',
      email: detail.email || '',
      phone: detail.phone || '',
      sex: detail.sex || '0',
      type: detail.type || '1',
      status: detail.status || '0'
    };
    formVisible.value = true;
  } finally {
    loading.value = false;
  }
};

const handleFormSubmit = async (data: UserFormModel) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'edit' && data.id) {
      await updateUser({
        id: data.id,
        nickname: data.nickname || undefined,
        email: data.email || undefined,
        phone: data.phone || undefined,
        sex: data.sex,
        type: data.type,
        status: data.status
      });
      ElMessage.success('修改成功');
    } else {
      const payload: CreateUserPayload = {
        username: data.username,
        password: data.password,
        nickname: data.nickname || undefined,
        email: data.email || undefined,
        phone: data.phone || undefined,
        sex: data.sex,
        type: data.type,
        status: data.status
      };
      await createUser(payload);
      ElMessage.success('新增成功');
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

const handleDelete = async (row?: SysUser) => {
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map(item => item.username).join('、');
  await ElMessageBox.confirm(`是否确认删除用户名称为“${names}”的数据项？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  });
  for (const item of rows) {
    await deleteUser(item.id);
  }
  ElMessage.success('删除成功');
  if (userList.value.length === rows.length && queryParams.pageNum > 1) {
    queryParams.pageNum -= 1;
  }
  await getList();
};

const handleStatusChange = async (row: SysUser, value: string) => {
  const previousStatus = row.status || '0';
  row.status = value;
  try {
    await updateUser({
      id: row.id,
      nickname: row.nickname,
      email: row.email,
      phone: row.phone,
      sex: row.sex || '0',
      type: row.type || '1',
      status: value
    });
    ElMessage.success(value === '0' ? '启用成功' : '停用成功');
  } catch {
    row.status = previousStatus;
  }
};

const handleStatusSwitchChange = (row: SysUser, value: string | number | boolean) => {
  void handleStatusChange(row, String(value));
};

onMounted(() => {
  void getList();
});
</script>
