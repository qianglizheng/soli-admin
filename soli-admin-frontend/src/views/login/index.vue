<template>
  <div class="login-page">
    <div class="login-page__texture"></div>

    <div class="login-layout">
      <section class="login-hero">
        <div class="login-hero__frame">
          <div class="hero-topbar">
            <div class="hero-brand">
              <img src="@/assets/logo-mini.svg" alt="logo" class="hero-brand__logo" />
              <div class="hero-brand__text">
                <span class="hero-brand__cn">Soli Admin</span>
                <span class="hero-brand__en">Enterprise Backoffice</span>
              </div>
            </div>
            <div class="hero-badge">模块化协同平台</div>
          </div>

          <div class="hero-main">
            <p class="hero-kicker">ORGANIZATION / MODULE / WORKFLOW</p>
            <h1>统一模块与权限配置</h1>
            <p class="hero-description">
              面向多公司、多角色后台场景，统一管理模块、字段标题、按钮与状态权限。
            </p>
          </div>

          <div class="hero-summary">
            <div class="hero-summary__title">核心能力</div>
            <div class="hero-summary__item">
              <el-icon><OfficeBuilding /></el-icon>
              <span>支持多公司业务切换</span>
            </div>
            <div class="hero-summary__item">
              <el-icon><Switch /></el-icon>
              <span>模块、字段、按钮统一驱动</span>
            </div>
            <div class="hero-summary__item">
              <el-icon><DataAnalysis /></el-icon>
              <span>状态权限与业务动作集中管理</span>
            </div>
          </div>
        </div>
      </section>

      <section class="login-panel">
        <div class="login-panel__card">
          <div class="panel-header">
            <div class="panel-header__tag">ACCOUNT SIGN IN</div>
            <h2>欢迎登录</h2>
            <p>输入账号、密码与验证码后进入系统。</p>
          </div>

          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
            <el-form-item prop="username" class="login-form__item">
              <div class="field-label">账号</div>
              <el-input
                v-model="loginForm.username"
                type="text"
                placeholder="请输入账号"
                :prefix-icon="User"
                size="large"
              />
            </el-form-item>

            <el-form-item prop="password" class="login-form__item">
              <div class="field-label">密码</div>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item prop="code" class="login-form__item">
              <div class="field-label field-label--row">
                <span>验证码</span>
                <button type="button" class="field-action" @click="refreshCode">刷新</button>
              </div>
              <div class="captcha-row">
                <el-input
                  v-model="loginForm.code"
                  placeholder="请输入验证码"
                  :prefix-icon="Key"
                  size="large"
                  @keyup.enter="handleLogin"
                />
                <button type="button" class="captcha-box" @click="refreshCode">
                  <img :src="codeUrl" alt="验证码" />
                </button>
              </div>
            </el-form-item>

            <div class="login-assist">
              <div class="login-assist__item">
                <el-icon><UserFilled /></el-icon>
                <span>默认已预填演示账号</span>
              </div>
              <div class="login-assist__item">
                <el-icon><OfficeBuilding /></el-icon>
                <span>登录后需先选择进入公司</span>
              </div>
            </div>

            <el-form-item class="login-form__submit">
              <el-button :loading="loading" type="primary" size="large" class="submit-button" @click="handleLogin">
                进入系统
              </el-button>
            </el-form-item>
          </el-form>

          <div class="panel-footer">
            <div class="panel-footer__item">
              <el-icon><Monitor /></el-icon>
              <span>推荐使用最新版 Chrome 浏览器</span>
            </div>
            <div class="panel-footer__item">
              <el-icon><RefreshRight /></el-icon>
              <span>点击验证码图片可快速刷新</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import {
  DataAnalysis,
  Key,
  Lock,
  Monitor,
  OfficeBuilding,
  RefreshRight,
  Switch,
  User,
  UserFilled
} from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { generateCaptcha } from '@/api/captcha';
import { useUserStore } from '@/store/modules/user';

defineOptions({
  name: 'UserLogin'
});

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();
const loginFormRef = ref<FormInstance>();

const loginForm = reactive({
  username: 'admin',
  password: '123456',
  code: '1234'
});

const loginRules = reactive<FormRules>({
  username: [{ required: true, trigger: 'blur', message: '请输入您的账号' }],
  password: [{ required: true, trigger: 'blur', message: '请输入您的密码' }],
  code: [{ required: true, trigger: 'change', message: '请输入验证码' }]
});

const loading = ref(false);
const codeUrl = ref('');
const captchaUUID = ref('');

const generateCode = async () => {
  const res = await generateCaptcha({ type: 'IMAGE', scene: 'LOGIN' });
  const img = res.data.base64CaptchaImage;
  captchaUUID.value = res.data.captchaUUID;
  codeUrl.value = `data:image/png;base64,${img}`;
};

const refreshCode = () => {
  void generateCode();
};

