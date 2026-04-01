import request from './request';
import type { ModuleContext } from './moduleCenter';
import type { ApiResponse, PageResult } from '@/types/global';
import type {
  CurrencyEnum,
  CurrencyEnumCode,
  PurchaseOrderActivityTypeEnum,
  PurchaseOrderActivityTypeEnumCode,
  PurchaseOrderSettleTypeEnum,
  PurchaseOrderSettleTypeEnumCode,
  PurchaseOrderSourceStatusEnum,
  PurchaseOrderSourceStatusEnumCode,
  PurchaseOrderSourceTypeEnum,
  PurchaseOrderSourceTypeEnumCode,
  PurchaseOrderStatusEnum,
  PurchaseOrderStatusEnumCode
} from '@/types/enums';

export type PurchaseOrderStatus = PurchaseOrderStatusEnum;
export type PurchaseOrderStatusCode = PurchaseOrderStatusEnumCode;
export type PurchaseOrderSettleType = PurchaseOrderSettleTypeEnum;
export type PurchaseOrderSettleTypeCode = PurchaseOrderSettleTypeEnumCode;
export type PurchaseOrderCurrency = CurrencyEnum;
export type PurchaseOrderCurrencyCode = CurrencyEnumCode;
export type PurchaseOrderSourceType = PurchaseOrderSourceTypeEnum;
export type PurchaseOrderSourceTypeCode = PurchaseOrderSourceTypeEnumCode;
export type PurchaseOrderSourceStatus = PurchaseOrderSourceStatusEnum;
export type PurchaseOrderSourceStatusCode = PurchaseOrderSourceStatusEnumCode;
export type PurchaseOrderActivityType = PurchaseOrderActivityTypeEnum;
export type PurchaseOrderActivityTypeCode = PurchaseOrderActivityTypeEnumCode;

export interface PurchaseOrderOverviewCard {
  key: string;
  label: string;
  value: string;
  unit?: string;
  icon: 'document' | 'clock' | 'money' | 'finished';
  theme: 'primary' | 'warning' | 'danger' | 'success';
  footerText?: string;
  trendValue?: string;
  trendDirection?: 'up' | 'down';
}

export interface PurchaseOrderListItem {
  id: number;
  billNo: string;
  billType: string;
  supplierId: number;
  supplierName: string;
  billDate: string;
  userName: string;
  warehouseId: number;
  warehouseName: string;
  totalAmount: number;
  netAmount: number;
  taxAmount: number;
  totalQty: number;
  status: PurchaseOrderStatus;
  statusName: string;
}

export interface PurchaseOrderHeader {
  id?: number;
  billNo: string;
  billDate: string;
  status: PurchaseOrderStatus;
  statusName: string;
  supplierId: number | null;
  supplierName?: string;
  settleType: PurchaseOrderSettleType;
  deptId?: string;
  userName: string;
  warehouseId: number | null;
  warehouseName?: string;
  currency: PurchaseOrderCurrency;
  remark: string;
  createByName: string;
}

export interface PurchaseOrderItem {
  id?: number;
  itemCode: string;
  itemName: string;
  spec: string;
  unit: string;
  qty: number | null;
  priceExcl: number | null;
  taxRate: number | null;
  taxAmount: number | null;
  totalAmount: number | null;
  note?: string;
}

export interface PurchaseOrderSourceBill {
  id: number;
  sourceBillNo: string;
  sourceType: PurchaseOrderSourceType;
  supplierName: string;
  billDate: string;
  totalAmount: number;
  status: PurchaseOrderSourceStatus;
  remark?: string;
}

export interface PurchaseOrderAttachment {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: string;
  uploadUser: string;
  uploadTime: string;
  remark?: string;
}

export interface PurchaseOrderActivity {
  id: number;
  content: string;
  timestamp: string;
  operator: string;
  type: PurchaseOrderActivityType;
}

export interface PurchaseOrderDetail {
  header: PurchaseOrderHeader;
  items: PurchaseOrderItem[];
  sourceBills: PurchaseOrderSourceBill[];
  attachments: PurchaseOrderAttachment[];
  activities: PurchaseOrderActivity[];
}

export interface PurchaseOrderPageQuery {
  pageNum: number;
  pageSize: number;
  billNo?: string;
  supplierId?: number | null;
  status?: PurchaseOrderStatusCode | '';
  startBillDate?: string;
  endBillDate?: string;
  userName?: string;
  warehouseId?: number | null;
}

export interface PurchaseOrderPageResult {
  overviewCards: PurchaseOrderOverviewCard[];
  page: PageResult<PurchaseOrderListItem>;
}

export interface PurchaseOrderSaveItemPayload {
  id?: number;
  itemCode: string;
  itemName: string;
  spec?: string;
  unit?: string;
  qty: number | null;
  priceExcl: number | null;
  taxRate: number | null;
  note?: string;
}

export interface PurchaseOrderSavePayload {
  id?: number;
  billDate: string;
  supplierId: number | null;
  settleType: PurchaseOrderSettleTypeCode;
  warehouseId: number | null;
  userName: string;
  currency: PurchaseOrderCurrencyCode;
  remark?: string;
  items: PurchaseOrderSaveItemPayload[];
}

export function getPurchaseOrderPage(data: PurchaseOrderPageQuery) {
  return request<ApiResponse<PurchaseOrderPageResult>>({
    data,
    method: 'post',
    url: '/purchase/order/page'
  });
}

export function getPurchaseOrderContext(stateCode?: string) {
  return request<ApiResponse<ModuleContext>>({
    method: 'get',
    params: stateCode ? { stateCode } : undefined,
    url: '/purchase/order/context'
  });
}

export function getPurchaseOrderDetail(id: number) {
  return request<ApiResponse<PurchaseOrderDetail>>({
    method: 'get',
    url: `/purchase/order/${id}`
  });
}

export function createPurchaseOrder(data: PurchaseOrderSavePayload) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/purchase/order'
  });
}

export function updatePurchaseOrder(data: PurchaseOrderSavePayload & { id: number }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/purchase/order'
  });
}

export function executePurchaseOrderAction(id: number, actionCode: 'submit' | 'audit' | 'ship' | 'complete') {
  return request<ApiResponse<void>>({
    method: 'post',
    url: `/purchase/order/${id}/action/${actionCode}`
  });
}
