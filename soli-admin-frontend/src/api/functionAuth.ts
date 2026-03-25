import request from './request';
import type { ApiResponse } from '@/types/global';
import type { ModuleDetail, ModuleTreeNode } from './moduleCenter';
import type { OrgPostDetail, OrgPostTreeNode } from './orgPost';

export interface FunctionAuthFieldPermission {
  fieldId: number;
  permissionLevel: number;
}

export interface FunctionAuthButtonPermission {
  buttonId: number;
  permissionLevel: number;
}

export interface FunctionAuthConfig {
  orgPostId: number;
  moduleId: number;
  moduleVisible: boolean;
  navVisible: boolean;
  fieldPermissions: FunctionAuthFieldPermission[];
  buttonPermissions: FunctionAuthButtonPermission[];
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

export function saveFunctionAuth(data: FunctionAuthConfig) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/function-auth'
  });
}
