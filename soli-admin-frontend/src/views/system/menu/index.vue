
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
      row-key="menuId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160" />
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template #default="scope">
          <el-icon v-if="scope.row.icon">
            <component :is="scope.row.icon" />
          </el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序" width="60" />
      <el-table-column prop="perms" label="权限标识" :show-overflow-tooltip="true" />
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
import { ref, reactive, nextTick } from 'vue';
import { Search, Plus, Edit, Delete, Sort, Refresh } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const showSearch = ref(true);
const loading = ref(false);
const isExpandAll = ref(false);
const refreshTable = ref(true);
const menuList = ref<any[]>([]);

const queryParams = reactive({
  menuName: '',
  status: ''
});

// Mock Data
menuList.value = [
  {
    menuId: 1,
    menuName: '系统管理',
    icon: 'Setting',
    orderNum: 1,
    perms: '',
    component: '',
    status: '0',
    createTime: '2023-01-01 10:00:00',
    children: [
      {
        menuId: 101,
        menuName: '用户管理',
        icon: 'User',
        orderNum: 1,
        perms: 'system:user:list',
        component: 'system/user/index',
        status: '0',
        createTime: '2023-01-01 10:00:00'
      },
      {
        menuId: 102,
        menuName: '角色管理',
        icon: 'Avatar',
        orderNum: 2,
        perms: 'system:role:list',
        component: 'system/role/index',
        status: '0',
        createTime: '2023-01-01 10:00:00'
      }
    ]
  },
  {
    menuId: 2,
    menuName: '日志管理',
    icon: 'Monitor',
    orderNum: 2,
    perms: '',
    component: '',
    status: '0',
    createTime: '2023-01-01 10:00:00',
    children: [
      {
        menuId: 201,
        menuName: '操作日志',
        icon: 'Document',
        orderNum: 1,
        perms: 'monitor:operlog:list',
        component: 'monitor/operlog/index',
        status: '0',
        createTime: '2023-01-01 10:00:00'
      }
    ]
  }
];

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.menuName = '';
  queryParams.status = '';
  handleQuery();
};

const toggleExpandAll = () => {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
};

const handleAdd = (row?: any) => {
  if (row && row.menuId) {
    ElMessage.info(`新增 [${row.menuName}] 的子菜单`);
  } else {
    ElMessage.info('新增顶级菜单');
  }
};

const handleUpdate = (row: any) => {
  ElMessage.info(`修改 [${row.menuName}]`);
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.menuName}"的数据项?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    ElMessage.success("删除成功");
  }).catch(() => {});
};
</script>

<style scoped>
.app-container {
  background-color: #fff;
  min-height: 100%;
}
.mb8 {
  margin-bottom: 8px;
}
</style>
