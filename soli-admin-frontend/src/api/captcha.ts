import request from './request';
import type { ApiResponse } from '@/types/global';

export type CaptchaType = 'IMAGE' | 'SMS' | 'EMAIL';
export type CaptchaScene = 'LOGIN' | 'REGISTER';

export interface CaptchaGenerateRequest {
  type: CaptchaType;
  scene: CaptchaScene;
  target?: string;
}

export interface CaptchaImageDTO {
  captchaUUID: string;
  base64CaptchaImage: string;
}

export function generateCaptcha(data: CaptchaGenerateRequest) {
  return request<ApiResponse<CaptchaImageDTO>>({
    data,
    method: 'post',
    url: '/auth/captcha'
  });
}
