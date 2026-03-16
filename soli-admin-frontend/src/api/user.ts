
import request from './request';
import type { ApiResponse, UserInfo } from '@/types/global';

export function loginUsingUsername(data: { username: string; password: string }) {
  return request<ApiResponse<{ accessToken: string; refreshToken: string }>>({
    url: '/auth/login-using-username',
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
