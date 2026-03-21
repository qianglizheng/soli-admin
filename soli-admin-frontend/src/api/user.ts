import request from './request';
import type { ApiResponse, PageResult, SysRole, SysUser, UserInfo } from '@/types/global';

export interface UserPageQuery {
  pageNum: number;
  pageSize: number;
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
  roleIds?: number[];
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
  roleIds?: number[];
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

export function getUserPage(data: UserPageQuery) {
  return request<ApiResponse<PageResult<SysUser>>>({
    data,
    method: 'post',
    url: '/sys/user/page'
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

export function getUserRoleOptions() {
  return request<ApiResponse<SysRole[]>>({
    method: 'get',
    url: '/sys/user/role-options'
  });
}

export function deleteUser(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/user/${id}`
  });
}
