<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="登录地址" prop="ipaddr">
        <el-input v-model="queryParams.ipaddr" placeholder="请输入登录地址" clearable style="width: 240px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="用户名称" prop="userName">
        <el-input v-model="queryParams.userName" placeholder="请输入用户名称" clearable style="width: 240px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="登录状态" clearable style="width: 240px">
          <el-option label="成功" value="0" />
          <el-option label="失败" value="1" />
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
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" @click="handleClean">清空</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
    </el-row>

    <!-- 表格区域 -->
    <el-table v-loading="loading" :data="loginList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="访问编号" align="center" prop="infoId" />
      <el-table-column label="用户名称" align="center" prop="userName" />
      <el-table-column label="登录地址" align="center" prop="ipaddr" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="登录地点" align="center" prop="loginLocation" :show-overflow-tooltip="true" />
      <el-table-column label="浏览器" align="center" prop="browser" />
      <el-table-column label="操作系统" align="center" prop="os" />
      <el-table-column label="登录状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '成功' : '失败'
            }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作信息" align="center" prop="msg" />
      <el-table-column label="登录日期" align="center" prop="loginTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.loginTime }}</span>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 30, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total"
        @size-change="handleQuery" @current-change="handleQuery" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

defineOptions({
  name: "LoginInfo"
})

const showSearch = ref(true);
const loading = ref(false);
const multiple = ref(true);
const total = ref(0);
const loginList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ipaddr: '',
  userName: '',
  status: ''
});

// Mock Data
loginList.value = [
  { infoId: 1, userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '0', msg: '登录成功', loginTime: '2023-01-01 10:00:00' },
  { infoId: 2, userName: 'admin', ipaddr: '127.0.0.1', loginLocation: '内网IP', browser: 'Chrome', os: 'Windows 10', status: '1', msg: '密码错误', loginTime: '2023-01-01 11:00:00' }
];
total.value = 2;

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.ipaddr = '';
  queryParams.userName = '';
  queryParams.status = '';
  handleQuery();
};

const handleSelectionChange = (selection: any[]) => {
  multiple.value = !selection.length;
};

const handleDelete = () => {
  ElMessageBox.confirm('是否确认删除选中的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function () {
    ElMessage.success("删除成功");
  }).catch(() => { });
};

const handleClean = () => {
  ElMessageBox.confirm('是否确认清空所有登录日志数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function () {
    ElMessage.success("清空成功");
  }).catch(() => { });
};

const handleExport = () => { ElMessage.info('点击导出'); };

</script>
