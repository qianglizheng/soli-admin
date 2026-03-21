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
  type?: string;
  roles: string[];
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
  type?: string;
  sex?: string;
  status?: string;
  createTime?: string;
  updateTime?: string;
}

export interface SysMenuDTO {
  id: number;
  name: string;
  parentId: number;
  sort: string;
  path: string;
  component: string;
  type: string;
  perms: string;
  icon: string;
  status: string;
  createTime?: string;
  children?: SysMenuDTO[];
}
