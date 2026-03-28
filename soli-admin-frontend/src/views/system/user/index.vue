<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" v-show="showSearch">
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
      <el-col v-if="listButtonConfigMap.create.visible" :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          :disabled="listButtonConfigMap.create.disabled"
          @click="handleAdd"
        >
          {{ listButtonConfigMap.create.label }}
        </el-button>
      </el-col>
      <el-col v-if="listButtonConfigMap.modify.visible" :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single || listButtonConfigMap.modify.disabled"
          @click="handleUpdate()"
        >
          {{ listButtonConfigMap.modify.label }}
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

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户编号" align="center" prop="id" width="120" />
      <el-table-column
        v-if="listFieldConfigMap.username.visible"
        :label="listFieldConfigMap.username.label"
        align="center"
        prop="username"
        :show-overflow-tooltip="true"
        min-width="140"
      />
      <el-table-column
        v-if="listFieldConfigMap.nickname.visible"
        :label="listFieldConfigMap.nickname.label"
        align="center"
        prop="nickname"
        :show-overflow-tooltip="true"
        min-width="140"
      />
      <el-table-column
        v-if="listFieldConfigMap.phone.visible"
        :label="listFieldConfigMap.phone.label"
        align="center"
        prop="phone"
        width="140"
      />
      <el-table-column
        v-if="listFieldConfigMap.email.visible"
        :label="listFieldConfigMap.email.label"
        align="center"
        prop="email"
        :show-overflow-tooltip="true"
        min-width="180"
      />
      <el-table-column
        v-if="listFieldConfigMap.type.visible"
        :label="listFieldConfigMap.type.label"
        align="center"
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
        :label="listFieldConfigMap.status.label"
        align="center"
        width="100"
      >
        <template #default="{ row }">
          <el-switch
            :model-value="row.status"
            active-value="0"
            inactive-value="1"
            :disabled="isStatusSwitchDisabled()"
            @change="handleStatusSwitchChange(row, $event)"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column
        v-if="showOperationColumn"
        label="操作"
        align="center"
        fixed="right"
        min-width="260"
        class-name="small-padding fixed-width"
      >
        <template #default="{ row }">
          <el-button
            v-if="listButtonConfigMap.modify.visible"
            link
            type="primary"
            icon="Edit"
            :disabled="listButtonConfigMap.modify.disabled"
            @click="handleUpdate(row)"
          >
            {{ listButtonConfigMap.modify.label }}
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

    <user-form
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
  createUser,
  deleteUser,
  getUserDetail,
  getUserModuleContext,
  getUserPage,
  updateUser,
  type CreateUserPayload,
  type UpdateUserPayload
} from '@/api/user';
import type { ModuleContext } from '@/api/moduleCenter';
import type { SysUser } from '@/types/global';
import UserForm, { type UserFormModel } from './components/UserForm.vue';
import { useSearchCollapse } from '@/utils/useSearchCollapse';

defineOptions({
  name: 'SystemUser'
});

type UserFieldCode = 'username' | 'password' | 'nickname' | 'email' | 'phone' | 'sex' | 'type' | 'status';
type UserButtonCode = 'create' | 'modify' | 'remove';
type ModuleFieldContext = ModuleContext['headerTabs'][number]['fields'][number];
type ModuleButtonContext = ModuleContext['buttons']['listToolbar'][string];

interface ResolvedFieldConfig {
  label: string;
  placeholder: string;
  helpText: string;
  visible: boolean;
  editable: boolean;
  required: boolean;
}

interface ResolvedButtonConfig {
  label: string;
  visible: boolean;
  disabled: boolean;
}

const userFieldFallbackMap: Record<UserFieldCode, ResolvedFieldConfig> = {
  username: {
    editable: true,
    helpText: '',
    label: '用户名称',
    placeholder: '请输入用户名称',
    required: true,
    visible: true
  },
  password: {
    editable: true,
    helpText: '',
    label: '用户密码',
    placeholder: '请输入用户密码',
    required: true,
    visible: true
  },
  nickname: {
    editable: true,
    helpText: '',
    label: '用户昵称',
    placeholder: '请输入用户昵称',
    required: false,
    visible: true
  },
  email: {
    editable: true,
    helpText: '',
    label: '用户邮箱',
    placeholder: '请输入用户邮箱',
    required: false,
    visible: true
  },
  phone: {
    editable: true,
    helpText: '',
    label: '手机号码',
    placeholder: '请输入手机号码',
    required: false,
    visible: true
  },
  sex: {
    editable: true,
    helpText: '',
    label: '性别',
    placeholder: '请选择性别',
    required: false,
    visible: true
  },
  type: {
    editable: true,
    helpText: '',
    label: '账号类型',
    placeholder: '请选择账号类型',
    required: false,
    visible: true
  },
  status: {
    editable: true,
    helpText: '',
    label: '状态',
    placeholder: '请选择用户状态',
    required: false,
    visible: true
  }
};

const userButtonFallbackMap: Record<UserButtonCode, ResolvedButtonConfig> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  remove: { disabled: false, label: '删除', visible: true }
};

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
const listContext = ref<ModuleContext | null>(null);
const formContext = ref<ModuleContext | null>(null);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  phone: '',
  status: '',
  nicknameKeyword: ''
});

const buildFieldContextMap = (context: ModuleContext | null) => {
  const result: Partial<Record<UserFieldCode, ModuleFieldContext>> = {};
  if (!context) {
    return result;
  }
  [...context.headerTabs, ...context.detailTabs].forEach((tab) => {
    tab.fields.forEach((field) => {
      if (field.fieldCode in userFieldFallbackMap) {
        result[field.fieldCode as UserFieldCode] = field;
      }
    });
  });
  return result;
};

