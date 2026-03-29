<template>
  <div class="app-container">
    <el-form v-show="showSearch" :inline="true" :model="queryParams">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item
          v-if="listFieldConfigMap.username.visible"
          :label="listFieldConfigMap.username.label"
          prop="username"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.username"
            :placeholder="listFieldConfigMap.username.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item
          v-if="listFieldConfigMap.phone.visible"
          :label="listFieldConfigMap.phone.label"
          prop="phone"
          data-search-item="true"
        >
          <el-input
            v-model="queryParams.phone"
            :placeholder="listFieldConfigMap.phone.placeholder"
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
          v-if="listFieldConfigMap.nickname.visible"
          :label="listFieldConfigMap.nickname.label"
          prop="nicknameKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.nicknameKeyword"
            :placeholder="listFieldConfigMap.nickname.placeholder"
            clearable
            style="width: 240px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item data-search-actions="true">
          <el-button icon="Search" type="primary" @click="handleQuery">查询</el-button>
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
          plain
          icon="Plus"
          type="primary"
          :disabled="createButtonConfig.disabled"
          @click="handleAdd"
        >
          {{ createButtonConfig.label }}
        </el-button>
      </el-col>
      <el-col v-if="editButtonConfig.visible" :span="1.5">
        <el-button
          plain
          icon="Edit"
          type="success"
          :disabled="single || editButtonConfig.disabled"
          @click="handleUpdate()"
        >
          {{ editButtonConfig.label }}
        </el-button>
      </el-col>
      <el-col v-if="listButtonConfigMap.remove.visible" :span="1.5">
        <el-button
          plain
          icon="Delete"
          type="danger"
          :disabled="multiple || listButtonConfigMap.remove.disabled"
          @click="handleDelete()"
        >
          {{ listButtonConfigMap.remove.label }}
        </el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column align="center" label="用户编号" prop="id" width="120" />
      <el-table-column
        v-if="listFieldConfigMap.username.visible"
        align="center"
        :label="listFieldConfigMap.username.label"
        min-width="140"
        prop="username"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="listFieldConfigMap.nickname.visible"
        align="center"
        :label="listFieldConfigMap.nickname.label"
        min-width="140"
        prop="nickname"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="listFieldConfigMap.phone.visible"
        align="center"
        :label="listFieldConfigMap.phone.label"
        prop="phone"
        width="140"
      />
      <el-table-column
        v-if="listFieldConfigMap.email.visible"
        align="center"
        :label="listFieldConfigMap.email.label"
        min-width="180"
        prop="email"
        show-overflow-tooltip
      />
      <el-table-column
        v-if="listFieldConfigMap.type.visible"
        align="center"
        :label="listFieldConfigMap.type.label"
        width="120"
      >
        <template #default="{ row }">
          <el-tag :type="row.type === '0' ? 'danger' : 'info'">
            {{ row.type === '0' ? '超级管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-if="listFieldConfigMap.status.visible"
        align="center"
        :label="listFieldConfigMap.status.label"
        width="100"
      >
        <template #default="{ row }">
          <el-switch
            active-value="0"
            inactive-value="1"
            :disabled="isStatusSwitchDisabled()"
            :model-value="row.status"
            @change="handleStatusSwitchChange(row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建时间" prop="createTime" width="180" />
      <el-table-column
        v-if="showOperationColumn"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
        label="操作"
        min-width="260"
      >
        <template #default="{ row }">
          <el-button
            v-if="editButtonConfig.visible"
            link
            icon="Edit"
            type="primary"
            :disabled="editButtonConfig.disabled"
            @click="handleUpdate(row)"
          >
            {{ editButtonConfig.label }}
          </el-button>
          <el-button
            v-if="listButtonConfigMap.remove.visible"
            link
            icon="Delete"
            type="primary"
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
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="getList"
        @size-change="handlePageSizeChange"
      />
    </div>

    <user-form
      v-model="formVisible"
      :context="formContext"
      :initial-data="formInitial"
      :mode="formMode"
      :submitting="submitLoading"
      @cancel="handleFormCancel"
      @submit="handleFormSubmit"
    />
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  createUser,
  deleteUser,
  getUserDetail,
  getUserModuleContext,
  getUserPage,
  updateUser,
  type CreateUserPayload,
  type UpdateUserPayload
} from '@/api/user';
import { useStatefulModuleContext } from '@/composables/useStatefulModuleContext';
import type { SysUser } from '@/types/global';
import {
  buildResolvedButtonConfigMap,
  buildResolvedFieldConfigMap,
  pickWritableModuleValue
} from '@/utils/moduleContext';
import { useSearchCollapse } from '@/utils/useSearchCollapse';
import UserForm, { type UserFormModel } from './components/UserForm.vue';
import {
  USER_FORM_COMPONENT,
  userButtonFallbackMap,
  userFieldFallbackMap,
  type UserFieldCode
} from './moduleConfig';

defineOptions({
  name: 'SystemUser'
});

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
const userList = ref<SysUser[]>([]);
const selectedRows = ref<SysUser[]>([]);
const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<UserFormModel>>({});

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
      const { data } = await getUserModuleContext(stateCode);
      return data;
    } catch {
      return null;
    }
  }
});

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: '',
  nicknameKeyword: ''
});

