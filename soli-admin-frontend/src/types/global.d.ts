import type {
  NormalDisableStatusValue,
  UserSexValue,
  UserTypeValue,
  YesNoValue
} from '@/types/enums';

declare global {
  interface Window {
    __APP__: unknown;
  }
}

export interface UserInfo {
  id: number;
  username: string;
  nickname?: string;
  avatar?: string;
  type?: UserTypeValue;
  roles: string[];
  currentCompanyId?: number | null;
}

export interface ApiResponse<T = unknown> {
  code: number;
  data: T;
  message: string;
}

export interface PageResult<T> {
  total: number;
  pageNum: number;
  pageSize: number;
  list: T[];
}

export interface SysUser {
  id: number;
  username: string;
  password?: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  type?: UserTypeValue;
  sex?: UserSexValue;
  status?: NormalDisableStatusValue;
  createTime?: string;
  updateTime?: string;
}

export interface SysDictType {
  id: number;
  name: string;
  type: string;
  status?: NormalDisableStatusValue;
  note?: string;
  createTime?: string;
  updateTime?: string;
}

export interface SysDictData {
  id: number;
  dictTypeId: number;
  label: string;
  value: string;
  sort?: string;
  cssClass?: string;
  listClass?: string;
  defaultFlag?: YesNoValue;
  status?: NormalDisableStatusValue;
  note?: string;
  createTime?: string;
  updateTime?: string;
}

export interface SysConfig {
  id: number;
  configName: string;
  configKey: string;
  configValue?: string;
  configType?: string;
  note?: string;
  createTime?: string;
  updateTime?: string;
}
