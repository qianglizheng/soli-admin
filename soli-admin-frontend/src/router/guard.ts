
import router from './index';
import { useUserStore } from '@/store/modules/user';
import { getToken } from '@/utils/auth';
import { ElMessage } from 'element-plus';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

NProgress.configure({ showSpinner: false });

const whiteList = ['/login'];

router.beforeEach(async (to, from, next) => {
  NProgress.start();
  const hasToken = getToken();
debugger
  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' });
      NProgress.done();
    } else {
      const userStore = useUserStore();
      const hasRoles = userStore.roles && userStore.roles.length > 0;
      if (hasRoles) {
        next();
      } else {
        try {
          await userStore.getInfo();
          next({ ...to, replace: true });
        } catch (error) {
          await userStore.logout();
          ElMessage.error('Has Error');
          next(`/login?redirect=${to.path}`);
          NProgress.done();
        }
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next();
    } else {
      next(`/login?redirect=${to.path}`);
      NProgress.done();
    }
  }
});

router.afterEach(() => {
  NProgress.done();
});
