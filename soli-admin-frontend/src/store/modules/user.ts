
import { defineStore } from 'pinia';
import { loginUsingUsername } from '@/api/user';
import { getToken, setToken, removeToken } from '@/utils/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken(),
    name: '',
    avatar: '',
    roles: [] as string[]
  }),
  actions: {
    // 登录
    async login(userInfo: { username: string; password: string; code?: string; captchaUUID?: string }) {
      const res = await loginUsingUsername({
        username: userInfo.username,
        password: userInfo.password,
        code: userInfo.code,
        captchaUUID: userInfo.captchaUUID
      });
      this.token = res.data.accessToken;
      setToken(res.data.accessToken);
    },
    // 获取用户信息
    async getInfo() {
      // const res = await getUserInfo();
      const res = {
        data: {
          username: 'a',
          avatar: 'a',
          roles: ['a']
        }
      };
      const { username, avatar, roles } = res.data;
      this.name = username;
      this.avatar = avatar || '';
      this.roles = roles;
    },
    // 退出
    async logout() {
      // await logout();
      this.token = '';
      this.roles = [];
      removeToken();
    }
  }
});
