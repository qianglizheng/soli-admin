import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { getUserCompanyOptions, switchUserCompany, type UserCompanyNodeType } from '@/api/user';
import type { OrgTypeEnumCode } from '@/types/enums';
import { getEnumCode } from '@/utils/enum';
import { setToken } from '@/utils/auth';
import { storage } from '@/utils/storage';

export interface CompanyOption {
  id: number;
  nodeKey: string;
  nodeCode: string;
  nodeName: string;
  nodeType: UserCompanyNodeType;
  typeLabel: string;
  sort: number;
}

const COMPANY_STORAGE_KEY = 'currentCompanyId';
const COMPANY_CACHE_STORAGE_KEY = 'currentCompanyCache';

const companyTypeLabelMap: Record<OrgTypeEnumCode, string> = {
  GROUP: '集团',
  BRANCH: '分公司',
  HEADQUARTERS: '总公司'
};

export const useCompanyStore = defineStore('company', () => {
  const companyOptions = ref<CompanyOption[]>([]);
  const selectedCompanyId = ref<number | null>(storage.get(COMPANY_STORAGE_KEY) ?? null);
  const cachedCompany = ref<CompanyOption | null>(storage.get(COMPANY_CACHE_STORAGE_KEY));
  const selectorVisible = ref(false);
  const selectionRequired = ref(false);
  const loading = ref(false);
  const loaded = ref(false);

  const currentCompany = computed(() => {
    return companyOptions.value.find((item) => item.id === selectedCompanyId.value) || cachedCompany.value || null;
  });

  const hasSelectedCompany = computed(() => Boolean(selectedCompanyId.value));

  const showSelector = (required = false) => {
    selectorVisible.value = true;
    selectionRequired.value = required;
  };

  const closeSelector = () => {
    if (selectionRequired.value && !selectedCompanyId.value) {
      return;
    }
    selectorVisible.value = false;
    selectionRequired.value = false;
  };

  const clearSelectedCompany = () => {
    selectedCompanyId.value = null;
    cachedCompany.value = null;
    storage.remove(COMPANY_STORAGE_KEY);
    storage.remove(COMPANY_CACHE_STORAGE_KEY);
  };

  const cacheCompany = (company: CompanyOption) => {
    selectedCompanyId.value = company.id;
    cachedCompany.value = company;
    storage.set(COMPANY_STORAGE_KEY, company.id);
    storage.set(COMPANY_CACHE_STORAGE_KEY, company);
  };

  const syncCurrentCompany = (companyId: number | null) => {
    if (!companyId) {
      clearSelectedCompany();
      return;
    }
    selectedCompanyId.value = companyId;
    storage.set(COMPANY_STORAGE_KEY, companyId);
    const matchedCompany = companyOptions.value.find((item) => item.id === companyId);
    if (matchedCompany) {
      cachedCompany.value = matchedCompany;
      storage.set(COMPANY_CACHE_STORAGE_KEY, matchedCompany);
      return;
    }
    if (cachedCompany.value?.id !== companyId) {
      cachedCompany.value = null;
      storage.remove(COMPANY_CACHE_STORAGE_KEY);
    }
  };

  const switchCompany = async (company: CompanyOption) => {
    loading.value = true;
    try {
      const res = await switchUserCompany({ companyId: company.id });
      setToken(res.data.accessToken);
      cacheCompany(company);
      closeSelector();
      return company;
    } finally {
      loading.value = false;
    }
  };

  const loadCompanies = async (force = false) => {
    if (loaded.value && !force) {
      return companyOptions.value;
    }
    loading.value = true;
    try {
      const res = await getUserCompanyOptions();
      const companies = (res.data || []).map((company) => ({
        id: company.id,
        nodeKey: company.nodeKey,
        nodeCode: company.nodeCode,
        nodeName: company.nodeName,
        nodeType: company.nodeType,
        typeLabel: companyTypeLabelMap[getEnumCode(company.nodeType) || 'GROUP'] || getEnumNameFallback(company.nodeType),
        sort: company.sort ?? 0
      })).sort((left, right) => {
        if (left.sort !== right.sort) {
          return left.sort - right.sort;
        }
        return left.nodeName.localeCompare(right.nodeName, 'zh-CN');
      });

      companyOptions.value = companies;
      loaded.value = true;

      if (selectedCompanyId.value) {
        const matchedCompany = companies.find((item) => item.id === selectedCompanyId.value);
        if (matchedCompany) {
          cachedCompany.value = matchedCompany;
          storage.set(COMPANY_CACHE_STORAGE_KEY, matchedCompany);
        } else {
          clearSelectedCompany();
        }
      }

      return companies;
    } finally {
      loading.value = false;
    }
  };

  const getEnumNameFallback = (nodeType: UserCompanyNodeType) => {
    return nodeType.name || getEnumCode(nodeType) || '';
  };

  const openSelector = async (required = false) => {
    showSelector(required);
    await loadCompanies();
  };

  const ensureSelection = async () => {
    await loadCompanies();
    if (!selectedCompanyId.value) {
      showSelector(true);
    }
  };

  const reset = () => {
    companyOptions.value = [];
    selectedCompanyId.value = null;
    cachedCompany.value = null;
    selectorVisible.value = false;
    selectionRequired.value = false;
    loading.value = false;
    loaded.value = false;
    storage.remove(COMPANY_STORAGE_KEY);
    storage.remove(COMPANY_CACHE_STORAGE_KEY);
  };

  return {
    companyOptions,
    currentCompany,
    hasSelectedCompany,
    selectorVisible,
    selectionRequired,
    loading,
    showSelector,
    closeSelector,
    clearSelectedCompany,
    syncCurrentCompany,
    switchCompany,
    loadCompanies,
    openSelector,
    ensureSelection,
    reset
  };
});
