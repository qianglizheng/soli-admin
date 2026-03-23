const BILL_TEMPLATE_MOCK_BASE_URL = `${import.meta.env.BASE_URL}mock/purchase/bill-template`;

export type BillTemplateOverviewIcon = 'document' | 'clock' | 'money' | 'finished';
export type BillTemplateOverviewTheme = 'primary' | 'warning' | 'danger' | 'success';
export type BillTemplateOverviewTrendDirection = 'up' | 'down';

export interface BillTemplateOverviewCard {
  key: string;
  label: string;
  value: string;
  unit?: string;
  icon: BillTemplateOverviewIcon;
  theme: BillTemplateOverviewTheme;
  footerText: string;
  trendValue?: string;
  trendDirection?: BillTemplateOverviewTrendDirection;
}

export interface BillTemplateListItem {
  billNo: string;
  billType: string;
  supplierId: string;
  supplierName: string;
  billDate: string;
  userName: string;
  warehouseId: string;
  warehouseName: string;
  totalAmount: string;
  netAmount: string;
  taxAmount: string;
  totalQty: number;
  status: string;
  statusName: string;
}

export interface BillTemplateListMockData {
  overviewCards: BillTemplateOverviewCard[];
  items: BillTemplateListItem[];
}

export interface BillTemplateDetailHeader {
  billNo: string;
  billDate: string;
  status: string;
  statusName: string;
  supplierId: number | null;
  settleType: string;
  deptId: string;
  userName: string;
  warehouseId: number | null;
  currency: string;
  remark: string;
  createByName: string;
}

export interface BillTemplateDetailItemRow {
  itemCode: string;
  itemName: string;
  spec: string;
  unit: string;
  qty: number | null;
  priceExcl: number | null;
  taxRate: number | null;
  note: string;
}

export interface BillTemplateSourceBillRow {
  sourceBillNo: string;
  sourceType: string;
  supplierName: string;
  billDate: string;
  totalAmount: string;
  status: string;
  remark: string;
}

export interface BillTemplateAttachmentRow {
  fileName: string;
  fileType: string;
  fileSize: string;
  uploadUser: string;
  uploadTime: string;
  remark: string;
}

export interface BillTemplateActivity {
  content: string;
  timestamp: string;
  operator: string;
  type?: string;
}

export interface BillTemplateDetailRecord {
  title: string;
  header: BillTemplateDetailHeader;
  items: BillTemplateDetailItemRow[];
  sourceBills: BillTemplateSourceBillRow[];
  attachments: BillTemplateAttachmentRow[];
  activities: BillTemplateActivity[];
}

export interface BillTemplateDetailMockData {
  createTemplate: BillTemplateDetailRecord;
  records: BillTemplateDetailRecord[];
}

const fetchMockJson = async <T>(url: string) => {
  const response = await fetch(url);
  return await response.json() as T;
};

/**
 * 获取单据列表 mock 数据。
 */
export const fetchBillTemplateListData = async () => {
  return await fetchMockJson<BillTemplateListMockData>(`${BILL_TEMPLATE_MOCK_BASE_URL}/bill-list.json`);
};

/**
 * 获取单据详情 mock 数据，未传 billNo 时返回新建模板数据。
 */
export const fetchBillTemplateDetailData = async (billNo?: string) => {
  const data = await fetchMockJson<BillTemplateDetailMockData>(`${BILL_TEMPLATE_MOCK_BASE_URL}/bill-detail.json`);
  if (!billNo) {
    return data.createTemplate;
  }
  return data.records.find((item) => {
    return item.header.billNo === billNo;
  }) || data.createTemplate;
};
