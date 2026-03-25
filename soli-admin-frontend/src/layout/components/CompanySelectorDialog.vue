<template>
  <el-dialog
    :model-value="companyStore.selectorVisible"
    :show-close="!companyStore.selectionRequired"
    :close-on-click-modal="false"
    :close-on-press-escape="!companyStore.selectionRequired"
    :width="860"
    append-to-body
    class="company-selector-dialog"
    @update:model-value="handleDialogVisibleChange"
  >
    <template #header>
      <div class="dialog-header">
        <div class="dialog-header__icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="dialog-header__content">
          <h3>选择进入公司</h3>
          <p>登录后请先确认当前业务归属公司，右上角可随时切换。</p>
        </div>
      </div>
    </template>

    <div v-loading="companyStore.loading" class="dialog-body">
      <template v-if="companyStore.companyOptions.length">
        <button
          v-for="company in companyStore.companyOptions"
          :key="company.id"
          :class="['company-card', { 'is-active': companyStore.currentCompany?.id === company.id }]"
          type="button"
          @click="handleSelectCompany(company)"
        >
          <div class="company-card__top">
            <el-tag effect="plain" size="small" type="success">{{ company.typeLabel }}</el-tag>
            <el-icon v-if="companyStore.currentCompany?.id === company.id" class="company-card__check">
              <CircleCheckFilled />
            </el-icon>
          </div>
          <div class="company-card__name">{{ company.nodeName }}</div>
          <div class="company-card__code">编码：{{ company.nodeCode }}</div>
          <div class="company-card__action">
            {{ companyStore.currentCompany?.id === company.id ? '当前已进入' : '选择并进入' }}
          </div>
        </button>
      </template>

      <el-empty v-else description="暂未获取到可选公司">
        <el-button type="primary" @click="handleReloadCompanies">重新加载</el-button>
      </el-empty>
    </div>

    <template v-if="!companyStore.selectionRequired" #footer>
      <div class="dialog-footer">
        <el-button @click="companyStore.closeSelector">取消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { CircleCheckFilled, OfficeBuilding } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { CompanyOption } from '@/store/modules/company';
import { useCompanyStore } from '@/store/modules/company';

defineOptions({
  name: "CompanySelectorDialog"
});

const companyStore = useCompanyStore();

const handleDialogVisibleChange = (visible: boolean) => {
  if (!visible) {
    companyStore.closeSelector();
  }
};

const handleReloadCompanies = async () => {
  try {
    await companyStore.loadCompanies(true);
  } catch (error) {
    console.error(error);
    ElMessage.error('公司列表加载失败，请稍后重试');
  }
};

const handleSelectCompany = (company: CompanyOption) => {
  companyStore.selectCompany(company);
  ElMessage.success(`已进入${company.nodeName}`);
};
</script>

<style scoped lang="scss">
:deep(.company-selector-dialog) {
  .el-dialog {
    border-radius: 22px;
    overflow: hidden;
    background:
      radial-gradient(circle at top left, rgba(64, 158, 255, 0.18), transparent 32%),
      linear-gradient(180deg, #ffffff 0%, #f7fbff 100%);
  }

  .el-dialog__header {
    margin: 0;
    padding: 28px 32px 12px;
  }

  .el-dialog__body {
    padding: 8px 32px 28px;
  }

  .el-dialog__footer {
    padding: 0 32px 28px;
  }
}

.dialog-header {
  display: flex;
  align-items: center;
  gap: 16px;

  &__icon {
    width: 52px;
    height: 52px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #409eff, #36cfc9);
    color: #fff;
    font-size: 24px;
    box-shadow: 0 12px 24px rgba(64, 158, 255, 0.2);
  }

  &__content {
    h3 {
      margin: 0 0 6px;
      font-size: 24px;
      color: #1f2d3d;
      font-weight: 700;
    }

    p {
      margin: 0;
      font-size: 14px;
      color: #6b7280;
      line-height: 1.6;
    }
  }
}

.dialog-body {
  min-height: 240px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.company-card {
  border: 1px solid #dbe7f5;
  border-radius: 18px;
  padding: 18px 20px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.04);

  &:hover {
    transform: translateY(-2px);
    border-color: #8cc5ff;
    box-shadow: 0 16px 30px rgba(64, 158, 255, 0.12);
  }

  &.is-active {
    border-color: #409eff;
    background: linear-gradient(180deg, rgba(64, 158, 255, 0.08), rgba(54, 207, 201, 0.04));
    box-shadow: 0 18px 32px rgba(64, 158, 255, 0.16);
  }

  &__top {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 18px;
  }

  &__check {
    color: #409eff;
    font-size: 18px;
  }

  &__name {
    font-size: 20px;
    font-weight: 700;
    color: #1f2937;
    margin-bottom: 10px;
  }

  &__code {
    font-size: 13px;
    color: #6b7280;
    margin-bottom: 24px;
  }

  &__action {
    font-size: 13px;
    color: #409eff;
    font-weight: 600;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}

@media screen and (max-width: 768px) {
  :deep(.company-selector-dialog) {
    .el-dialog {
      width: calc(100vw - 24px) !important;
      margin: 12px auto !important;
    }

    .el-dialog__header,
    .el-dialog__body,
    .el-dialog__footer {
      padding-left: 18px;
      padding-right: 18px;
    }
  }

  .dialog-body {
    grid-template-columns: 1fr;
  }

  .dialog-header {
    align-items: flex-start;
  }
}
</style>
