<template>
  <div class="bill-detail-wrapper">
    <!-- 1. 详情头部 -->
    <DetailHeader 
      title="采购订单" 
      :billNo="billInfo.billNo" 
      :statusName="billInfo.statusName" 
      :statusType="getStatusType(billInfo.status)"
      @back="handleBack"
    >
      <template #extra>
        <el-dropdown split-button type="primary" @click="handleSave(billInfo.status)" @command="handleSave">
          <el-icon><DocumentChecked /></el-icon>&nbsp;保存单据
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="0">保存为未审核 (暂存)</el-dropdown-item>
              <el-dropdown-item command="1">保存并提交审核</el-dropdown-item>
              <el-dropdown-item command="2">保存并直接过账</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button icon="Check" @click="handleAudit" style="margin-left: 12px">审核通过</el-button>
        <el-button icon="Printer">打印</el-button>
        <el-button type="danger" plain icon="Delete">作废</el-button>
      </template>
    </DetailHeader>

    <!-- 2. 单头信息 -->
    <CollapsibleCard title="单据头信息" icon="InfoFilled" v-model="headerExpanded">
      <el-tabs v-model="activeHeaderTab" class="header-tabs custom-plain-tabs">
        <el-tab-pane label="基础信息" name="basic">
          <el-descriptions :column="4" border size="small">
            <el-descriptions-item label="单据日期">
              <el-date-picker v-model="billInfo.billDate" type="date" size="small" style="width: 100%" />
            </el-descriptions-item>
            <el-descriptions-item label="供应商">
              <el-select v-model="billInfo.supplierId" size="small" style="width: 100%">
                <el-option label="华为技术有限公司" :value="1" />
              </el-select>
            </el-descriptions-item>
            <el-descriptions-item label="结算方式">
              <el-select v-model="billInfo.settleType" size="small" style="width: 100%">
                <el-option label="转账付款" value="1" />
              </el-select>
            </el-descriptions-item>
            <el-descriptions-item label="仓库">
              <el-select v-model="billInfo.warehouseId" size="small" style="width: 100%">
                <el-option label="深圳一号仓" :value="1" />
              </el-select>
            </el-descriptions-item>
            <el-descriptions-item label="业务员">{{ billInfo.userName }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ billInfo.phone }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">
              <el-input v-model="billInfo.remark" size="small" placeholder="请输入备注" />
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="审核信息" name="audit">
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="制单人">张三</el-descriptions-item>
            <el-descriptions-item label="制单时间">2024-03-22 09:00:00</el-descriptions-item>
            <el-descriptions-item label="审核状态">
              <el-tag size="small" :type="billInfo.status === '1' ? 'success' : 'info'">{{ billInfo.statusName }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="审核意见" :span="3">
              <el-input v-model="auditInfo.opinion" type="textarea" :rows="2" size="small" placeholder="备注..." />
            </el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
      </el-tabs>
    </CollapsibleCard>

    <!-- 3. 明细表格 -->
    <TableCard>
      <el-tabs v-model="activeDetailTab" class="tech-tabs-fill">
        <el-tab-pane label="物料明细清单" name="items">
          <div class="tab-pane-content">
            <div class="table-toolbar">
              <el-button type="primary" icon="Plus" size="small" @click="addItem">添加物料行</el-button>
              <el-button 
                type="danger" 
                icon="Delete" 
                size="small" 
                :disabled="!selectedItems.length" 
                @click="handleBatchDelete"
              >
                批量删除 ({{ selectedItems.length }})
              </el-button>
            </div>
            <div class="table-container">
              <el-table 
                :data="itemDetails" 
                border 
                height="100%" 
                size="small" 
                class="bill-table"
                @selection-change="handleItemSelectionChange"
              >
                <el-table-column type="selection" width="45" align="center" />
                <el-table-column type="index" width="45" label="序号" align="center" />
                <el-table-column label="物料编码" prop="itemCode" width="120" />
                <el-table-column label="物料名称" prop="itemName" min-width="150" />
                <el-table-column label="单位" prop="unit" width="70" align="center" />
                <el-table-column label="数量" prop="qty" width="110" align="right">
                  <template #default="scope">
                    <el-input-number v-model="scope.row.qty" :controls="false" size="small" style="width: 100%" />
                  </template>
                </el-table-column>
                <el-table-column label="含税单价" prop="price" width="110" align="right">
                  <template #default="scope">
                    <el-input-number v-model="scope.row.price" :controls="false" :precision="2" size="small" style="width: 100%" />
                  </template>
                </el-table-column>
                <el-table-column label="金额" width="130" align="right">
                  <template #default="scope">
                    <span class="amount-text">¥ {{ (scope.row.qty * scope.row.price).toLocaleString() }}</span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-tab-pane>
        <el-tab-pane label="付款记录" name="payments">
          <div class="tab-pane-content">
            <div class="table-container">
              <el-table :data="paymentDetails" border height="100%" size="small">
                <el-table-column label="结算账户" prop="account" />
                <el-table-column label="支付日期" prop="payDate" align="center" />
                <el-table-column label="支付金额" prop="payAmount" align="right" />
              </el-table>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </TableCard>

    <!-- 4. 底部合计 -->
    <FooterSummary :items="summaryData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';
import { 
  DocumentChecked, Check, Printer, Delete, Plus, InfoFilled,
  Collection, Wallet, CreditCard, Money 
} from '@element-plus/icons-vue';
import { ElMessage, ElMessageBox } from 'element-plus';

// 导入业务组件
import DetailHeader from '@/components/Business/DetailHeader.vue';
import CollapsibleCard from '@/components/Business/CollapsibleCard.vue';
import TableCard from '@/components/Business/TableCard.vue';
import FooterSummary, { type SummaryItem } from '@/components/Business/FooterSummary.vue';

const router = useRouter();

// 状态
const headerExpanded = ref(true);
const activeHeaderTab = ref('basic');
const activeDetailTab = ref('items');
const selectedItems = ref<any[]>([]);

const billInfo = reactive({
  billNo: 'PO-20240322-001',
  billDate: '2024-03-22',
  status: '0',
  statusName: '未审核',
  supplierId: 1,
  settleType: '1',
  warehouseId: 1,
  userName: '王经理',
  phone: '13800138000',
  remark: ''
});

const auditInfo = reactive({ opinion: '' });

const itemDetails = ref([
  { itemCode: 'HW-001', itemName: '华为 Mate 60 Pro', unit: '台', qty: 10, price: 6999.00 }
]);

const paymentDetails = ref([
  { account: '招商银行 (8892)', payDate: '2024-03-22', payAmount: '50,000.00' }
]);

// 计算合计栏数据
const summaryData = computed<SummaryItem[]>(() => {
  const totalQty = itemDetails.value.reduce((sum, item) => sum + item.qty, 0).toFixed(2);
  const totalAmount = itemDetails.value.reduce((sum, item) => sum + (item.qty * item.price), 0).toLocaleString();
  
  if (activeDetailTab.value === 'items') {
    return [
      { label: '合计数量', value: totalQty, icon: Collection },
      { label: '合计金额', value: totalAmount, icon: Wallet, type: 'amount', isMoney: true }
    ];
  } else {
    return [
      { label: '累计付款', value: '50,000.00', icon: CreditCard, type: 'payments', isMoney: true },
      { label: '待付余额', value: '19,990.00', icon: Money, type: 'balance', isMoney: true }
    ];
  }
});

// 方法
const handleBack = () => router.back();
const handleSave = (status: string) => {
  billInfo.statusName = status === '1' ? '待审核' : '未审核';
  ElMessage.success('保存成功');
};
const handleAudit = () => ElMessage.success('审核通过');
const addItem = () => {
  itemDetails.value.push({ itemCode: 'NEW', itemName: '新物料', unit: '个', qty: 1, price: 0 });
};

const handleItemSelectionChange = (val: any[]) => {
  selectedItems.value = val;
};

const handleBatchDelete = () => {
  ElMessageBox.confirm('确定删除选中行吗？', '提示', { type: 'warning' }).then(() => {
    // 简单模拟删除逻辑
    const selectedCodes = selectedItems.value.map(item => item.itemCode);
    itemDetails.value = itemDetails.value.filter(item => !selectedCodes.includes(item.itemCode));
    selectedItems.value = [];
    ElMessage.success('删除成功');
  });
};

const getStatusType = (status: string) => ({ '0': 'info', '1': 'warning', '2': 'success' }[status] || 'info');
</script>

<style scoped lang="scss">
.bill-detail-wrapper {
  display: flex;
  flex-direction: column;
  flex: 1;
  height: 0;
  gap: 4px;
  overflow: hidden;
}

.amount-text {
  font-family: 'Consolas', monospace;
  font-weight: 600;
  color: #cf1322;
}
</style>
