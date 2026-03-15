
import axios, { type AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import { getToken } from '@/utils/auth';

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 5000
});

service.interceptors.request.use(
  config => {
    // const token = getToken();
    const token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzb2xpLWF1dGgiLCJzdWIiOiIxIiwiaWF0IjoxNzczNTUzOTk4LCJleHAiOjE3ODM1NTM5OTd9.ieoDx8sfUbEdldO7_izVyEsPOfKBt_KST-HbuXYQq2s";
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  response => {
    const res = response.data;
    const hasCode = typeof res === 'object' && res !== null && 'code' in res && 'data' in res;
    if (hasCode) {
      if ((res as any).code !== 200) {
        ElMessage.error((res as any).message || 'Error');
        return Promise.reject(new Error((res as any).message || 'Error'));
      }
      return res as any;
    }
    return { code: 200, data: res, message: '' };
  },
  error => {
    ElMessage.error(error.message || 'Request Error');
    return Promise.reject(error);
  }
);

export default function request<T = unknown>(config: AxiosRequestConfig): Promise<T> {
  return service(config) as unknown as Promise<T>;
}
