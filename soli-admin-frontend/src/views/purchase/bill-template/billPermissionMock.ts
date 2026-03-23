import type { BillPagePermissions } from '@/components/Bill/billPermission';

type BillTemplatePermissionPage = 'create' | 'detail';
type BillTemplateBillStatus = '0' | '1' | '2' | '3' | '4';

const BILL_TEMPLATE_PERMISSION_MOCK_BASE_URL = `${import.meta.env.BASE_URL}mock/purchase/bill-template`;

/**
 * 通过静态 JSON 模拟后端接口，按页面与单据状态返回权限数据。
 * 新建页直接复用未审核状态权限。
 */
export const fetchBillTemplatePagePermissions = async (
  page: BillTemplatePermissionPage,
  status?: string
) => {
  const currentStatus = (page === 'create' ? '0' : ((status as BillTemplateBillStatus) || '0')) as BillTemplateBillStatus;
  const mockUrl = `${BILL_TEMPLATE_PERMISSION_MOCK_BASE_URL}/${page}-${currentStatus}.json`;
  const response = await fetch(mockUrl);

  return await response.json() as BillPagePermissions;
};
