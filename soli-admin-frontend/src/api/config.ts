import request from './request';
import type { ModuleContext } from './moduleCenter';
import type { ApiResponse, PageResult, SysConfig } from '@/types/global';

export interface ConfigPageQuery {
  pageNum: number;
  pageSize: number;
  configName?: string;
  configKey?: string;
  configType?: string;
}

export interface ConfigPayload {
  configName: string;
  configKey: string;
  configValue?: string;
  configType?: string;
  note?: string;
}

export interface ConfigUpdatePayload extends ConfigPayload {
  id: number;
}

export function getConfigPage(data: ConfigPageQuery) {
  return request<ApiResponse<PageResult<SysConfig>>>({
    data,
    method: 'post',
    url: '/sys/config/page'
  });
}

export function getConfigDetail(id: number) {
  return request<ApiResponse<SysConfig>>({
    method: 'get',
    url: `/sys/config/${id}`
  });
}

export function getConfigModuleContext(stateCode?: 'create' | 'edit') {
  return request<ApiResponse<ModuleContext>>({
    method: 'get',
    params: stateCode ? { stateCode } : undefined,
    url: '/sys/config/context'
  });
}

export function createConfig(data: ConfigPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/config'
  });
}

export function updateConfig(data: ConfigUpdatePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/config'
  });
}

export function deleteConfig(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/config/${id}`
  });
}

export function refreshConfigCache() {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: '/sys/config/refresh-cache'
  });
}
