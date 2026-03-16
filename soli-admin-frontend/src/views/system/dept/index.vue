
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="部门名称" prop="deptName">
        <el-input
          v-model="queryParams.deptName"
          placeholder="请输入部门名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="部门状态" clearable style="width: 240px">
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
      :data="deptList"
      row-key="deptId"
      :default-expand-all="isExpandAll"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="deptName" label="部门名称" width="260" />
      <el-table-column prop="orderNum" label="排序" width="200" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="200">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Plus" @click="handleAdd(scope.row)">新增</el-button>
          <el-button link type="primary" icon="Delete" v-if="scope.row.parentId !== 0" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

defineOptions({
  name: "SystemDept"
})

const showSearch = ref(true);
const loading = ref(false);
const isExpandAll = ref(true);
const refreshTable = ref(true);
const deptList = ref<any[]>([]);

const queryParams = reactive({
  deptName: '',
  status: ''
});

// Mock Data
deptList.value = [
  {
    deptId: 100,
    parentId: 0,
    deptName: '若依科技',
    orderNum: 0,
    status: '0',
    createTime: '2023-01-01 10:00:00',
    children: [
      {
        deptId: 101,
        parentId: 100,
        deptName: '深圳总公司',
        orderNum: 1,
        status: '0',
        createTime: '2023-01-01 10:00:00',
        children: [
          {
            deptId: 103,
            parentId: 101,
            deptName: '研发部门',
            orderNum: 1,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          },
          {
            deptId: 104,
            parentId: 101,
            deptName: '市场部门',
            orderNum: 2,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          },
          {
            deptId: 105,
            parentId: 101,
            deptName: '测试部门',
            orderNum: 3,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          },
          {
            deptId: 106,
            parentId: 101,
            deptName: '财务部门',
            orderNum: 4,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          },
          {
            deptId: 107,
            parentId: 101,
            deptName: '运维部门',
            orderNum: 5,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          }
        ]
      },
      {
        deptId: 102,
        parentId: 100,
        deptName: '长沙分公司',
        orderNum: 2,
        status: '0',
        createTime: '2023-01-01 10:00:00',
        children: [
          {
            deptId: 108,
            parentId: 102,
            deptName: '市场部门',
            orderNum: 1,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          },
          {
            deptId: 109,
            parentId: 102,
            deptName: '财务部门',
            orderNum: 2,
            status: '0',
            createTime: '2023-01-01 10:00:00'
          }
        ]
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
  queryParams.deptName = '';
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
  if (row && row.deptId) {
    ElMessage.info(`新增 [${row.deptName}] 的下级部门`);
  } else {
    ElMessage.info('新增顶级部门');
  }
};

const handleUpdate = (row: any) => {
  ElMessage.info(`修改 [${row.deptName}]`);
};

const handleDelete = (row: any) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.deptName}"的数据项?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    ElMessage.success("删除成功");
  }).catch(() => {});
};
</script>

<style scoped>

</style>
