<template>
  <EnterFocusScope>
    <el-form ref="formRef" label-position="left" :model="model" :rules="rules" label-width="90px" size="small" class="compact-form">
      <el-row :gutter="24">
        <el-col v-if="isFieldVisible('billDate')" :span="6">
          <el-form-item :label="getFieldTitle('billDate')" prop="billDate">
            <el-date-picker
              v-model="model.billDate"
              type="date"
              value-format="YYYY-MM-DD"
              :placeholder="showPlaceholders ? '请选择单据日期' : ''"
              :disabled="isFieldReadonly('billDate')"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('supplierId')" :span="6">
          <el-form-item :label="getFieldTitle('supplierId')" prop="supplierId">
            <el-select
              v-model="model.supplierId"
              :placeholder="showPlaceholders ? '请选择供应商' : ''"
              :disabled="isFieldReadonly('supplierId')"
              style="width: 100%"
            >
              <el-option label="华为技术有限公司" :value="1" />
              <el-option label="小米通讯有限公司" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('settleType')" :span="6">
          <el-form-item :label="getFieldTitle('settleType')" prop="settleType">
            <el-select
              v-model="model.settleType"
              :placeholder="showPlaceholders ? '请选择结算方式' : ''"
              :disabled="isFieldReadonly('settleType')"
              style="width: 100%"
            >
              <el-option label="电汇" value="1" />
              <el-option label="现结" value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('warehouseId')" :span="6">
          <el-form-item :label="getFieldTitle('warehouseId')" prop="warehouseId">
            <el-select
              v-model="model.warehouseId"
              :placeholder="showPlaceholders ? '请选择仓库' : ''"
              :disabled="isFieldReadonly('warehouseId')"
              style="width: 100%"
            >
              <el-option label="深圳一号仓" :value="1" />
              <el-option label="广州二号仓" :value="2" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('userName')" :span="6">
          <el-form-item :label="getFieldTitle('userName')" prop="userName">
            <el-input v-model="model.userName" :placeholder="showPlaceholders ? '请输入业务员' : ''" :disabled="isFieldReadonly('userName')" />
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('currency')" :span="6">
          <el-form-item :label="getFieldTitle('currency')" prop="currency">
            <el-select
              v-model="model.currency"
              :placeholder="showPlaceholders ? '请选择币种' : ''"
              :disabled="isFieldReadonly('currency')"
              style="width: 100%"
            >
              <el-option label="人民币 (CNY)" value="CNY" />
              <el-option label="美元 (USD)" value="USD" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col v-if="isFieldVisible('remark')" :span="24">
          <el-form-item :label="getFieldTitle('remark')">
            <el-input
              v-model="model.remark"
              :placeholder="showPlaceholders ? '请输入备注' : '备注...' "
              :disabled="isFieldReadonly('remark')"
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
import type { FormInstance, FormRules  } from 'element-plus';
import {
  getFieldLabel,
  isPermissionReadonly,
  isPermissionVisible,
  type BillPermissionSet
} from '@/components/Bill/billPermission';

interface BillHeaderBasicFormModel {
  billDate: string;
  supplierId: number | null;
  settleType: string;
  userName: string;
  warehouseId: number | null;
  currency: string;
  remark: string;
}

interface Props {
  model: BillHeaderBasicFormModel;
  showPlaceholders?: boolean;
  permissions?: BillPermissionSet;
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

/**
 * 判断字段是否允许显示。
 */
const isFieldVisible = (key: string) => {
  return isPermissionVisible(props.permissions, 'fields', key);
};

/**
 * 判断字段是否处于只读状态。
 */
const isFieldReadonly = (key: string) => {
  return isPermissionReadonly(props.permissions, 'fields', key);
};

/**
 * 读取字段标题，优先使用权限配置返回的自定义标题。
 */
const getFieldTitle = (key: keyof typeof defaultFieldLabels) => {
  return getFieldLabel(props.permissions, key, defaultFieldLabels[key]);
};

/**
 * 根据字段权限动态生成校验规则，避免只读或隐藏字段仍参与必填校验。
 */
const rules = computed<FormRules<BillHeaderBasicFormModel>>(() => {
  const nextRules: FormRules<BillHeaderBasicFormModel> = {};
  if (isFieldVisible('billDate') && !isFieldReadonly('billDate')) {
    nextRules.billDate = [{ required: true, message: `请选择${getFieldTitle('billDate')}`, trigger: 'change' }];
  }
  if (isFieldVisible('supplierId') && !isFieldReadonly('supplierId')) {
    nextRules.supplierId = [{ required: true, message: `请选择${getFieldTitle('supplierId')}`, trigger: 'change' }];
  }
  if (isFieldVisible('settleType') && !isFieldReadonly('settleType')) {
    nextRules.settleType = [{ required: true, message: `请选择${getFieldTitle('settleType')}`, trigger: 'change' }];
  }
  if (isFieldVisible('warehouseId') && !isFieldReadonly('warehouseId')) {
    nextRules.warehouseId = [{ required: true, message: `请选择${getFieldTitle('warehouseId')}`, trigger: 'change' }];
  }
  if (isFieldVisible('userName') && !isFieldReadonly('userName')) {
    nextRules.userName = [{ required: true, message: `请输入${getFieldTitle('userName')}`, trigger: 'blur' }];
  }
  if (isFieldVisible('currency') && !isFieldReadonly('currency')) {
    nextRules.currency = [{ required: true, message: `请选择${getFieldTitle('currency')}`, trigger: 'change' }];
  }
  return nextRules;
});

/**
 * 对外暴露表单校验能力，供新建页继续按钮统一触发。
 */
const validate = async () => {
  await formRef.value?.validate();
};

defineExpose({
  validate
});
</script>