const handleLogin = async () => {
  if (!loginFormRef.value) {
    return;
  }

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true;
      try {
        await userStore.login({ ...loginForm, captchaUUID: captchaUUID.value });
        const redirect = route.query.redirect as string;
        ElMessage.success('登录成功');
        router.push({
          path: '/select-company',
          query: redirect ? { redirect } : undefined
        });
      } catch (error) {
        console.error(error);
        refreshCode();
      } finally {
        loading.value = false;
      }
    }
  });
};

onMounted(() => {
  refreshCode();
});
</script>

<style scoped lang="scss">
.login-page {
  --page-bg: var(--app-bg-color);
  --ink-strong: #1f2937;
  --ink-normal: #66758a;
  --accent: var(--app-primary);
  --accent-dark: #0958d9;
  --hero-bg: #ffffff;
  --panel-bg: rgba(255, 255, 255, 0.92);
  --panel-line: var(--app-border-color);
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  min-height: 100svh;
  padding: clamp(10px, 2.4vw, 28px);
  overflow: hidden;
  box-sizing: border-box;
  background:
    radial-gradient(circle at top left, rgba(22, 119, 255, 0.12), transparent 28%),
    radial-gradient(circle at bottom right, rgba(0, 21, 41, 0.08), transparent 30%),
    linear-gradient(180deg, #f7f9fc 0%, var(--page-bg) 100%);
}

.login-page__texture {
  position: absolute;
  inset: 0;
  pointer-events: none;
  background-image:
    linear-gradient(rgba(15, 23, 42, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(15, 23, 42, 0.03) 1px, transparent 1px);
  background-size: 28px 28px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.42), transparent 88%);
}

.login-layout {
  position: relative;
  z-index: 1;
  display: grid;
  width: 100%;
  max-width: 1120px;
  grid-template-columns: minmax(0, 1fr) minmax(0, 0.88fr);
  gap: clamp(14px, 2vw, 18px);
  align-items: stretch;
}

.login-hero {
  display: flex;
}

