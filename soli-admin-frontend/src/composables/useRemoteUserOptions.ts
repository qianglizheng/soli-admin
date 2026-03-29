import { ref } from 'vue';
import { getUserDetail, getUserPage } from '@/api/user';

export interface RemoteUserOption {
  id: number;
  label: string;
}

interface UseRemoteUserOptionsOptions {
  pageSize?: number;
}

export function useRemoteUserOptions(options: UseRemoteUserOptionsOptions = {}) {
  const pageSize = options.pageSize || 20;
  const loading = ref(false);
  const userOptions = ref<RemoteUserOption[]>([]);

  const loadUserOptions = async (keyword = '') => {
    loading.value = true;
    try {
      const { data } = await getUserPage({
        pageNum: 1,
        pageSize,
        username: keyword || undefined
      });
      userOptions.value = data.list.map((item) => ({
        id: item.id,
        label: item.nickname ? `${item.nickname} / ${item.username}` : item.username
      }));
    } finally {
      loading.value = false;
    }
  };

  const ensureUserOption = async (userId?: number) => {
    if (!userId || userOptions.value.some((item) => item.id === userId)) {
      return;
    }
    const { data } = await getUserDetail(userId);
    userOptions.value.unshift({
      id: data.id,
      label: data.nickname ? `${data.nickname} / ${data.username}` : data.username
    });
  };

  return {
    ensureUserOption,
    loadUserOptions,
    loading,
    userOptions
  };
}
