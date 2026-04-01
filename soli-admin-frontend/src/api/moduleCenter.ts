import request from './request';
import type { ApiResponse } from '@/types/global';
import type {
  BinaryFlagEnum,
  ModuleComponentTypeEnum,
  ModuleTypeEnum,
  ModuleValueTypeEnum,
  PermissionLevelEnum
} from '@/types/enums';

export type ModuleType = ModuleTypeEnum;
export type YesNo = BinaryFlagEnum;
export type ModuleComponentType = ModuleComponentTypeEnum;
export type ModuleValueType = ModuleValueTypeEnum;
export type PermissionLevel = PermissionLevelEnum;

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
  componentId: number;
  componentCode: string;
  fieldCode: string;
  defaultTitle: string;
  displayTitle?: string | null;
  placeholder?: string | null;
  helpText?: string | null;
  componentType: ModuleComponentType;
  dataPath: string;
  valueType: ModuleValueType;
  requiredFlag: YesNo;
  sort: number;
  status?: YesNo;
  note?: string;
}

export interface ModuleComponentInfo {
  id: number;
  moduleId: number;
  componentCode: string;
  componentName: string;
  sort: number;
  status?: YesNo;
  note?: string;
}

export interface ModuleComponentDefinition {
  componentInfo: ModuleComponentInfo;
  fields: ModuleFieldDefinition[];
}

export interface ModuleButtonDefinition {
  id: number;
  moduleId: number;
  buttonCode: string;
  defaultTitle: string;
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
  components: ModuleComponentDefinition[];
  buttons: ModuleButtonDefinition[];
  states: ModuleStateDefinition[];
  transitions: ModuleStateTransition[];
}

export type ModuleContextConfigType = 'field' | 'button';

export interface ModuleContextConfigItem {
  configKey: string;
  configType: ModuleContextConfigType;
  component: string;
  code: string;
  fieldId?: number;
  buttonId?: number;
  defaultTitle: string;
  displayTitle?: string;
  label: string;
  placeholder?: string;
  helpText?: string;
  componentType: ModuleComponentType;
  dataPath?: string;
  valueType?: ModuleValueType;
  permissionLevel: PermissionLevel;
  visible: boolean;
  editable: boolean;
  required: boolean;
  disabled: boolean;
}

export interface ModuleContext {
  moduleId: number;
  moduleCode: string;
  moduleName: string;
  contextVersion: number;
  statefulFlag: YesNo;
  stateFieldCode: string;
  state?: {
    currentValue: string;
    currentLabel: string;
    stateField: string;
  } | null;
  fieldConfigs: Record<string, ModuleContextConfigItem>;
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
  BILL: '页面',
  CATALOG: '目录',
  PAGE: '页面'
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
  return request<ApiResponse<ModuleContext>>({
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

export function createModuleComponent(data: Omit<ModuleComponentInfo, 'id'> & { note?: string; status?: YesNo }) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module/component'
  });
}

export function updateModuleComponent(data: ModuleComponentInfo & { note?: string; status?: YesNo }) {
  return request<ApiResponse<void>>({
    data,
    method: 'put',
    url: '/sys/module/component'
  });
}

export function deleteModuleComponent(id: number) {
  return request<ApiResponse<void>>({
    method: 'delete',
    url: `/sys/module/component/${id}`
  });
}

export function createModuleField(data: Omit<ModuleFieldDefinition, 'id' | 'componentCode'> & { note?: string; status?: YesNo }) {
  return request<ApiResponse<number>>({
    data,
    method: 'post',
    url: '/sys/module/field'
  });
}

export function updateModuleField(data: Omit<ModuleFieldDefinition, 'componentCode'> & { note?: string; status?: YesNo }) {
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
