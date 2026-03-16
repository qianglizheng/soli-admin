<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch">
      <el-form-item label="参数名称" prop="configName">
        <el-input v-model="queryParams.configName" placeholder="请输入参数名称" clearable style="width: 240px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="参数键名" prop="configKey">
        <el-input v-model="queryParams.configKey" placeholder="请输入参数键名" clearable style="width: 240px"
          @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="系统内置" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="系统内置" clearable style="width: 240px">
          <el-option label="是" value="Y" />
          <el-option label="否" value="N" />
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

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="参数主键" align="center" prop="configId" />
      <el-table-column label="参数名称" align="center" prop="configName" :show-overflow-tooltip="true" />
      <el-table-column label="参数键名" align="center" prop="configKey" :show-overflow-tooltip="true" />
      <el-table-column label="参数键值" align="center" prop="configValue" :show-overflow-tooltip="true" />
      <el-table-column label="系统内置" align="center" prop="configType">
        <template #default="scope">
          <el-tag :type="scope.row.configType === 'Y' ? 'danger' : 'primary'">
            {{ scope.row.configType === 'Y' ? '是' : '否' }}
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
  name: "SystemConfig"
})

const showSearch = ref(true);
const loading = ref(false);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const configList = ref<any[]>([]);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  configName: '',
  configKey: '',
  configType: ''
});

// Mock Data
configList.value = [
  { configId: 1, configName: '主框架页-默认皮肤样式名称', configKey: 'sys.index.skinName', configValue: 'skin-blue', configType: 'Y', remark: '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow', createTime: '2023-01-01 10:00:00' },
  { configId: 2, configName: '用户管理-账号初始密码', configKey: 'sys.user.initPassword', configValue: '123456', configType: 'Y', remark: '初始化密码 123456', createTime: '2023-01-02 11:00:00' },
  { configId: 3, configName: '主框架页-侧边栏主题', configKey: 'sys.index.sideTheme', configValue: 'theme-dark', configType: 'Y', remark: '深色主题 theme-dark，浅色主题 theme-light', createTime: '2023-01-03 12:00:00' },
  { configId: 4, configName: '账号自助-验证码开关', configKey: 'sys.account.captchaEnabled', configValue: 'true', configType: 'Y', remark: '是否开启验证码', createTime: '2023-01-04 13:00:00' },
  { configId: 5, configName: '账号自助-注册开关', configKey: 'sys.account.registerEnabled', configValue: 'false', configType: 'Y', remark: '是否开启注册功能', createTime: '2023-01-05 14:00:00' },
  { configId: 6, configName: '用户登录-黑名单列表', configKey: 'sys.login.blackIPList', configValue: '', configType: 'Y', remark: '设置登录IP黑名单', createTime: '2023-01-06 15:00:00' }
];
total.value = 6;

const handleQuery = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    ElMessage.success('查询成功');
  }, 500);
};

const resetQuery = () => {
  queryParams.configName = '';
  queryParams.configKey = '';
  queryParams.configType = '';
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
  }).catch(() => { });
};
const handleExport = () => { ElMessage.info('点击导出'); };
const handleRefreshCache = () => { ElMessage.success('刷新缓存成功'); };
</script>
