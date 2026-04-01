import type {
  PurchaseOrderCurrency,
  PurchaseOrderCurrencyCode,
  PurchaseOrderHeader,
  PurchaseOrderItem,
  PurchaseOrderSettleType,
  PurchaseOrderSettleTypeCode,
  PurchaseOrderStatus,
  PurchaseOrderStatusCode
} from '@/api/purchaseOrder';
import { getEnumCode, getEnumName } from '@/utils/enum';

export interface OptionItem<TValue extends number | string = number | string> {
  label: string;
  value: TValue;
}

export interface PurchaseOrderHeaderDraft {
  billDate: string;
  supplierId: number | null;
  settleType: PurchaseOrderSettleTypeCode | '';
  deptId: string;
  userName: string;
  warehouseId: number | null;
  currency: PurchaseOrderCurrencyCode;
  remark: string;
  status: PurchaseOrderStatusCode;
  statusName: string;
  createByName: string;
}

export interface PurchaseOrderEditableHeader extends PurchaseOrderHeaderDraft {
  id?: number;
  billNo: string;
  supplierName?: string;
  warehouseName?: string;
}

const purchaseOrderStatusNameMap: Record<PurchaseOrderStatusCode, string> = {
  unaudited: '未审核',
  pre_audited: '待审核',
  audited: '已审核',
  shipped: '已发运',
  completed: '已完成'
};

export const supplierOptions: OptionItem<number>[] = [
  { label: '华为技术有限公司', value: 1 },
  { label: '小米通讯有限公司', value: 2 },
  { label: '宁德时代新能源科技股份有限公司', value: 3 }
];

export const warehouseOptions: OptionItem<number>[] = [
  { label: '深圳一号仓', value: 1 },
  { label: '广州二号仓', value: 2 },
  { label: '上海中心仓', value: 3 }
];

export const settleTypeOptions: OptionItem<PurchaseOrderSettleTypeCode>[] = [
  { label: '月结', value: 'monthly' },
  { label: '现结', value: 'cash' }
];

export const currencyOptions: OptionItem<PurchaseOrderCurrencyCode>[] = [
  { label: '人民币 (CNY)', value: 'CNY' }
];

export const purchaseOrderStatusOptions: Array<{ label: string; value: PurchaseOrderStatusCode | '' }> = [
  { label: '全部单据', value: '' },
  { label: '未审核', value: 'unaudited' },
  { label: '待审核', value: 'pre_audited' },
  { label: '已审核', value: 'audited' },
  { label: '已发运', value: 'shipped' },
  { label: '已完成', value: 'completed' }
];

export const resolvePurchaseOrderStatusCode = (
  status?: PurchaseOrderStatus | PurchaseOrderStatusCode | null
): PurchaseOrderStatusCode => {
  return getEnumCode(status) || 'unaudited';
};

export const resolvePurchaseOrderStatusName = (
  status?: PurchaseOrderStatus | PurchaseOrderStatusCode | null,
  fallback?: string
) => {
  if (fallback) {
    return fallback;
  }
  if (status && typeof status === 'object') {
    return getEnumName(status) || purchaseOrderStatusNameMap[status.code];
  }
  return purchaseOrderStatusNameMap[resolvePurchaseOrderStatusCode(status)];
};

export const getStatusType = (status?: PurchaseOrderStatus | PurchaseOrderStatusCode | string | null) => {
  const code = getEnumCode(status) || String(status || '');
  return ({
    unaudited: 'info',
    pre_audited: 'warning',
    audited: 'primary',
    shipped: 'success',
    completed: 'success'
  }[code] || 'info');
};

export const getCurrentDate = () => {
  const date = new Date();
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

export const createDefaultPurchaseOrderHeaderDraft = (): PurchaseOrderHeaderDraft => ({
  billDate: getCurrentDate(),
  supplierId: null,
  settleType: '',
  deptId: '',
  userName: '',
  warehouseId: null,
  currency: 'CNY',
  remark: '',
  status: 'unaudited',
  statusName: '未审核',
  createByName: ''
});

export const createDefaultPurchaseOrderHeader = (): PurchaseOrderEditableHeader => ({
  billNo: '',
  billDate: getCurrentDate(),
  status: 'unaudited',
  statusName: '未审核',
  supplierId: null,
  settleType: 'monthly',
  deptId: '',
  userName: '',
  warehouseId: null,
  currency: 'CNY',
  remark: '',
  createByName: ''
});

export const mapPurchaseOrderHeaderToEditable = (header?: PurchaseOrderHeader | null): PurchaseOrderEditableHeader => {
  if (!header) {
    return createDefaultPurchaseOrderHeader();
  }
  const status = resolvePurchaseOrderStatusCode(header.status);
  return {
    id: header.id,
    billNo: header.billNo || '',
    billDate: header.billDate || getCurrentDate(),
    status,
    statusName: resolvePurchaseOrderStatusName(header.status, header.statusName),
    supplierId: header.supplierId ?? null,
    supplierName: header.supplierName,
    settleType: getEnumCode(header.settleType) || 'monthly',
    deptId: header.deptId || '',
    userName: header.userName || '',
    warehouseId: header.warehouseId ?? null,
    warehouseName: header.warehouseName,
    currency: getEnumCode(header.currency) || 'CNY',
    remark: header.remark || '',
    createByName: header.createByName || ''
  };
};

export const createDefaultPurchaseOrderItem = (): PurchaseOrderItem => ({
  itemCode: '',
  itemName: '',
  spec: '',
  unit: '',
  qty: 0,
  priceExcl: 0,
  taxRate: 13,
  taxAmount: 0,
  totalAmount: 0,
  note: ''
});

export const calcPurchaseOrderItemAmounts = (item: PurchaseOrderItem) => {
  const qty = Number(item.qty || 0);
  const priceExcl = Number(item.priceExcl || 0);
  const taxRate = Number(item.taxRate || 0);
  const taxAmount = Number((qty * priceExcl * taxRate / 100).toFixed(2));
  const totalAmount = Number((qty * priceExcl + taxAmount).toFixed(2));
  return {
    taxAmount,
    totalAmount
  };
};
