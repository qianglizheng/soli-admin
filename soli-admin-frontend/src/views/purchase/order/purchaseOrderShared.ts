import type {
  PurchaseOrderCurrency,
  PurchaseOrderHeader,
  PurchaseOrderItem,
  PurchaseOrderSettleType,
  PurchaseOrderStatus
} from '@/api/purchaseOrder';

export interface OptionItem {
  label: string;
  value: number | string;
}

export interface PurchaseOrderHeaderDraft {
  billDate: string;
  supplierId: number | null;
  settleType: PurchaseOrderSettleType | '';
  deptId: string;
  userName: string;
  warehouseId: number | null;
  currency: PurchaseOrderCurrency;
  remark: string;
  status: PurchaseOrderStatus;
  statusName: string;
  createByName: string;
}

export const supplierOptions: OptionItem[] = [
  { label: '华为技术有限公司', value: 1 },
  { label: '小米通讯有限公司', value: 2 },
  { label: '宁德时代新能源科技股份有限公司', value: 3 }
];

export const warehouseOptions: OptionItem[] = [
  { label: '深圳一号仓', value: 1 },
  { label: '广州二号仓', value: 2 },
  { label: '上海中心仓', value: 3 }
];

export const settleTypeOptions: OptionItem[] = [
  { label: '电汇', value: 'monthly' },
  { label: '现结', value: 'cash' }
];

export const currencyOptions: OptionItem[] = [
  { label: '人民币 (CNY)', value: 'CNY' },
  { label: '美元 (USD)', value: 'USD' }
];

export const purchaseOrderStatusOptions: Array<{ label: string; value: PurchaseOrderStatus | '' }> = [
  { label: '全部单据', value: '' },
  { label: '未审核', value: 'unaudited' },
  { label: '待审核', value: 'pre_audited' },
  { label: '已审核', value: 'audited' },
  { label: '已发运', value: 'shipped' },
  { label: '已完成', value: 'completed' }
];

export const getStatusType = (status: PurchaseOrderStatus | string) => {
  return ({
    unaudited: 'info',
    pre_audited: 'warning',
    audited: 'primary',
    shipped: 'success',
    completed: 'success'
  }[status] || 'info');
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

export const createDefaultPurchaseOrderHeader = (): PurchaseOrderHeader => ({
  billNo: '',
  billDate: getCurrentDate(),
  status: 'unaudited',
  statusName: '未审核',
  supplierId: null,
  settleType: '',
  deptId: '',
  userName: '',
  warehouseId: null,
  currency: 'CNY',
  remark: '',
  createByName: ''
});

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