const createContext = computed(() => getStateContext('create'));
const editContext = computed(() => getStateContext('edit'));

const listFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(listContext.value?.fieldConfigs || {}, USER_FORM_COMPONENT, userFieldFallbackMap);
});

const listButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(listContext.value?.fieldConfigs || {}, userButtonFallbackMap);
});

const editFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(editContext.value?.fieldConfigs || {}, USER_FORM_COMPONENT, userFieldFallbackMap);
});

const formFieldConfigMap = computed(() => {
  return buildResolvedFieldConfigMap(formContext.value?.fieldConfigs || {}, USER_FORM_COMPONENT, userFieldFallbackMap);
});

const createButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(createContext.value?.fieldConfigs || {}, userButtonFallbackMap);
});

const editButtonConfigMap = computed(() => {
  return buildResolvedButtonConfigMap(editContext.value?.fieldConfigs || {}, userButtonFallbackMap);
});

const createButtonConfig = computed(() => createButtonConfigMap.value.create);
const editButtonConfig = computed(() => editButtonConfigMap.value.modify);

const showOperationColumn = computed(() => {
  return editButtonConfig.value.visible || listButtonConfigMap.value.remove.visible;
});

const getList = async () => {
  loading.value = true;
  try {
    const res = await getUserPage({
      nickname: queryParams.nicknameKeyword || undefined,
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize,
      phone: queryParams.phone || undefined,
      status: queryParams.status || undefined,
      username: queryParams.username || undefined
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

const handleAdd = async () => {
  if (!createButtonConfig.value.visible || createButtonConfig.value.disabled) {
    return;
  }
  formMode.value = 'create';
  formContext.value = await setActiveStateContext('create');
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
      getUserDetail(currentRow.id),
      setActiveStateContext('edit')
    ]);
    const detail = detailRes.data;
    formMode.value = 'edit';
    formContext.value = context;
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

const pickWritableFormValue = <T,>(fieldCode: UserFieldCode, value: T | undefined) => {
  return pickWritableModuleValue(formFieldConfigMap.value, fieldCode, value);
};

const handleFormSubmit = async (data: UserFormModel) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'edit' && data.id) {
      const payload: UpdateUserPayload = {
        id: data.id,
        email: pickWritableFormValue('email', data.email || undefined),
        nickname: pickWritableFormValue('nickname', data.nickname || undefined),
        phone: pickWritableFormValue('phone', data.phone || undefined),
        sex: pickWritableFormValue('sex', data.sex),
        status: pickWritableFormValue('status', data.status),
        type: pickWritableFormValue('type', data.type)
      };
      await updateUser(payload);
      ElMessage.success('修改成功');
    } else {
      const payload: CreateUserPayload = {
        email: pickWritableFormValue('email', data.email || undefined),
        nickname: pickWritableFormValue('nickname', data.nickname || undefined),
        password: pickWritableFormValue('password', data.password) || '',
        phone: pickWritableFormValue('phone', data.phone || undefined),
        sex: pickWritableFormValue('sex', data.sex),
        status: pickWritableFormValue('status', data.status),
        type: pickWritableFormValue('type', data.type),
        username: pickWritableFormValue('username', data.username) || ''
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
  if (!listButtonConfigMap.value.remove.visible || listButtonConfigMap.value.remove.disabled) {
    return;
  }
  const rows = row ? [row] : selectedRows.value;
  if (!rows.length) {
    return;
  }
  const names = rows.map((item) => item.username).join('、');
  await ElMessageBox.confirm(`是否确认删除用户“${names}”？`, '警告', {
    cancelButtonText: '取消',
    confirmButtonText: '确定',
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

const isStatusSwitchDisabled = () => {
  return !editFieldConfigMap.value.status.editable
    || !editButtonConfig.value.visible
    || editButtonConfig.value.disabled;
};

const handleStatusChange = async (row: SysUser, value: string) => {
  if (isStatusSwitchDisabled()) {
    return;
  }
  const previousStatus = row.status || '0';
  row.status = value;
  try {
    await updateUser({
      id: row.id,
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
  void Promise.allSettled([
    loadListContext(),
    preloadStateContexts(['create', 'edit']),
    getList()
  ]);
});
</script>
