
import request from './request';
import type { ApiResponse, UserInfo } from '@/types/global';

export function loginUsingUsername(data: { username: string; password: string; code?: string; captchaUUID?: string }) {
  return request<ApiResponse<{ accessToken: string; refreshToken: string }>>({
    data,
    method: 'post',
    url: '/auth/login-using-username'
  });
}

export function getUserInfo() {
  return request<ApiResponse<UserInfo>>({
    method: 'get',
    url: '/user/info'
  });
}

export function logout() {
  return request<ApiResponse>({
    method: 'post',
    url: '/user/logout'
  });
}
