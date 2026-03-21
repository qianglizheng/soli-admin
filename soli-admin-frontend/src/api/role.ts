import request from './request';
import type { ApiResponse, PageResult, SysRole } from '@/types/global';

export interface RolePageQuery {
  pageNum: number;
  pageSize: number;
  roleName?: string;
  roleKey?: string;
  status?: string;
}

export interface CreateRolePayload {
  name: string;
  code: string;
  sort?: string;
  dataScope?: string;
  status?: string;
}

export interface UpdateRolePayload {
  id: number;
  name?: string;
  code?: string;
  sort?: string;
  dataScope?: string;
  status?: string;
}

export function getRolePage(data: RolePageQuery) {
  return request<ApiResponse<PageResult<SysRole>>>({
    data,
    method: 'post',
    url: '/sys/role/page'
  });
}

export function createRole(data: CreateRolePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/role'
  });
}

export function updateRole(data: UpdateRolePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/role'
  });
}

export function deleteRole(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/role/${id}`
  });
}
