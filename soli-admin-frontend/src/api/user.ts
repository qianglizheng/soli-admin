import request from './request';
import type { ApiResponse, PageResult, SysUser, UserInfo } from '@/types/global';
import type { ModuleContext } from './moduleCenter';

export interface UserPageQuery {
  pageNum: number;
  pageSize: number;
  nickname?: string;
  username?: string;
  phone?: string;
  status?: string;
}

export interface CreateUserPayload {
  username: string;
  password: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  type?: string;
  sex?: string;
  status?: string;
}

export interface UpdateUserPayload {
  id: number;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  type?: string;
  sex?: string;
  status?: string;
}

export type UserCompanyNodeType = 'HEADQUARTERS' | 'BRANCH';

export interface UserCompanyOption {
  id: number;
  nodeKey: string;
  nodeCode: string;
  nodeName: string;
  nodeType: UserCompanyNodeType;
  sort: number;
}

export function loginUsingUsername(data: { username: string; password: string; code?: string; captchaUUID?: string }) {
  return request<ApiResponse<{ accessToken: string; refreshToken: string }>>({
    data,
    method: 'post',
    url: '/auth/login-using-username'
  });
}

export function getUserInfo() {
  return request<ApiResponse<UserInfo>>({
    method: 'get',
    url: '/user/info'
  });
}

export function getUserCompanyOptions() {
  return request<ApiResponse<UserCompanyOption[]>>({
    method: 'get',
    url: '/user/company/options'
  });
}

export function switchUserCompany(data: { companyId: number }) {
  return request<ApiResponse<{ accessToken: string; refreshToken: string }>>({
    data,
    method: 'put',
    url: '/user/company'
  });
}

export function getUserPage(data: UserPageQuery) {
  return request<ApiResponse<PageResult<SysUser>>>({
    data,
    method: 'post',
    url: '/sys/user/page'
  });
}

export function getUserModuleContext(stateCode?: 'create' | 'edit') {
  return request<ApiResponse<ModuleContext>>({
    method: 'get',
    params: stateCode ? { stateCode } : undefined,
    url: '/sys/user/context'
  });
}

export function createUser(data: CreateUserPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/user'
  });
}

export function updateUser(data: UpdateUserPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/user'
  });
}

export function getUserDetail(id: number) {
  return request<ApiResponse<SysUser>>({
    method: 'get',
    url: `/sys/user/${id}`
  });
}

export function deleteUser(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/user/${id}`
  });
}
