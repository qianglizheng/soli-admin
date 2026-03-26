<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="login-left-content">
          <h2>Soli Admin</h2>
          <p>新一代企业级后台管理系统</p>
          <img src="@/assets/logo-full.svg" alt="logo" class="login-logo" />
        </div>
      </div>
      <div class="login-form">
        <div class="login-title">
          <h3>欢迎登录</h3>
          <span>Welcome Back</span>
        </div>
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form-content">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" type="text" placeholder="账号" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" size="large"
              show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item prop="code">
            <div class="login-code">
              <el-input v-model="loginForm.code" placeholder="验证码" prefix-icon="Key" size="large" style="width: 60%"
                @keyup.enter="handleLogin" />
              <div class="login-code-img" @click="refreshCode">
                <img :src="codeUrl" alt="验证码" />
              </div>
            </div>
          </el-form-item>
          <el-form-item style="width: 100%">
            <el-button :loading="loading" type="primary" size="large" style="width: 100%" @click="handleLogin">
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/modules/user';
import { ElMessage } from 'element-plus';
import type { FormInstance, FormRules } from 'element-plus';
import { generateCaptcha } from '@/api/captcha';

defineOptions({
  name: "UserLogin"
})

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
  generateCode();
};

generateCode();

const handleLogin = async () => {
  if (!loginFormRef.value) return;

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
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
  background-repeat: no-repeat;
  background-position: center 110px;
  background-size: 100%;
  background-color: #f0f2f5;
}

.login-box {
  display: flex;
  width: 900px;
  height: 500px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.login-left {
  width: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #36cfc9 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;

  .login-left-content {
    text-align: center;
    color: #fff;
    z-index: 1;

    h2 {
      font-size: 32px;
      margin-bottom: 10px;
      font-weight: 600;
    }

    p {
      font-size: 16px;
      margin-bottom: 30px;
      opacity: 0.9;
    }

    .login-logo {
      width: 120px;
      animation: float 3s ease-in-out infinite;
    }
  }

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image: url('https://gw.alipayobjects.com/zos/rmsportal/TVYTbAXWheQpRcWDaDMu.svg');
    background-size: cover;
    opacity: 0.1;
  }
}

.login-form {
  width: 50%;
  padding: 40px 50px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  .login-title {
    margin-bottom: 30px;

    h3 {
      font-size: 24px;
      color: #333;
      margin-bottom: 5px;
      font-weight: 600;
    }

    span {
      color: #999;
      font-size: 14px;
    }
  }

  .login-form-content {
    .el-input {
      --el-input-height: 45px;

      :deep(.el-input__wrapper) {
        background-color: #f5f7fa;
        box-shadow: none;
        border: 1px solid transparent;
        transition: all 0.3s;

        &:hover,
        &.is-focus {
          background-color: #fff;
          border-color: #409EFF;
          box-shadow: 0 0 0 1px #409EFF inset;
        }
      }
    }

    .login-code {
      display: flex;
      justify-content: space-between;
      width: 100%;

      .login-code-img {
        width: 35%;
        height: 45px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        cursor: pointer;
        overflow: hidden;
        background-color: #f5f7fa;
        box-shadow: 0 0 0 1px rgba(64, 158, 255, 0.08) inset;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          image-rendering: -webkit-optimize-contrast;
        }
      }
    }

    .el-button {
      height: 45px;
      font-size: 16px;
      border-radius: 4px;
      font-weight: 500;
      letter-spacing: 2px;
      background: linear-gradient(90deg, #409EFF, #36cfc9);
      border: none;
      transition: all 0.3s;

      &:hover {
        opacity: 0.9;
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
      }
    }
  }
}

@keyframes float {
  0% {
    transform: translateY(0px);
  }

  50% {
    transform: translateY(-15px);
  }

  100% {
    transform: translateY(0px);
  }
}

@media screen and (max-width: 768px) {
  .login-box {
    width: 90%;
    height: auto;
    flex-direction: column;
  }

  .login-left {
    display: none;
  }

  .login-form {
    width: 100%;
    padding: 30px 20px;
  }
}
</style>
