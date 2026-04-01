import request from './request';
import type { ApiResponse } from '@/types/global';
import type { ModuleDetail, ModuleTreeNode } from './moduleCenter';
import type { PermissionLevelEnum, PermissionLevelEnumCode } from '@/types/enums';

export interface StateFieldPermission {
  fieldId: number;
  permissionLevel: PermissionLevelEnum;
}

export interface StateButtonPermission {
  buttonId: number;
  permissionLevel: PermissionLevelEnum;
}

export interface StateFieldPermissionPayload {
  fieldId: number;
  permissionLevel: PermissionLevelEnumCode;
}

export interface StateButtonPermissionPayload {
  buttonId: number;
  permissionLevel: PermissionLevelEnumCode;
}

export interface StatePermissionByState {
  stateCode: string;
  fieldPermissions: StateFieldPermission[];
  buttonPermissions: StateButtonPermission[];
}

export interface StatePermissionByStatePayload {
  stateCode: string;
  fieldPermissions: StateFieldPermissionPayload[];
  buttonPermissions: StateButtonPermissionPayload[];
}

export interface StateAuthConfig {
  moduleId: number;
  permissionsByState: StatePermissionByState[];
}

export interface StateAuthConfigPayload {
  moduleId: number;
  permissionsByState: StatePermissionByStatePayload[];
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

export function saveStateAuth(data: StateAuthConfigPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/state-auth'
  });
}
