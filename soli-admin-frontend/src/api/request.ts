
import axios, { type AxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';
import { getToken } from '@/utils/auth';

const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API || '/api',
  timeout: 5000
});

service.interceptors.request.use(
  config => {
    const token = getToken();
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
    if (res.code !== 200) {
      ElMessage.error(res.message || 'Error');
      return Promise.reject(new Error(res.message || 'Error'));
    } else {
      return res;
    }
  },
  error => {
    ElMessage.error(error.message || 'Request Error');
    return Promise.reject(error);
  }
);

export default function request<T = any>(config: AxiosRequestConfig): Promise<T> {
  return service(config) as unknown as Promise<T>;
}