const buildButtonContextMap = (context: ModuleContext | null) => {
  const result: Partial<Record<UserButtonCode, ModuleButtonContext>> = {};
  if (!context?.buttons) {
    return result;
  }
  const buttonGroups = [
    context.buttons.listToolbar,
    context.buttons.listRow,
    context.buttons.headerToolbar,
    context.buttons.detailRow
  ];
  buttonGroups.forEach((buttonGroup) => {
    Object.values(buttonGroup || {}).forEach((button) => {
      if (button.buttonCode in userButtonFallbackMap) {
        result[button.buttonCode as UserButtonCode] = button;
      }
    });
  });
  return result;
};

const buildResolvedFieldConfigMap = (fieldMap: Partial<Record<UserFieldCode, ModuleFieldContext>>) => {
  return (Object.keys(userFieldFallbackMap) as UserFieldCode[]).reduce<Record<UserFieldCode, ResolvedFieldConfig>>((result, fieldCode) => {
    const fallback = userFieldFallbackMap[fieldCode];
    const field = fieldMap[fieldCode];
    result[fieldCode] = {
      editable: field?.editable ?? fallback.editable,
      helpText: field?.helpText || fallback.helpText,
      label: field?.label || field?.displayTitle || field?.defaultTitle || fallback.label,
      placeholder: field?.placeholder || fallback.placeholder,
      required: field?.required ?? fallback.required,
      visible: field?.visible ?? fallback.visible
    };
    return result;
  }, {} as Record<UserFieldCode, ResolvedFieldConfig>);
};

const buildResolvedButtonConfigMap = (buttonMap: Partial<Record<UserButtonCode, ModuleButtonContext>>) => {
  return (Object.keys(userButtonFallbackMap) as UserButtonCode[]).reduce<Record<UserButtonCode, ResolvedButtonConfig>>((result, buttonCode) => {
    const fallback = userButtonFallbackMap[buttonCode];
    const button = buttonMap[buttonCode];
    result[buttonCode] = {
      disabled: button?.disabled ?? fallback.disabled,
      label: button?.label || button?.defaultTitle || fallback.label,
      visible: button?.visible ?? fallback.visible
    };
    return result;
  }, {} as Record<UserButtonCode, ResolvedButtonConfig>);
};

const listFieldConfigMap = computed(() => buildResolvedFieldConfigMap(buildFieldContextMap(listContext.value)));
const formFieldConfigMap = computed(() => buildResolvedFieldConfigMap(buildFieldContextMap(formContext.value)));
const listButtonConfigMap = computed(() => buildResolvedButtonConfigMap(buildButtonContextMap(listContext.value)));

const showOperationColumn = computed(() => {
  return listButtonConfigMap.value.modify.visible || listButtonConfigMap.value.remove.visible;
});

const loadListContext = async () => {
  try {
    const res = await getUserModuleContext();
    listContext.value = res.data;
  } catch {
    listContext.value = null;
  }
};

const loadFormContext = async (stateCode: 'create' | 'edit') => {
  try {
    const res = await getUserModuleContext(stateCode);
    return res.data;
  } catch {
    return null;
  }
};

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

const handleAdd = async () => {
  if (!listButtonConfigMap.value.create.visible || listButtonConfigMap.value.create.disabled) {
    return;
  }
  formMode.value = 'create';
  formContext.value = await loadFormContext('create');
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
  if (!listButtonConfigMap.value.modify.visible || listButtonConfigMap.value.modify.disabled) {
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
      loadFormContext('edit')
    ]);
    const detail = detailRes.data;
    formContext.value = context;
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

const pickWritableFormValue = <T,>(fieldCode: UserFieldCode, value: T | undefined) => {
  const fieldConfig = formFieldConfigMap.value[fieldCode];
  if (!fieldConfig.visible || !fieldConfig.editable) {
    return undefined;
  }
  return value;
};

const handleFormSubmit = async (data: UserFormModel) => {
  submitLoading.value = true;
  try {
    if (formMode.value === 'edit' && data.id) {
      const payload: UpdateUserPayload = {
        id: data.id,
        nickname: pickWritableFormValue('nickname', data.nickname || undefined),
        email: pickWritableFormValue('email', data.email || undefined),
        phone: pickWritableFormValue('phone', data.phone || undefined),
        sex: pickWritableFormValue('sex', data.sex),
        type: pickWritableFormValue('type', data.type),
        status: pickWritableFormValue('status', data.status)
      };
      await updateUser(payload);
      ElMessage.success('修改成功');
    } else {
      const payload: CreateUserPayload = {
        username: pickWritableFormValue('username', data.username) || '',
        password: pickWritableFormValue('password', data.password) || '',
        nickname: pickWritableFormValue('nickname', data.nickname || undefined),
        email: pickWritableFormValue('email', data.email || undefined),
        phone: pickWritableFormValue('phone', data.phone || undefined),
        sex: pickWritableFormValue('sex', data.sex),
        type: pickWritableFormValue('type', data.type),
        status: pickWritableFormValue('status', data.status)
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

const isStatusSwitchDisabled = () => {
  return !listFieldConfigMap.value.status.editable
    || !listButtonConfigMap.value.modify.visible
    || listButtonConfigMap.value.modify.disabled;
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
  void Promise.allSettled([loadListContext(), getList()]);
});
</script>
