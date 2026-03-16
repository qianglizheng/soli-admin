
import { defineStore } from 'pinia';
import { loginUsingUsername } from '@/api/user';
import { getToken, setToken, removeToken } from '@/utils/auth';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const token = ref<string | undefined>(getToken());
  const name = ref('');
  const avatar = ref('');
  const roles = ref<string[]>([]);

  const login = async (userInfo: { username: string; password: string; code?: string; captchaUUID?: string }) => {
    const res = await loginUsingUsername({
      username: userInfo.username,
      password: userInfo.password,
      code: userInfo.code,
      captchaUUID: userInfo.captchaUUID
    });
    token.value = res.data.accessToken;
    setToken(res.data.accessToken);
  };

  const getInfo = async () => {
    const res = {
      data: {
        username: 'a',
        avatar: 'a',
        roles: ['a']
      }
    };
    const { username, avatar: av, roles: rs } = res.data;
    name.value = username;
    avatar.value = av || '';
    roles.value = rs;
  };

  const logout = async () => {
    token.value = '';
    roles.value = [];
    removeToken();
  };

  return {
    token,
    name,
    avatar,
    roles,
    login,
    getInfo,
    logout
  };
});
