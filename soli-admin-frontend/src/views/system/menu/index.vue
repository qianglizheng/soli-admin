<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <div ref="searchCollapseRef" class="search-collapse-container">
        <el-form-item label="菜单名称" prop="menuName" data-search-item="true">
          <el-input
            v-model="queryParams.menuName"
            placeholder="请输入菜单名称"
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
          <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 240px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item
          label="权限字符"
          prop="menuPermsKeyword"
          data-search-item="true"
          data-search-more="true"
          :class="{ 'search-collapse-item-hidden': !isSearchMeasured || (showMoreButton && !showMoreSearch) }"
        >
          <el-input
            v-model="queryParams.menuPermsKeyword"
            placeholder="请输入权限字符"
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
        <el-button type="info" plain icon="Sort" @click="toggleExpandAll">展开/折叠</el-button>
      </el-col>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="menuList"
      row-key="id"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="菜单名称" :show-overflow-tooltip="true" width="160" />
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <el-icon v-if="scope.row.icon">
            <component :is="scope.row.icon" />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="perms" label="权限代码" :show-overflow-tooltip="true" />
      <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" fixed="right" min-width="260" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <menu-form
      v-model="formVisible"
      :mode="formMode"
      :initialData="formInitial"
      :treeData="menuList"
      :currentId="formMode === 'edit' ? (formInitial.id as number) : undefined"
      @submit="onFormSubmit"
      @cancel="onFormCancel"
    />
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, reactive, ref } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { addMenu, deleteMenu, getMenuTree, updateMenu } from '@/api/menu';
import type { SysMenuDTO } from '@/types/global';
import MenuForm from './components/MenuForm.vue';
import { useSearchCollapse } from '@/utils/useSearchCollapse';

defineOptions({
  name: "SystemMenu"
})

const showSearch = ref<boolean>(true);
const {
  searchCollapseRef,
  isSearchMeasured,
  showMoreButton,
  showMoreSearch,
  toggleMoreSearch,
} = useSearchCollapse(showSearch);
const loading = ref<boolean>(false);
const isExpandAll = ref<boolean>(false);
const refreshTable = ref<boolean>(true);
const menuList = ref<SysMenuDTO[]>([]);

const queryParams = reactive({
  menuName: '',
  status: '',
  menuPermsKeyword: ''
});

const loadMenuTree = async () => {
  loading.value = true;
  try {
    const res = await getMenuTree({
      menuName: queryParams.menuName || undefined,
      status: queryParams.status || undefined
    });
    menuList.value = res.data || [];
  } catch (error) {
    console.error('加载菜单树失败', error);
    menuList.value = [];
  } finally {
    loading.value = false;
  }
};

const handleQuery = async () => {
  await loadMenuTree();
  ElMessage.success('查询成功');
};

const resetQuery = async () => {
  queryParams.menuName = '';
  queryParams.status = '';
  queryParams.menuPermsKeyword = '';
  await handleQuery();
};

const toggleExpandAll = () => {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
};

const handleAdd = (row?: SysMenuDTO) => {
  formMode.value = 'create';
  formInitial.value = row && row.id ? { parentId: row.id } : { parentId: 0 };
  formVisible.value = true;
};

const handleUpdate = (row: SysMenuDTO) => {
  formMode.value = 'edit';
  formInitial.value = { ...row };
  formVisible.value = true;
};

const handleDelete = (row: SysMenuDTO) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.name}"的数据项?`, "警告", {
    cancelButtonText: "取消",
    confirmButtonText: "确定",
    type: "warning"
  }).then(() => {
    if (row.id) {
      return deleteMenu(row.id);
    }
  }).then(() => {
    ElMessage.success("删除成功");
    handleQuery();
  }).catch(() => {});
};

const formVisible = ref(false);
const formMode = ref<'create' | 'edit'>('create');
const formInitial = ref<Partial<SysMenuDTO>>({});

const onFormSubmit = async (data: Partial<SysMenuDTO>) => {
  loading.value = true;
  try {
    if (formMode.value === 'create') {
      await addMenu(data);
      ElMessage.success('新增成功');
    } else {
      await updateMenu(data);
      ElMessage.success('修改成功');
    }
    await handleQuery();
  } finally {
    loading.value = false;
  }
};

const onFormCancel = () => {
  formVisible.value = false;
};

onMounted(() => {
  loadMenuTree();
});
</script>
