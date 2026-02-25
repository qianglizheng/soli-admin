
<template>
  <div class="app-container">
    <el-row :gutter="0">
      <!-- 左侧部门树 -->
      <el-col :span="4" :xs="24">
        <div class="box-container dept-container">
          <el-input v-model="deptName" placeholder="请输入部门名称" prefix-icon="Search" clearable class="mb10" />
          <el-tree
            ref="deptTreeRef"
            class="filter-tree"
            :data="deptData"
            :props="defaultProps"
            default-expand-all
            :filter-node-method="filterNode"
            @node-click="handleNodeClick"
          />
        </div>
      </el-col>

      <!-- 右侧用户数据 -->
      <el-col :span="20" :xs="24">
        <div class="box-container">
          <!-- 搜索区域 -->
          <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
            <el-form-item label="用户名称" prop="userName">
              <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名称"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item label="手机号码" prop="phonenumber">
              <el-input
                v-model="queryParams.phonenumber"
                placeholder="请输入手机号码"
                clearable
                style="width: 240px"
                @keyup.enter="handleQuery"
              />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select
                v-model="queryParams.status"
                placeholder="用户状态"
                clearable
                style="width: 240px"
              >
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
              <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
            </el-col>
          </el-row>

          <!-- 表格区域 -->
          <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="50" align="center" />
            <el-table-column label="用户编号" align="center" key="userId" prop="userId" />
            <el-table-column label="用户名称" align="center" key="userName" prop="userName" :show-overflow-tooltip="true" />
            <el-table-column label="用户昵称" align="center" key="nickName" prop="nickName" :show-overflow-tooltip="true" />
            <el-table-column label="部门" align="center" key="deptName" prop="dept.deptName" :show-overflow-tooltip="true" />
            <el-table-column label="手机号码" align="center" key="phonenumber" prop="phonenumber" width="120" />
            <el-table-column label="状态" align="center" key="status">
              <template #default="scope">
                <el-switch
                  v-model="scope.row.status"
                  active-value="0"
                  inactive-value="1"
                  @change="handleStatusChange(scope.row)"
                />
              </template>
            </el-table-column>
            <el-table-column label="创建时间" align="center" prop="createTime" width="160">
              <template #default="scope">
                <span>{{ scope.row.createTime }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
              <template #default="scope">
                <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
                <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
                <el-button link type="primary" icon="Key" @click="handleResetPwd(scope.row)">重置密码</el-button>
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
              @size-change="handleQuery"
              @current-change="handleQuery"
            />
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, nextTick } from 'vue';
import { Search, Plus, Edit, Delete, Download, Refresh, Key } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const deptName = ref('');
const deptTreeRef = ref(null);
const showSearch = ref(true);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const userList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: '',
  deptId: undefined
});

const defaultProps = {
  children: 'children',
  label: 'label'
};

const deptData = ref([
  {
    id: 1,
    label: '总公司',
    children: [
      { id: 101, label: '研发部门' },
      { id: 102, label: '市场部门' },
      { id: 103, label: '测试部门' },
      { id: 104, label: '财务部门' },
      { id: 105, label: '运维部门' }
    ]
  }
]);

// Mock Data
userList.value = [
  { userId: 1, userName: 'admin', nickName: '管理员', dept: { deptName: '研发部门' }, phonenumber: '15888888888', status: '0', createTime: '2023-01-01 10:00:00' },
  { userId: 2, userName: 'ry', nickName: '若依', dept: { deptName: '测试部门' }, phonenumber: '15666666666', status: '1', createTime: '2023-01-02 11:00:00' }
];
total.value = 2;

watch(deptName, (val) => {
  (deptTreeRef.value as any).filter(val);
});

const filterNode = (value: string, data: any) => {
  if (!value) return true;
  return data.label.includes(value);
};

const handleNodeClick = (data: any) => {
  queryParams.deptId = data.id;
  handleQuery();
};

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.userName = '';
  queryParams.phonenumber = '';
  queryParams.status = '';
  queryParams.deptId = undefined;
  handleQuery();
};

const handleSelectionChange = (selection: any[]) => {
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleStatusChange = (row: any) => {
  let text = row.status === '0' ? '启用' : '停用';
  ElMessageBox.confirm(`确认要"${text}""${row.userName}"用户吗?`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function() {
    ElMessage.success(`${text}成功`);
  }).catch(function() {
    row.status = row.status === '0' ? '1' : '0';
  });
};

const handleAdd = () => { ElMessage.info('点击新增'); };
const handleUpdate = (row: any) => { ElMessage.info('点击修改'); };
const handleDelete = (row: any) => { ElMessage.info('点击删除'); };
const handleExport = () => { ElMessage.info('点击导出'); };
const handleResetPwd = (row: any) => { ElMessage.info('重置密码'); };

</script>

<style scoped>
.box-container {
  padding: 10px;
  background: #fff;
}
.dept-container {
  border-right: 1px solid #d8dce5;
}
.filter-tree {
  margin-top: 10px;
}
</style>
