import request from './request';
import type { ApiResponse, SysMenuDTO } from '@/types/global';

export interface MenuQueryParams {
  menuName?: string;
  status?: string;
}

export function getMenuTree(params?: MenuQueryParams) {
  return request<ApiResponse<SysMenuDTO[]>>({
    url: '/sys/menu/tree',
    method: 'get',
    params
  });
}

export function addMenu(data: Partial<SysMenuDTO>) {
  return request<ApiResponse<void>>({
    url: '/sys/menu',
    method: 'post',
    data
  });
}

export function updateMenu(id: number, data: Partial<SysMenuDTO>) {
  return request<ApiResponse<void>>({
    url: `/sys/menu/${id}`,
    method: 'put',
    data
  });
}

export function deleteMenu(id: number) {
  return request<ApiResponse<void>>({
    url: `/sys/menu/${id}`,
    method: 'delete'
  });
}
