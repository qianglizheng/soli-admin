import { defineStore } from 'pinia';
import { getUserInfo, loginUsingUsername } from '@/api/user';
import { useCompanyStore } from '@/store/modules/company';
import { getToken, removeToken, setToken } from '@/utils/auth';
import { getEnumCode } from '@/utils/enum';
import { computed, ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const token = ref<string | undefined>(getToken());
  const name = ref('');
  const avatar = ref('');
  const type = ref('');
  const roles = ref<string[]>([]);
  const infoLoaded = ref(false);
  const isSuperAdmin = computed(() => type.value === '0' || roles.value.includes('admin'));

  const login = async (userInfo: { username: string; password: string; code?: string; captchaUUID?: string }) => {
    const companyStore = useCompanyStore();
    const res = await loginUsingUsername({
      username: userInfo.username,
      password: userInfo.password,
      code: userInfo.code,
      captchaUUID: userInfo.captchaUUID
    });
    token.value = res.data.accessToken;
    setToken(res.data.accessToken);
    infoLoaded.value = false;
    companyStore.reset();
  };

  const getInfo = async () => {
    const companyStore = useCompanyStore();
    const res = await getUserInfo();
    const { username, nickname, avatar: av, type: userType, roles: rs, currentCompanyId } = res.data;
    name.value = nickname || username;
    avatar.value = av || '';
    type.value = getEnumCode(userType) || '';
    roles.value = rs || [];
    infoLoaded.value = true;
    companyStore.syncCurrentCompany(currentCompanyId ?? null);
  };

  const logout = async () => {
    const companyStore = useCompanyStore();
    token.value = '';
    name.value = '';
    avatar.value = '';
    type.value = '';
    roles.value = [];
    infoLoaded.value = false;
    companyStore.reset();
    removeToken();
  };

  return {
    token,
    name,
    avatar,
    type,
    roles,
    infoLoaded,
    isSuperAdmin,
    login,
    getInfo,
    logout
  };
});
