import { defineStore } from 'pinia';
import { ref } from 'vue';
import { storage } from '@/utils/storage';

const HEADER_DRAFT_STORAGE_KEY = 'purchaseBillTemplateHeaderDraft';

const getCurrentDate = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export interface BillTemplateHeaderDraft {
  billDate: string;
  supplierId: number | null;
  settleType: string;
  deptId: string;
  userName: string;
  warehouseId: number | null;
  currency: string;
  remark: string;
  status: string;
  statusName: string;
  createByName: string;
}

export const createDefaultBillTemplateHeaderDraft = (): BillTemplateHeaderDraft => ({
  billDate: getCurrentDate(),
  supplierId: null,
  settleType: '',
  deptId: '',
  userName: '',
  warehouseId: null,
  currency: 'CNY',
  remark: '',
  status: '0',
  statusName: '未审核',
  createByName: ''
});

const normalizeHeaderDraft = (draft?: Partial<BillTemplateHeaderDraft> | null): BillTemplateHeaderDraft => ({
  ...createDefaultBillTemplateHeaderDraft(),
  ...(draft || {})
});

export const useBillTemplateStore = defineStore('billTemplate', () => {
  const headerDraft = ref<BillTemplateHeaderDraft>(normalizeHeaderDraft(storage.get(HEADER_DRAFT_STORAGE_KEY)));

  const setHeaderDraft = (draft: Partial<BillTemplateHeaderDraft>) => {
    headerDraft.value = normalizeHeaderDraft(draft);
    storage.set(HEADER_DRAFT_STORAGE_KEY, headerDraft.value);
  };

  const resetHeaderDraft = (draft?: Partial<BillTemplateHeaderDraft>) => {
    headerDraft.value = normalizeHeaderDraft(draft);
    storage.set(HEADER_DRAFT_STORAGE_KEY, headerDraft.value);
  };

  const clearHeaderDraft = () => {
    headerDraft.value = createDefaultBillTemplateHeaderDraft();
    storage.remove(HEADER_DRAFT_STORAGE_KEY);
  };

  return {
    headerDraft,
    setHeaderDraft,
    resetHeaderDraft,
    clearHeaderDraft
  };
});
