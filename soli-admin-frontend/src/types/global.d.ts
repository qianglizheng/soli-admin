
declare global {
  interface Window {
    __APP__: unknown;
  }
}

export interface UserInfo {
  id: number;
  username: string;
  avatar?: string;
  roles: string[];
}

export interface ApiResponse<T = unknown> {
  code: number;
  data: T;
  message: string;
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
