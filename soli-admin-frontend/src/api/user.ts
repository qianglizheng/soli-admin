
import request from './request';
import type { ApiResponse, UserInfo } from '@/types/global';

export function login(data: any) {
  return request<ApiResponse<{ token: string }>>({
    url: '/user/login',
    method: 'post',
    data
  });
}

export function getUserInfo() {
  return request<ApiResponse<UserInfo>>({
    url: '/user/info',
    method: 'get'
  });
}

export function logout() {
  return request<ApiResponse>({
    url: '/user/logout',
    method: 'post'
  });
}
