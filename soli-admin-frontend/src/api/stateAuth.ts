import request from './request';
import type { ApiResponse } from '@/types/global';
import type { ModuleDetail, ModuleTreeNode } from './moduleCenter';

export interface StateFieldPermission {
  fieldId: number;
  permissionLevel: number;
}

export interface StateButtonPermission {
  buttonId: number;
  permissionLevel: number;
}

export interface StatePermissionByState {
  stateCode: string;
  fieldPermissions: StateFieldPermission[];
  buttonPermissions: StateButtonPermission[];
}

export interface StateAuthConfig {
  moduleId: number;
  permissionsByState: StatePermissionByState[];
}

export interface StateAuthPageDetail {
  moduleDetail: ModuleDetail;
  config: StateAuthConfig;
}

export function getStateAuthTree() {
  return request<ApiResponse<ModuleTreeNode[]>>({
    method: 'get',
    url: '/sys/state-auth/tree'
  });
}

export function getStateAuthDetail(moduleId: number) {
  return request<ApiResponse<StateAuthPageDetail>>({
    method: 'get',
    url: `/sys/state-auth/${moduleId}`
  });
}

export function saveStateAuth(data: StateAuthConfig) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/state-auth'
  });
}
