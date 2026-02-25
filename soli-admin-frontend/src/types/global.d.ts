
declare global {
  interface Window {
    __APP__: any;
  }
}

export interface UserInfo {
  id: number;
  username: string;
  avatar?: string;
  roles: string[];
}

export interface ApiResponse<T = any> {
  code: number;
  data: T;
  message: string;
}
