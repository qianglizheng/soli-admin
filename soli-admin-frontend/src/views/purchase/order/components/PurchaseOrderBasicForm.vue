<template>
  <EnterFocusScope>
    <el-form ref="formRef" label-position="left" :model="model" :rules="rules" label-width="90px" size="small" class="compact-form">
      <el-row :gutter="24">
        <el-col v-if="permissionAccess.isFieldVisible('billDate')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('billDate')" prop="billDate">
            <el-date-picker
              v-model="model.billDate"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="showPlaceholders ? '请选择单据日期' : ''"
              :disabled="permissionAccess.isFieldReadonly('billDate')"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('supplierId')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('supplierId')" prop="supplierId">
            <el-select
              v-model="model.supplierId"
              :placeholder="showPlaceholders ? '请选择供应商' : ''"
              :disabled="permissionAccess.isFieldReadonly('supplierId')"
              style="width: 100%"
            >
              <el-option v-for="item in supplierOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('settleType')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('settleType')" prop="settleType">
            <el-select
              v-model="model.settleType"
              :placeholder="showPlaceholders ? '请选择结算方式' : ''"
              :disabled="permissionAccess.isFieldReadonly('settleType')"
              style="width: 100%"
            >
              <el-option v-for="item in settleTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('warehouseId')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('warehouseId')" prop="warehouseId">
            <el-select
              v-model="model.warehouseId"
              :placeholder="showPlaceholders ? '请选择仓库' : ''"
              :disabled="permissionAccess.isFieldReadonly('warehouseId')"
              style="width: 100%"
            >
              <el-option v-for="item in warehouseOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('userName')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('userName')" prop="userName">
            <el-input
              v-model="model.userName"
              :placeholder="showPlaceholders ? '请输入业务员' : ''"
              :disabled="permissionAccess.isFieldReadonly('userName')"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('currency')" :span="6">
          <el-form-item :label="permissionAccess.getFieldLabel('currency')" prop="currency">
            <el-select
              v-model="model.currency"
              :placeholder="showPlaceholders ? '请选择币种' : ''"
              :disabled="permissionAccess.isFieldReadonly('currency')"
              style="width: 100%"
            >
              <el-option v-for="item in currencyOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="permissionAccess.isFieldVisible('remark')" :span="24">
          <el-form-item :label="permissionAccess.getFieldLabel('remark')" prop="remark">
            <el-input
              v-model="model.remark"
              :placeholder="showPlaceholders ? '请输入备注' : '备注...'"
              :disabled="permissionAccess.isFieldReadonly('remark')"
              type="textarea"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </EnterFocusScope>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type {
  PurchaseOrderCurrency,
  PurchaseOrderSettleType
} from '@/api/purchaseOrder';
import {
  createBillPermissionAccessor,
  type BillPermissionSource
} from '@/components/Bill/billPermission';
import {
  currencyOptions,
  settleTypeOptions,
  supplierOptions,
  warehouseOptions
} from '../purchaseOrderShared';

interface PurchaseOrderBasicFormModel {
  billDate: string;
  supplierId: number | null;
  settleType: PurchaseOrderSettleType | '';
  userName: string;
  warehouseId: number | null;
  currency: PurchaseOrderCurrency;
  remark: string;
}

interface Props {
  model: PurchaseOrderBasicFormModel;
  showPlaceholders?: boolean;
  permissions?: BillPermissionSource;
}

const props = withDefaults(defineProps<Props>(), {
  showPlaceholders: false
});

const formRef = ref<FormInstance>();
const defaultFieldLabels = {
  billDate: '单据日期',
  supplierId: '供应商',
  settleType: '结算方式',
  warehouseId: '仓库',
  userName: '业务员',
  currency: '币种',
  remark: '备注'
} as const;

const permissionAccess = createBillPermissionAccessor(() => props.permissions, {
  fieldLabels: defaultFieldLabels
});

const rules = computed<FormRules<PurchaseOrderBasicFormModel>>(() => {
  const nextRules: FormRules<PurchaseOrderBasicFormModel> = {};
  if (permissionAccess.isFieldVisible('billDate') && !permissionAccess.isFieldReadonly('billDate')) {
    nextRules.billDate = [{ required: true, message: `请选择${permissionAccess.getFieldLabel('billDate')}`, trigger: 'change' }];
  }
  if (permissionAccess.isFieldVisible('supplierId') && !permissionAccess.isFieldReadonly('supplierId')) {
    nextRules.supplierId = [{ required: true, message: `请选择${permissionAccess.getFieldLabel('supplierId')}`, trigger: 'change' }];
  }
  if (permissionAccess.isFieldVisible('settleType') && !permissionAccess.isFieldReadonly('settleType')) {
    nextRules.settleType = [{ required: true, message: `请选择${permissionAccess.getFieldLabel('settleType')}`, trigger: 'change' }];
  }
  if (permissionAccess.isFieldVisible('warehouseId') && !permissionAccess.isFieldReadonly('warehouseId')) {
    nextRules.warehouseId = [{ required: true, message: `请选择${permissionAccess.getFieldLabel('warehouseId')}`, trigger: 'change' }];
  }
  if (permissionAccess.isFieldVisible('userName') && !permissionAccess.isFieldReadonly('userName')) {
    nextRules.userName = [{ required: true, message: `请输入${permissionAccess.getFieldLabel('userName')}`, trigger: 'blur' }];
  }
  if (permissionAccess.isFieldVisible('currency') && !permissionAccess.isFieldReadonly('currency')) {
    nextRules.currency = [{ required: true, message: `请选择${permissionAccess.getFieldLabel('currency')}`, trigger: 'change' }];
  }
  return nextRules;
});

const validate = async () => {
  await formRef.value?.validate();
};

defineExpose({
  validate
});
</script>
