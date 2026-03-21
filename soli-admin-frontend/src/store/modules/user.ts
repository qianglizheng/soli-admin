import { defineStore } from 'pinia';
import { getUserInfo, loginUsingUsername } from '@/api/user';
import { getToken, removeToken, setToken } from '@/utils/auth';
import { computed, ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const token = ref<string | undefined>(getToken());
  const name = ref('');
  const avatar = ref('');
  const type = ref('');
  const roles = ref<string[]>([]);
  const isSuperAdmin = computed(() => type.value === '0' || roles.value.includes('admin'));

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
    const res = await getUserInfo();
    const { username, nickname, avatar: av, type: userType, roles: rs } = res.data;
    name.value = nickname || username;
    avatar.value = av || '';
    type.value = userType || '';
    roles.value = rs || [];
  };

  const logout = async () => {
    token.value = '';
    name.value = '';
    avatar.value = '';
    type.value = '';
    roles.value = [];
    removeToken();
  };

  return {
    token,
    name,
    avatar,
    type,
    roles,
    isSuperAdmin,
    login,
    getInfo,
    logout
  };
});
