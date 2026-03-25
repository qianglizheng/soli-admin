import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { getOrgNodeTypeLabel, getOrgPostTree, type OrgNodeType, type OrgPostTreeNode } from '@/api/orgPost';
import { storage } from '@/utils/storage';

type CompanyNodeType = Extract<OrgNodeType, 'HEADQUARTERS' | 'BRANCH'>;

export interface CompanyOption {
  id: number;
  nodeKey: string;
  nodeCode: string;
  nodeName: string;
  nodeType: CompanyNodeType;
  typeLabel: string;
  sort: number;
}

const COMPANY_STORAGE_KEY = 'currentCompanyId';
const COMPANY_CACHE_STORAGE_KEY = 'currentCompanyCache';

const isCompanyNodeType = (nodeType?: OrgNodeType): nodeType is CompanyNodeType => {
  return nodeType === 'HEADQUARTERS' || nodeType === 'BRANCH';
};

const collectCompanyOptions = (nodes: OrgPostTreeNode[], result: CompanyOption[] = []) => {
  nodes.forEach((node) => {
    if (isCompanyNodeType(node.nodeType)) {
      result.push({
        id: node.id,
        nodeKey: node.nodeKey,
        nodeCode: node.nodeCode,
        nodeName: node.nodeName,
        nodeType: node.nodeType,
        typeLabel: getOrgNodeTypeLabel(node.nodeType),
        sort: node.sort ?? 0
      });
    }
    if (node.children?.length) {
      collectCompanyOptions(node.children, result);
    }
  });
  return result;
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

  const selectCompany = (company: CompanyOption) => {
    selectedCompanyId.value = company.id;
    cachedCompany.value = company;
    storage.set(COMPANY_STORAGE_KEY, company.id);
    storage.set(COMPANY_CACHE_STORAGE_KEY, company);
    closeSelector();
  };

  const loadCompanies = async (force = false) => {
    if (loaded.value && !force) {
      return companyOptions.value;
    }
    loading.value = true;
    try {
      const res = await getOrgPostTree();
      const companies = collectCompanyOptions(res.data || []).sort((left, right) => {
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
    selectorVisible,
    selectionRequired,
    loading,
    showSelector,
    closeSelector,
    clearSelectedCompany,
    selectCompany,
    loadCompanies,
    openSelector,
    ensureSelection,
    reset
  };
});
