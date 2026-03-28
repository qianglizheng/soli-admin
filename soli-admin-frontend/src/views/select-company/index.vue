<template>
  <div class="select-company-page">
    <div class="select-company-panel">
      <div class="panel-header">
        <div class="panel-header__icon">
          <el-icon><OfficeBuilding /></el-icon>
        </div>
        <div class="panel-header__content">
          <h2>选择进入公司</h2>
          <p>登录成功后，请先确认当前业务所属公司，再进入后台。</p>
        </div>
        <el-button plain @click="handleLogout">退出登录</el-button>
      </div>

      <div v-loading="companyStore.loading" class="panel-body">
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
              {{ companyStore.currentCompany?.id === company.id ? '当前已进入' : '选择并进入后台' }}
            </div>
          </button>
        </template>

        <el-empty v-else description="当前账号暂未配置可进入公司">
          <el-button type="primary" @click="handleReloadCompanies">重新加载</el-button>
        </el-empty>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { CircleCheckFilled, OfficeBuilding } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { useCompanyStore, type CompanyOption } from '@/store/modules/company';
import { usePermissionStore } from '@/store/modules/permission';
import { useUserStore } from '@/store/modules/user';

defineOptions({
  name: 'SelectCompanyPage'
});

const route = useRoute();
const router = useRouter();
const companyStore = useCompanyStore();
const permissionStore = usePermissionStore();
const userStore = useUserStore();

const resolveRedirect = () => {
  const redirect = route.query.redirect;
  if (typeof redirect === 'string' && redirect) {
    return redirect;
  }
  return '/';
};

const handleReloadCompanies = async () => {
  try {
    await companyStore.loadCompanies(true);
  } catch (error) {
    console.error(error);
    ElMessage.error('公司列表加载失败，请稍后重试');
  }
};

const handleSelectCompany = async (company: CompanyOption) => {
  if (companyStore.currentCompany?.id === company.id) {
    router.replace(resolveRedirect());
    return;
  }
  try {
    await companyStore.switchCompany(company);
    permissionStore.resetRoutes();
    ElMessage.success(`已进入${company.nodeName}`);
    router.replace(resolveRedirect());
  } catch (error) {
    console.error(error);
  }
};

const handleLogout = async () => {
  await userStore.logout();
  permissionStore.resetRoutes();
  router.replace('/login');
};

onMounted(() => {
  handleReloadCompanies();
});
</script>

<style scoped lang="scss">
.select-company-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgba(64, 158, 255, 0.18), transparent 32%),
    linear-gradient(180deg, #f4f8fc 0%, #eef5fb 100%);
}

.select-company-panel {
  width: min(960px, 100%);
  border-radius: 28px;
  background: #fff;
  box-shadow: 0 24px 60px rgba(15, 23, 42, 0.08);
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 28px 32px 18px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);

  &__icon {
    width: 56px;
    height: 56px;
    border-radius: 18px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #409eff, #36cfc9);
    color: #fff;
    font-size: 24px;
    box-shadow: 0 14px 28px rgba(64, 158, 255, 0.22);
    flex-shrink: 0;
  }

  &__content {
    flex: 1;

    h2 {
      margin: 0 0 6px;
      font-size: 28px;
      color: #1f2937;
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

.panel-body {
  min-height: 320px;
  padding: 28px 32px 32px;
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

@media screen and (max-width: 768px) {
  .select-company-page {
    padding: 12px;
  }

  .panel-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .panel-body {
    padding-left: 18px;
    padding-right: 18px;
    grid-template-columns: 1fr;
  }
}
</style>
