import request from './request';
import type { ApiResponse } from '@/types/global';
import type { ModuleDetail, ModuleTreeNode } from './moduleCenter';
import type { OrgPostDetail, OrgPostTreeNode } from './orgPost';
import type { PermissionLevelValue, PermissionLevelCode } from '@/types/enums';

export interface FunctionAuthFieldPermission {
  fieldId: number;
  permissionLevel: PermissionLevelValue;
}

export interface FunctionAuthButtonPermission {
  buttonId: number;
  permissionLevel: PermissionLevelValue;
}

export interface FunctionAuthFieldPermissionPayload {
  fieldId: number;
  permissionLevel: PermissionLevelCode;
}

export interface FunctionAuthButtonPermissionPayload {
  buttonId: number;
  permissionLevel: PermissionLevelCode;
}

export interface FunctionAuthConfig {
  orgPostId: number;
  moduleId: number;
  moduleVisible: boolean;
  navVisible: boolean;
  fieldPermissions: FunctionAuthFieldPermission[];
  buttonPermissions: FunctionAuthButtonPermission[];
}

export interface FunctionAuthConfigPayload {
  orgPostId: number;
  moduleId: number;
  moduleVisible: boolean;
  navVisible: boolean;
  fieldPermissions: FunctionAuthFieldPermissionPayload[];
  buttonPermissions: FunctionAuthButtonPermissionPayload[];
}

export interface FunctionAuthPageDetail {
  visibleModuleCount: number;
  postDetail: OrgPostDetail;
  moduleDetail: ModuleDetail;
  config: FunctionAuthConfig;
}

export function getFunctionAuthPostTree() {
  return request<ApiResponse<OrgPostTreeNode[]>>({
    method: 'get',
    url: '/sys/function-auth/post-tree'
  });
}

export function getFunctionAuthModuleTree() {
  return request<ApiResponse<ModuleTreeNode[]>>({
    method: 'get',
    url: '/sys/function-auth/module-tree'
  });
}

export function getFunctionAuthDetail(orgPostId: number, moduleId: number) {
  return request<ApiResponse<FunctionAuthPageDetail>>({
    method: 'get',
    params: {
      moduleId,
      orgPostId
    },
    url: '/sys/function-auth/detail'
  });
}

export function saveFunctionAuth(data: FunctionAuthConfigPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/function-auth'
  });
}
