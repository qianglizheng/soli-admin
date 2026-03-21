import request from './request';
import type { ApiResponse, PageResult, SysDictData, SysDictType } from '@/types/global';

export interface DictPageQuery {
  pageNum: number;
  pageSize: number;
  name?: string;
  type?: string;
  status?: string;
}

export interface DictPayload {
  name: string;
  type: string;
  status?: string;
  note?: string;
}

export interface DictUpdatePayload extends DictPayload {
  id: number;
}

export interface DictDataPageQuery {
  pageNum: number;
  pageSize: number;
  dictTypeId: number;
  label?: string;
  value?: string;
  status?: string;
}

export interface DictDataPayload {
  dictTypeId: number;
  label: string;
  value: string;
  sort?: string;
  cssClass?: string;
  listClass?: string;
  isDefault?: string;
  status?: string;
  note?: string;
}

export interface DictDataUpdatePayload extends DictDataPayload {
  id: number;
}

export function getDictPage(data: DictPageQuery) {
  return request<ApiResponse<PageResult<SysDictType>>>({
    data,
    method: 'post',
    url: '/sys/dict/page'
  });
}

export function getDictDetail(id: number) {
  return request<ApiResponse<SysDictType>>({
    method: 'get',
    url: `/sys/dict/${id}`
  });
}

export function createDict(data: DictPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/dict'
  });
}

export function updateDict(data: DictUpdatePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/dict'
  });
}

export function deleteDict(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/dict/${id}`
  });
}

export function getDictDataPage(data: DictDataPageQuery) {
  return request<ApiResponse<PageResult<SysDictData>>>({
    data,
    method: 'post',
    url: '/sys/dict/data/page'
  });
}

export function createDictData(data: DictDataPayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'post',
    url: '/sys/dict/data'
  });
}

export function updateDictData(data: DictDataUpdatePayload) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/dict/data'
  });
}

export function deleteDictData(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/dict/data/${id}`
  });
}
