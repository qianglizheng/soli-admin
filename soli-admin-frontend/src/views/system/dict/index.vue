
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="字典名称" prop="dictName">
        <el-input
          v-model="queryParams.dictName"
          placeholder="请输入字典名称"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字典类型" prop="dictType">
        <el-input
          v-model="queryParams.dictType"
          placeholder="请输入字典类型"
          clearable
          style="width: 240px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="字典状态" clearable style="width: 240px">
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
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="dictList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="字典编号" align="center" prop="dictId" />
      <el-table-column label="字典名称" align="center" prop="dictName" :show-overflow-tooltip="true" />
      <el-table-column label="字典类型" align="center" :show-overflow-tooltip="true">
        <template #default="scope">
          <router-link :to="'/system/dict-data/index/' + scope.row.dictId" class="link-type">
            <span>{{ scope.row.dictType }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
import { Search, Plus, Edit, Delete, Download, Refresh } from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const showSearch = ref(true);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const dictList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  dictName: '',
  dictType: '',
  status: ''
});

// Mock Data
dictList.value = [
  { dictId: 1, dictName: '用户性别', dictType: 'sys_user_sex', status: '0', remark: '用户性别列表', createTime: '2023-01-01 10:00:00' },
  { dictId: 2, dictName: '菜单状态', dictType: 'sys_show_hide', status: '0', remark: '菜单状态列表', createTime: '2023-01-02 11:00:00' },
  { dictId: 3, dictName: '系统开关', dictType: 'sys_normal_disable', status: '0', remark: '系统开关列表', createTime: '2023-01-03 12:00:00' },
  { dictId: 4, dictName: '任务状态', dictType: 'sys_job_status', status: '0', remark: '任务状态列表', createTime: '2023-01-04 13:00:00' },
  { dictId: 5, dictName: '任务分组', dictType: 'sys_job_group', status: '0', remark: '任务分组列表', createTime: '2023-01-05 14:00:00' },
  { dictId: 6, dictName: '系统是否', dictType: 'sys_yes_no', status: '0', remark: '系统是否列表', createTime: '2023-01-06 15:00:00' },
  { dictId: 7, dictName: '通知类型', dictType: 'sys_notice_type', status: '0', remark: '通知类型列表', createTime: '2023-01-07 16:00:00' },
  { dictId: 8, dictName: '通知状态', dictType: 'sys_notice_status', status: '0', remark: '通知状态列表', createTime: '2023-01-08 17:00:00' },
  { dictId: 9, dictName: '操作类型', dictType: 'sys_oper_type', status: '0', remark: '操作类型列表', createTime: '2023-01-09 18:00:00' },
  { dictId: 10, dictName: '系统状态', dictType: 'sys_common_status', status: '0', remark: '登录状态列表', createTime: '2023-01-10 19:00:00' }
];
total.value = 10;

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.dictName = '';
  queryParams.dictType = '';
  queryParams.status = '';
  handleQuery();
};

const handleSelectionChange = (selection: any[]) => {
  single.value = selection.length !== 1;
  multiple.value = !selection.length;
};

const handleAdd = () => { ElMessage.info('点击新增'); };
const handleUpdate = (row: any) => { ElMessage.info('点击修改'); };
const handleDelete = (row: any) => {
  ElMessageBox.confirm('是否确认删除选中的数据项?', "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(() => {
    ElMessage.success("删除成功");
  }).catch(() => {});
};
const handleExport = () => { ElMessage.info('点击导出'); };
const handleRefreshCache = () => { ElMessage.success('刷新缓存成功'); };
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
.link-type {
  color: #409EFF;
  text-decoration: none;
  cursor: pointer;
}
.link-type:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>