.login-hero__frame {
  position: relative;
  display: flex;
  width: 100%;
  flex-direction: column;
  padding: clamp(22px, 3vw, 30px);
  border-radius: clamp(24px, 3vw, 34px);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background:
    linear-gradient(145deg, rgba(255, 255, 255, 0.06), rgba(255, 255, 255, 0)),
    linear-gradient(160deg, var(--app-sidebar-bg) 0%, #0a2f63 54%, var(--app-primary) 100%);
  box-shadow: 0 28px 64px rgba(0, 21, 41, 0.18);
}

.login-hero__frame::before {
  position: absolute;
  top: -80px;
  right: -70px;
  width: 220px;
  height: 220px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  content: '';
}

.login-hero__frame::after {
  position: absolute;
  left: -50px;
  bottom: -90px;
  width: 190px;
  height: 190px;
  border-radius: 30px;
  transform: rotate(18deg);
  background: rgba(255, 255, 255, 0.08);
  content: '';
}

.login-hero__frame > * {
  position: relative;
  z-index: 1;
}

.hero-topbar {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 32px;
}

.hero-brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.hero-brand__logo {
  display: block;
  width: 38px;
  height: 38px;
}

.hero-brand__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.hero-brand__cn {
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
}

.hero-brand__en {
  color: rgba(255, 255, 255, 0.7);
  font-size: 11px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-badge {
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  color: #ffffff;
  font-size: 12px;
  font-weight: 700;
}

.hero-main {
  max-width: 520px;
}

.hero-kicker {
  margin: 0 0 14px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 12px;
  letter-spacing: 0.22em;
}

.hero-main h1 {
  margin: 0;
  color: #ffffff;
  font-size: clamp(30px, 3.6vw, 46px);
  line-height: 1.1;
  font-weight: 700;
  letter-spacing: -0.03em;
}

.hero-description {
  max-width: 430px;
  margin: 16px 0 0;
  color: rgba(255, 255, 255, 0.82);
  font-size: 14px;
  line-height: 1.8;
}

.hero-summary {
  display: grid;
  gap: 12px;
  margin-top: auto;
  padding-top: 26px;
}

.hero-summary__title {
  color: rgba(255, 255, 255, 0.64);
  font-size: 12px;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.hero-summary__item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
  line-height: 1.6;

  .el-icon {
    display: flex;
    width: 34px;
    height: 34px;
    flex-shrink: 0;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.12);
    font-size: 16px;
  }
}

.login-panel {
  display: flex;
}

.login-panel__card {
  display: flex;
  width: 100%;
  flex-direction: column;
  justify-content: center;
  padding: clamp(22px, 3vw, 34px) clamp(20px, 3vw, 32px) clamp(22px, 3vw, 30px);
  border: 1px solid rgba(255, 255, 255, 0.92);
  border-radius: clamp(24px, 3vw, 34px);
  background: var(--panel-bg);
  backdrop-filter: blur(16px);
  box-shadow: 0 28px 64px rgba(15, 23, 42, 0.1);
}

.panel-header {
  margin-bottom: 26px;
}

.panel-header__tag {
  display: inline-flex;
  align-items: center;
  margin-bottom: 14px;
  padding: 8px 12px;
  border-radius: 999px;
  background: rgba(22, 119, 255, 0.08);
  color: var(--app-primary);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.16em;
}

.panel-header h2 {
  margin: 0 0 8px;
  color: var(--ink-strong);
  font-size: clamp(30px, 4vw, 38px);
  line-height: 1.08;
  font-weight: 700;
}

.panel-header p {
  margin: 0;
  color: var(--ink-normal);
  font-size: 14px;
  line-height: 1.7;
}

.login-form {
  display: flex;
  flex-direction: column;
}

.login-form__item {
  margin-bottom: 18px;

  :deep(.el-form-item__content) {
    display: block;
  }

  :deep(.el-input) {
    --el-input-height: 54px;
  }

  :deep(.el-input__wrapper) {
    border-radius: 18px;
    background: rgba(255, 255, 255, 0.86);
    box-shadow: inset 0 0 0 1px rgba(15, 23, 42, 0.08);
    transition: all 0.2s ease;
  }

  :deep(.el-input__wrapper:hover),
  :deep(.el-input__wrapper.is-focus) {
    box-shadow:
      inset 0 0 0 1px rgba(22, 119, 255, 0.42),
      0 10px 24px rgba(22, 119, 255, 0.08);
  }

  :deep(.el-input__prefix-inner) {
    color: var(--app-primary);
  }
}

.field-label {
  margin-bottom: 10px;
  color: #415062;
  font-size: 13px;
  font-weight: 600;

  &--row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}

.field-action {
  padding: 0;
  border: none;
  background: transparent;
  color: var(--app-primary);
  cursor: pointer;
  font-size: 12px;
  font-weight: 600;
}

.captcha-row {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 126px;
  gap: 12px;
}

.captcha-box {
  height: 54px;
  padding: 0;
  overflow: hidden;
  border: 1px solid rgba(15, 23, 42, 0.08);
  border-radius: 18px;
  background:
    linear-gradient(135deg, rgba(22, 119, 255, 0.08), rgba(255, 255, 255, 0.84)),
    #fff;
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
  cursor: pointer;

  img {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: cover;
    image-rendering: -webkit-optimize-contrast;
  }
}

.login-assist {
  display: grid;
  gap: 10px;
  margin: 2px 0 20px;
  padding: 14px 16px;
  border: 1px solid rgba(22, 119, 255, 0.08);
  border-radius: 22px;
  background: linear-gradient(180deg, rgba(22, 119, 255, 0.05), rgba(22, 119, 255, 0.02));

  &__item {
    display: flex;
    align-items: center;
    gap: 10px;
    color: var(--ink-normal);
    font-size: 13px;

    .el-icon {
      color: var(--app-primary);
    }
  }
}

.login-form__submit {
  margin-bottom: 0;

  :deep(.el-form-item__content) {
    width: 100%;
  }
}

.submit-button {
  width: 100%;
  height: 54px;
  border: none;
  border-radius: 18px;
  background: linear-gradient(90deg, #0958d9 0%, var(--app-primary) 100%);
  box-shadow: 0 18px 30px rgba(22, 119, 255, 0.24);
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 0.14em;
  transition: transform 0.2s ease, box-shadow 0.2s ease, opacity 0.2s ease;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 22px 34px rgba(22, 119, 255, 0.28);
    opacity: 0.97;
  }
}

.panel-footer {
  display: flex;
  flex-wrap: wrap;
  gap: 12px 16px;
  margin-top: 18px;
  padding-top: 16px;
  border-top: 1px solid var(--panel-line);
}

.panel-footer__item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--ink-normal);
  font-size: 12px;

  .el-icon {
    color: var(--app-primary);
  }
}

@media screen and (max-width: 1080px) {
  .login-layout {
    max-width: 560px;
    grid-template-columns: 1fr;
  }

  .login-hero {
    display: none;
  }
}

@media screen and (max-width: 768px) {
  .login-page {
    padding: 10px;
  }

  .login-panel__card {
    padding: 20px 16px 18px;
    border-radius: 24px;
  }

  .captcha-row {
    grid-template-columns: 1fr;
  }

  .login-form__item {
    margin-bottom: 16px;
  }

  .login-assist {
    margin-bottom: 18px;
    padding: 14px;
  }

  .panel-footer {
    margin-top: 14px;
    padding-top: 14px;
  }
}

@media screen and (max-height: 820px) {
  .hero-topbar {
    margin-bottom: 24px;
  }

  .hero-summary {
    gap: 10px;
    padding-top: 20px;
  }

  .panel-header {
    margin-bottom: 20px;
  }

  .login-form__item {
    margin-bottom: 16px;
  }

  .login-assist {
    margin-bottom: 16px;
    padding: 12px 14px;
  }

  .panel-footer {
    display: none;
  }
}
</style>
