import request from './request';
import type { ApiResponse, SysMenuDTO } from '@/types/global';

export interface MenuQueryParams {
  menuName?: string;
  status?: string;
}

export function getMenuTree(params?: MenuQueryParams) {
  return request<ApiResponse<SysMenuDTO[]>>({
    method: 'get',
    params,
    url: '/sys/menu/tree'
  });
}

export function addMenu(data: Partial<SysMenuDTO>) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/menu'
  });
}

export function updateMenu(data: Partial<SysMenuDTO>) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: `/sys/menu`
  });
}

export function deleteMenu(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/menu/${id}`
  });
}
