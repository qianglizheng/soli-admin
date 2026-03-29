import { defineStore } from 'pinia';
import { ref } from 'vue';
import { storage } from '@/utils/storage';
import {
  createDefaultPurchaseOrderHeaderDraft,
  type PurchaseOrderHeaderDraft
} from '@/views/purchase/order/purchaseOrderShared';

const HEADER_DRAFT_STORAGE_KEY = 'purchaseOrderHeaderDraft';

const normalizeHeaderDraft = (
  draft?: Partial<PurchaseOrderHeaderDraft> | null
): PurchaseOrderHeaderDraft => {
  return {
    ...createDefaultPurchaseOrderHeaderDraft(),
    ...(draft || {})
  };
};

export const usePurchaseOrderStore = defineStore('purchaseOrder', () => {
  const headerDraft = ref<PurchaseOrderHeaderDraft>(
    normalizeHeaderDraft(storage.get(HEADER_DRAFT_STORAGE_KEY))
  );

  const setHeaderDraft = (draft: Partial<PurchaseOrderHeaderDraft>) => {
    headerDraft.value = normalizeHeaderDraft(draft);
    storage.set(HEADER_DRAFT_STORAGE_KEY, headerDraft.value);
  };

  const resetHeaderDraft = (draft?: Partial<PurchaseOrderHeaderDraft>) => {
    headerDraft.value = normalizeHeaderDraft(draft);
    storage.set(HEADER_DRAFT_STORAGE_KEY, headerDraft.value);
  };

  const clearHeaderDraft = () => {
    headerDraft.value = createDefaultPurchaseOrderHeaderDraft();
    storage.remove(HEADER_DRAFT_STORAGE_KEY);
  };

  return {
    headerDraft,
    setHeaderDraft,
    resetHeaderDraft,
    clearHeaderDraft
  };
});
