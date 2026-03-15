<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="菜单名称" prop="menuName">
        <el-input
          v-model="queryParams.menuName"
          placeholder="请输入菜单名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="菜单状态" clearable style="width: 240px">
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
      <el-table-column label="创建时间" align="center" prop="createTime">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getMenuTree } from '@/api/menu';
import type { SysMenuDTO } from '@/types/global';

defineOptions({
  name: "SystemMenuPage"
})

const showSearch = ref<boolean>(true);
const loading = ref<boolean>(false);
const isExpandAll = ref<boolean>(false);
const refreshTable = ref<boolean>(true);
const menuList = ref<SysMenuDTO[]>([]);

const queryParams = reactive({
  menuName: '',
  status: ''
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
  if (row && row.id) {
    ElMessage.info(`新增 [${row.name}] 的子菜单`);
  } else {
    ElMessage.info('新增顶级菜单');
  }
};

const handleUpdate = (row: SysMenuDTO) => {
  ElMessage.info(`修改 [${row.name}]`);
};

const handleDelete = (row: SysMenuDTO) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.name}"的数据项?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    ElMessage.success("删除成功");
  }).catch(() => {});
};

onMounted(() => {
  loadMenuTree();
});
</script>
