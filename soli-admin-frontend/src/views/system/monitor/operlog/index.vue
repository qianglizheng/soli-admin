
<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="系统模块" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入系统模块"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="operName">
        <el-input
          v-model="queryParams.operName"
          placeholder="请输入操作人员"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="businessType">
        <el-select
          v-model="queryParams.businessType"
          placeholder="操作类型"
          clearable
          style="width: 240px"
        >
          <el-option label="新增" value="1" />
          <el-option label="修改" value="2" />
          <el-option label="删除" value="3" />
          <el-option label="授权" value="4" />
          <el-option label="导出" value="5" />
          <el-option label="导入" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select
          v-model="queryParams.status"
          placeholder="操作状态"
          clearable
          style="width: 240px"
        >
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
    <el-table v-loading="loading" :data="operLogList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="日志编号" align="center" prop="operId" />
      <el-table-column label="系统模块" align="center" prop="title" />
      <el-table-column label="操作类型" align="center" prop="businessType">
        <template #default="scope">
          <el-tag>{{ getBusinessType(scope.row.businessType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="请求方式" align="center" prop="requestMethod" />
      <el-table-column label="操作人员" align="center" prop="operName" />
      <el-table-column label="主机" align="center" prop="operIp" width="130" :show-overflow-tooltip="true" />
      <el-table-column label="操作状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '正常' : '异常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作日期" align="center" prop="operTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.operTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleView(scope.row)">详细</el-button>
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
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { Search, Delete, Download, Refresh, View } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const showSearch = ref(true);
const loading = ref(false);
const multiple = ref(true);
const total = ref(0);
const operLogList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  operName: '',
  businessType: '',
  status: ''
});

// Mock Data
operLogList.value = [
  { operId: 1, title: '用户管理', businessType: '1', requestMethod: 'POST', operName: 'admin', operIp: '127.0.0.1', status: '0', operTime: '2023-01-01 10:00:00' },
  { operId: 2, title: '角色管理', businessType: '2', requestMethod: 'PUT', operName: 'admin', operIp: '127.0.0.1', status: '1', operTime: '2023-01-01 11:00:00' }
];
total.value = 2;

const getBusinessType = (type: string) => {
  const map: Record<string, string> = {
    '1': '新增', '2': '修改', '3': '删除', '4': '授权', '5': '导出', '6': '导入'
  };
  return map[type] || '其他';
};

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.title = '';
  queryParams.operName = '';
  queryParams.businessType = '';
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
  }).then(function() {
    ElMessage.success("删除成功");
  }).catch(() => {});
};

const handleClean = () => {
  ElMessageBox.confirm('是否确认清空所有操作日志数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(function() {
    ElMessage.success("清空成功");
  }).catch(() => {});
};

const handleExport = () => { ElMessage.info('点击导出'); };
const handleView = (row: any) => { ElMessage.info('查看详情: ' + row.title); };

</script>

<style scoped>
.app-container {
  background-color: #fff;
  min-height: 100%;
}
.mb8 {
  margin-bottom: 8px;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
