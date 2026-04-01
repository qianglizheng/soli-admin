import type {
  NormalDisableStatusEnum,
  UserSexEnum,
  UserTypeEnum,
  YesNoEnum
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
  type?: UserTypeEnum;
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
  type?: UserTypeEnum;
  sex?: UserSexEnum;
  status?: NormalDisableStatusEnum;
  createTime?: string;
  updateTime?: string;
}

export interface SysDictType {
  id: number;
  name: string;
  type: string;
  status?: NormalDisableStatusEnum;
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
  defaultFlag?: YesNoEnum;
  status?: NormalDisableStatusEnum;
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
