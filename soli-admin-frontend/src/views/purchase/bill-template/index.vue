<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <div class="tech-card search-wrapper">
      <el-form :model="queryParams" ref="queryRef" :inline="true">
        <el-form-item label="单据编号" prop="billNo">
          <el-input v-model="queryParams.billNo" placeholder="请输入单据编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="供应商" prop="supplier">
          <el-select v-model="queryParams.supplier" placeholder="请选择供应商" clearable style="width: 200px">
            <el-option label="华为技术有限公司" value="1" />
            <el-option label="小米通讯技术有限公司" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="单据状态" clearable style="width: 150px">
            <el-option label="未审核" value="0" />
            <el-option label="待审核" value="1" />
            <el-option label="已完成" value="2" />
            <el-option label="已作废" value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 表格区域 -->
    <div class="tech-card table-wrapper">
      <div class="table-operations" style="margin-bottom: 20px">
        <el-button type="primary" icon="Plus" @click="handleAdd">新建采购单</el-button>
        <el-button type="success" plain icon="Download">导出</el-button>
      </div>

      <el-table v-loading="loading" :data="billList" border stripe highlight-current-row>
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column label="单据编号" prop="billNo" min-width="150">
          <template #default="scope">
            <el-link type="primary" @click="handleView(scope.row)">{{ scope.row.billNo }}</el-link>
          </template>
        </el-table-column>
        <el-table-column label="单据类型" prop="billType" width="120" />
        <el-table-column label="供应商" prop="supplierName" show-overflow-tooltip min-width="180" />
        <el-table-column label="合计金额" prop="totalAmount" width="150" align="right">
          <template #default="scope">
            <span style="font-weight: bold; color: #f56c6c">¥ {{ scope.row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column label="单据状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.statusName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" fixed="right" width="180">
          <template #default="scope">
            <el-button link type="primary" icon="View" @click="handleView(scope.row)">详情</el-button>
            <el-button link type="primary" icon="Edit">编辑</el-button>
            <el-button link type="danger" icon="Delete">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" style="margin-top: 20px; display: flex; justify-content: flex-end">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';

defineOptions({
  name: 'BillTemplateList'
});

const router = useRouter();
const loading = ref(false);
const total = ref(1);

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  billNo: '',
  supplier: '',
  status: ''
});

const billList = ref([
  {
    billNo: 'PO-20240322-001',
    billType: '采购进货',
    supplierName: '华为技术有限公司',
    totalAmount: '128,500.00',
    status: '0',
    statusName: '未审核',
    createTime: '2024-03-22 10:00:00'
  }
]);

const handleQuery = () => {
  // Mock query
};

const resetQuery = () => {
  queryParams.billNo = '';
  queryParams.supplier = '';
  queryParams.status = '';
};

const getStatusType = (status: string) => {
  const types: Record<string, string> = { 
    '0': 'info', 
    '1': 'warning', 
    '2': 'success',
    '3': 'danger'
  };
  return types[status] || 'info';
};

const handleAdd = () => {
  router.push('/purchase/bill-template/detail');
};

const handleView = (row: any) => {
  router.push({
    path: '/purchase/bill-template/detail',
    query: { id: row.billNo }
  });
};
</script>

<style scoped lang="scss">
.search-wrapper {
  padding: 20px 20px 0;
}
</style>