
import router from './index';
import { useUserStore } from '@/store/modules/user';
import { useCompanyStore } from '@/store/modules/company';
import { usePermissionStore } from '@/store/modules/permission';
import { getToken } from '@/utils/auth';
import { ElMessage } from 'element-plus';
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

NProgress.configure({ showSpinner: false });

const whiteList = ['/login'];

router.beforeEach(async (to, from, next) => {
  NProgress.start();
  const hasToken = getToken();

  if (hasToken) {
    if (to.path === '/login') {
      next({ path: '/' });
      NProgress.done();
    } else {
      const userStore = useUserStore();
      const companyStore = useCompanyStore();
      const permissionStore = usePermissionStore();
      try {
        if (!userStore.infoLoaded) {
          await userStore.getInfo();
        }

        if (to.path === '/select-company') {
          if (companyStore.hasSelectedCompany) {
            const redirect = typeof to.query.redirect === 'string' && to.query.redirect ? to.query.redirect : '/';
            next({ path: redirect, replace: true });
            return;
          }
          next();
          return;
        }

        if (!companyStore.hasSelectedCompany) {
          next({
            path: '/select-company',
            query: { redirect: to.fullPath }
          });
          return;
        }

        if (permissionStore.isRoutesLoaded) {
          next();
          return;
        }

        await permissionStore.loadRoutes();
        next({ ...to, replace: true });
      } catch (error) {
        console.error(error);
        await userStore.logout();
        permissionStore.resetRoutes();
        ElMessage.error('Has Error');
        next(`/login?redirect=${to.path}`);
        NProgress.done();
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
