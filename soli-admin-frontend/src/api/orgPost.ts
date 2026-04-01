import request from './request';
import type { ModuleContext } from './moduleCenter';
import type { ApiResponse, PageResult } from '@/types/global';
import type {
  BinaryFlagEnum,
  NormalDisableStatusEnum,
  OrgTypeEnum,
  PostTypeEnum
} from '@/types/enums';

export type YesNo = BinaryFlagEnum;
export type OrgNodeType = 'GROUP' | 'HEADQUARTERS' | 'BRANCH' | 'DEPT' | 'POST';

export interface OrgPostTreeNode {
  nodeKey: string;
  id: number;
  parentNodeKey?: string;
  nodeType: OrgNodeType;
  nodeCode: string;
  nodeName: string;
  status: NormalDisableStatusEnum;
  orgUnitId: number;
  parentPostId: number;
  sort: number;
  employeeCount: number;
  childPostCount: number;
  children?: OrgPostTreeNode[];
}

export interface OrgPostDetail {
  id: number;
  orgUnitId: number;
  parentPostId: number;
  postCode: string;
  postName: string;
  postType?: PostTypeEnum;
  managerUserId?: number;
  managerName?: string;
  orgName?: string;
  orgType?: OrgTypeEnum | OrgNodeType;
  parentNodeName?: string;
  sort: number;
  status: NormalDisableStatusEnum;
  childPostCount: number;
  employeeCount: number;
  note?: string;
}

export interface OrgPostUser {
  id: number;
  userId: number;
  username: string;
  nickname?: string;
  phone?: string;
  email?: string;
  orgPostId: number;
  primaryFlag?: YesNo;
  status?: NormalDisableStatusEnum;
  createTime?: string;
}

export interface OrgPostPageQuery {
  pageNum: number;
  pageSize: number;
  orgUnitId?: number;
  parentPostId?: number;
  postCode?: string;
  postName?: string;
  status?: NormalDisableStatusEnum;
}

export interface OrgPostUserPageQuery {
  pageNum: number;
  pageSize: number;
  orgPostId: number;
  keyword?: string;
  status?: NormalDisableStatusEnum;
}

export interface CreateOrgPostPayload {
  orgUnitId: number;
  parentPostId?: number;
  postCode: string;
  postName: string;
  postType?: PostTypeEnum;
  managerUserId?: number;
  sort?: number;
  status?: NormalDisableStatusEnum;
  note?: string;
}

export interface UpdateOrgPostPayload extends CreateOrgPostPayload {
  id: number;
}

export interface CreateOrgUnitPayload {
  parentId: number;
  orgCode: string;
  orgName: string;
  leaderUserId?: number;
  sort?: number;
  status?: NormalDisableStatusEnum;
  note?: string;
}

export interface UpdateOrgUnitPayload extends CreateOrgUnitPayload {
  id: number;
}

export interface OrgUnitDetail {
  id: number;
  parentId: number;
  orgCode: string;
  orgName: string;
  orgType?: OrgTypeEnum;
  leaderUserId?: number;
  sort: number;
  status: NormalDisableStatusEnum;
  note?: string;
}

export interface OrgPostFormModel {
  id?: number;
  parentNodeKey: string;
  postCode: string;
  postName: string;
  postType: PostTypeEnum;
  managerUserId?: number;
  sort: number;
  status: YesNo;
  note: string;
}

export interface OrgUnitFormModel {
  id?: number;
  parentNodeKey: string;
  orgCode: string;
  orgName: string;
  leaderUserId?: number;
  sort: number;
  status: YesNo;
  note: string;
}

export const POST_TYPE_OPTIONS = [
  { label: '管理岗', value: 'MANAGER' },
  { label: '采购岗', value: 'BUYER' },
  { label: '审核岗', value: 'AUDITOR' },
  { label: '销售经理', value: 'SALES_MANAGER' },
  { label: '销售岗', value: 'SALES' },
  { label: '运营岗', value: 'OPERATION' },
  { label: '财务岗', value: 'FINANCE' }
] as const;

const orgNodeTypeLabelMap: Record<OrgNodeType, string> = {
  BRANCH: '分公司',
  DEPT: '部门',
  GROUP: '集团',
  HEADQUARTERS: '总公司',
  POST: '岗位'
};

const postTypeLabelMap = POST_TYPE_OPTIONS.reduce<Record<string, string>>((result, item) => {
  result[item.value] = item.label;
  return result;
}, {});

export function getOrgNodeTypeLabel(nodeType?: string) {
  if (!nodeType) {
    return '岗位';
  }
  return orgNodeTypeLabelMap[nodeType as OrgNodeType] || nodeType;
}

export function getPostTypeLabel(postType?: string) {
  if (!postType) {
    return '岗位';
  }
  return postTypeLabelMap[postType] || postType;
}

export function getOrgPostTree() {
  return request<ApiResponse<OrgPostTreeNode[]>>({
    method: 'get',
    url: '/sys/org-post/tree'
  });
}

export function getOrgPostDetail(id: number) {
  return request<ApiResponse<OrgPostDetail>>({
    method: 'get',
    url: `/sys/org-post/${id}`
  });
}

export function getOrgPostPage(data: OrgPostPageQuery) {
  return request<ApiResponse<PageResult<OrgPostDetail>>>({
    data,
    method: 'post',
    url: '/sys/org-post/page'
  });
}

export function getOrgPostModuleContext(stateCode?: 'create' | 'edit') {
  return request<ApiResponse<ModuleContext>>({
    method: 'get',
    params: stateCode ? { stateCode } : undefined,
    url: '/sys/org-post/context'
  });
}

export function createOrgPost(data: CreateOrgPostPayload) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/org-post'
  });
}

export function createOrgUnit(data: CreateOrgUnitPayload) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/org-post/org-unit'
  });
}

export function getOrgUnitDetail(id: number) {
  return request<ApiResponse<OrgUnitDetail>>({
    method: 'get',
    url: `/sys/org-post/org-unit/${id}`
  });
}

export function updateOrgUnit(data: UpdateOrgUnitPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/org-post/org-unit'
  });
}

export function deleteOrgUnit(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/org-post/org-unit/${id}`
  });
}

export function updateOrgPost(data: UpdateOrgPostPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/org-post'
  });
}

export function deleteOrgPost(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/org-post/${id}`
  });
}

export function getOrgPostUserPage(data: OrgPostUserPageQuery) {
  return request<ApiResponse<PageResult<OrgPostUser>>>({
    data,
    method: 'post',
    url: '/sys/org-post/user/page'
  });
}

export function getOrgPostUserOptionPage(data: OrgPostUserPageQuery) {
  return request<ApiResponse<PageResult<OrgPostUser>>>({
    data,
    method: 'post',
    url: '/sys/org-post/user/options/page'
  });
}

export function bindOrgPostUsers(data: { orgPostId: number; userIds: number[] }) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/org-post/user/bind'
  });
}

export function unbindOrgPostUsers(data: { orgPostId: number; userIds: number[] }) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/org-post/user/unbind'
  });
}
