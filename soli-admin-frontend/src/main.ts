
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import ElementPlus from 'element-plus';
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue';

import App from './App.vue';
import router from './router';
import EnterFocusScope from '@/components/Global/EnterFocusScope.vue';
import '@/styles/index.scss';
import '@/styles/common.scss';
import '@/styles/reset.scss'
import '@/router/guard';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(ElementPlus, {
  locale: zhCn,
});

// Register icons globally
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}
app.component('EnterFocusScope', EnterFocusScope);

// --- Mock 权限指令演示 ---
const mockUserPermissions = ['purchase:bill:save', 'purchase:bill:add', 'purchase:bill:export', 'purchase:bill:audit', 'purchase:bill:log', 'purchase:bill:print'];

app.directive('hasPermi', {
  mounted(el, binding) {
    const { value } = binding;
    if (value && Array.isArray(value) && value.length > 0) {
      const hasPermission = value.some(perm => mockUserPermissions.includes(perm));
      if (!hasPermission) {
        el.style.display = 'none'; // 使用隐藏代替移除，更安全
      }
    }
  }
});

app.mount('#app');
