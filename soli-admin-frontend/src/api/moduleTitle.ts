import request from './request';
import type { ApiResponse } from '@/types/global';
import type { ModuleDetail, ModuleTreeNode } from './moduleCenter';

export interface ModuleFieldTitlePayload {
  fieldId: number;
  displayTitle: string;
  placeholder: string;
  helpText: string;
}

export interface ModuleTitleSavePayload {
  moduleId: number;
  fields: ModuleFieldTitlePayload[];
}

export function getModuleTitleTree() {
  return request<ApiResponse<ModuleTreeNode[]>>({
    method: 'get',
    url: '/sys/module-title/tree'
  });
}

export function getModuleTitleDetail(moduleId: number) {
  return request<ApiResponse<ModuleDetail>>({
    method: 'get',
    url: `/sys/module-title/${moduleId}`
  });
}

export function saveModuleTitle(data: ModuleTitleSavePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module-title'
  });
}
