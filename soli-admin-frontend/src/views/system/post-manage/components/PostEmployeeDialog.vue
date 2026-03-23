<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="760px" top="6vh" destroy-on-close>
    <div v-if="mode === 'assign'" class="assign-panel">
      <el-alert
        title="左侧为可选员工，右侧为当前岗位已分配员工。保存后只更新本地 mock 数据。"
        type="info"
        :closable="false"
        show-icon
      />
      <el-transfer
        v-model="localSelectedIds"
        :data="transferData"
        :titles="['可选员工', '已分配员工']"
        filterable
        class="employee-transfer"
      />
    </div>

    <div v-else class="view-panel">
      <el-empty v-if="!employees.length" description="当前岗位暂无员工" />
      <el-table v-else :data="employees" border>
        <el-table-column prop="employeeNo" label="工号" min-width="120" />
        <el-table-column prop="employeeName" label="姓名" min-width="120" />
        <el-table-column prop="companyName" label="所属公司" min-width="140" />
        <el-table-column prop="deptName" label="部门" min-width="140" />
        <el-table-column prop="mobile" label="手机号" min-width="140" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.status === '0' ? 'success' : 'danger'" effect="plain">
              {{ scope.row.status === '0' ? '在岗' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <template #footer>
      <el-button @click="handleCancel">{{ mode === 'assign' ? '取消' : '关闭' }}</el-button>
      <el-button v-if="mode === 'assign'" type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import type { PostManageEmployee } from '../postManageMock';

interface Props {
  modelValue: boolean;
  mode: 'assign' | 'view';
  postName?: string;
  employees: PostManageEmployee[];
  selectedIds?: number[];
}

const props = defineProps<Props>();
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submit', value: number[]): void;
  (e: 'cancel'): void;
}>();

const visible = computed({
  get: () => props.modelValue,
  set: (value: boolean) => emit('update:modelValue', value)
});

const localSelectedIds = ref<number[]>([]);

const transferData = computed(() => {
  return props.employees.map((item) => ({
    key: item.id,
    label: `${item.employeeName} / ${item.employeeNo} / ${item.deptName}`
  }));
});

watch(
  () => [props.modelValue, props.selectedIds],
  ([open]) => {
    if (!open) {
      return;
    }
    localSelectedIds.value = [...(props.selectedIds || [])];
  },
  { deep: true, immediate: true }
);

const dialogTitle = computed(() => {
  if (props.mode === 'assign') {
    return `给岗位分配员工${props.postName ? ` - ${props.postName}` : ''}`;
  }
  return `岗位员工列表${props.postName ? ` - ${props.postName}` : ''}`;
});

const handleCancel = () => {
  emit('cancel');
  visible.value = false;
};

const handleSubmit = () => {
  emit('submit', [...localSelectedIds.value]);
  visible.value = false;
};
</script>

<style scoped>
.assign-panel,
.view-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.employee-transfer {
  width: 100%;
}

:deep(.employee-transfer .el-transfer-panel) {
  width: calc(50% - 28px);
}
</style>
