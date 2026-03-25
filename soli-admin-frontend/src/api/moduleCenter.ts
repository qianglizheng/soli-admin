import request from './request';
import type { ApiResponse } from '@/types/global';

export type ModuleType = 'CATALOG' | 'PAGE' | 'BILL';
export type YesNo = '0' | '1';
export type ModuleTabScope = 'HEADER' | 'DETAIL';
export type ModuleButtonArea = 'LIST_TOOLBAR' | 'LIST_ROW_BUTTON' | 'HEADER_TOOLBAR' | 'DETAIL_ROW_BUTTON';

export interface ModuleTreeNode {
  id: number;
  parentId: number;
  moduleCode: string;
  moduleName: string;
  moduleType: ModuleType;
  routePath?: string;
  componentPath?: string;
  icon?: string;
  sort: number;
  navVisible?: YesNo;
  statefulFlag: YesNo;
  status: YesNo;
  children?: ModuleTreeNode[];
}

export interface ModuleFieldDefinition {
  id: number;
  moduleId: number;
  tabId: number;
  fieldScope: ModuleTabScope;
  fieldCode: string;
  defaultTitle: string;
  displayTitle?: string | null;
  placeholder?: string | null;
  helpText?: string | null;
  componentType: string;
  dataPath: string;
  valueType: string;
  requiredFlag: YesNo;
  sort: number;
  status?: YesNo;
  note?: string;
}

export interface ModuleTabInfo {
  id: number;
  moduleId: number;
  tabScope: ModuleTabScope;
  tabCode: string;
  tabName: string;
  sort: number;
  status?: YesNo;
  note?: string;
}

export interface ModuleTabDefinition {
  tabInfo: ModuleTabInfo;
  fields: ModuleFieldDefinition[];
}

export interface ModuleButtonDefinition {
  id: number;
  moduleId: number;
  buttonCode: string;
  defaultTitle: string;
  area: ModuleButtonArea;
  sort: number;
  status?: YesNo;
  note?: string;
}

export interface ModuleStateDefinition {
  id: number;
  moduleId: number;
  stateCode: string;
  stateName: string;
  sort: number;
  isInitial: YesNo;
  isFinal: YesNo;
  status: YesNo;
  note?: string;
}

export interface ModuleStateTransition {
  id: number;
  moduleId: number;
  actionButtonCode: string;
  actionButtonName?: string;
  fromStateCode: string;
  toStateCode: string;
  sort: number;
  status: YesNo;
  note?: string;
}

export interface ModuleDetail {
  id: number;
  parentId: number;
  ancestors?: string;
  moduleCode: string;
  moduleName: string;
  moduleType: ModuleType;
  routePath: string;
  componentPath: string;
  icon: string;
  sort: number;
  navVisible: YesNo;
  statefulFlag: YesNo;
  stateFieldCode: string;
  contextVersion: number;
  status: YesNo;
  note: string;
  headerTabs: ModuleTabDefinition[];
  detailTabs: ModuleTabDefinition[];
  buttons: ModuleButtonDefinition[];
  states: ModuleStateDefinition[];
  transitions: ModuleStateTransition[];
}

export interface ModuleContextPreview {
  moduleCode: string;
  moduleName: string;
  contextVersion: number;
  currentPost?: {
    postId: number;
    postCode: string;
    postName: string;
  };
  state?: {
    currentValue: string;
    currentLabel: string;
    stateField: string;
  } | null;
  headerTabs: Array<{
    tabInfo: ModuleTabInfo;
    visible: boolean;
    fields: Array<{
      fieldCode: string;
      label: string;
      componentType: string;
      visible: boolean;
      editable: boolean;
      required: boolean;
    }>;
  }>;
  detailTabs: Array<{
    tabInfo: ModuleTabInfo;
    visible: boolean;
    fields: Array<{
      fieldCode: string;
      label: string;
      componentType: string;
      visible: boolean;
      editable: boolean;
      required: boolean;
    }>;
  }>;
  buttons: {
    listToolbar: Record<string, { label: string; visible: boolean; disabled: boolean }>;
    listRow: Record<string, { label: string; visible: boolean; disabled: boolean }>;
    headerToolbar: Record<string, { label: string; visible: boolean; disabled: boolean }>;
    detailRow: Record<string, { label: string; visible: boolean; disabled: boolean }>;
  };
}

export interface ModuleFormModel {
  id?: number;
  parentId: number;
  moduleCode: string;
  moduleName: string;
  moduleType: ModuleType;
  routePath: string;
  componentPath: string;
  icon: string;
  sort: number;
  navVisible: YesNo;
  statefulFlag: YesNo;
  stateFieldCode: string;
  status: YesNo;
  note: string;
}

export const moduleTypeLabelMap: Record<ModuleType, string> = {
  BILL: '单据模块',
  CATALOG: '目录',
  PAGE: '页面'
};

export const buttonAreaLabelMap: Record<ModuleButtonArea, string> = {
  DETAIL_ROW_BUTTON: '明细行按钮',
  HEADER_TOOLBAR: '详情顶部按钮',
  LIST_ROW_BUTTON: '列表行按钮',
  LIST_TOOLBAR: '列表顶部按钮'
};

export function getModuleTree() {
  return request<ApiResponse<ModuleTreeNode[]>>({
    method: 'get',
    url: '/sys/module/tree'
  });
}

export function getModuleNavTree() {
  return request<ApiResponse<ModuleTreeNode[]>>({
    method: 'get',
    url: '/sys/module/nav-tree'
  });
}

export function getModuleDetail(id: number) {
  return request<ApiResponse<ModuleDetail>>({
    method: 'get',
    url: `/sys/module/${id}`
  });
}

export function getModuleContextPreview(id: number) {
  return request<ApiResponse<ModuleContextPreview>>({
    method: 'get',
    url: `/sys/module/${id}/context-preview`
  });
}

export function createModule(data: ModuleFormModel) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module'
  });
}

export function updateModule(data: ModuleFormModel & { id: number }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module'
  });
}

export function deleteModule(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/module/${id}`
  });
}

export function createModuleTab(data: Omit<ModuleTabInfo, 'id'> & { note?: string; status?: YesNo }) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module/tab'
  });
}

export function updateModuleTab(data: ModuleTabInfo & { note?: string; status?: YesNo }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module/tab'
  });
}

export function deleteModuleTab(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/module/tab/${id}`
  });
}

export function createModuleField(data: Omit<ModuleFieldDefinition, 'id'> & { note?: string; status?: YesNo }) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module/field'
  });
}

export function updateModuleField(data: ModuleFieldDefinition & { note?: string; status?: YesNo }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module/field'
  });
}

export function deleteModuleField(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/module/field/${id}`
  });
}

export function createModuleButton(data: Omit<ModuleButtonDefinition, 'id'> & { note?: string; status?: YesNo }) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module/button'
  });
}

export function updateModuleButton(data: ModuleButtonDefinition & { note?: string; status?: YesNo }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module/button'
  });
}

export function deleteModuleButton(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/module/button/${id}`
  });
}
