
import { defineStore } from 'pinia';
import { login, getUserInfo, logout } from '@/api/user';
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
    async login(userInfo: any) {
      // const res = await login(userInfo);
      const res = {
        data: {
          token: '22'
        }
      };
      this.token = res.data.token;
      setToken(res.data.token);
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
