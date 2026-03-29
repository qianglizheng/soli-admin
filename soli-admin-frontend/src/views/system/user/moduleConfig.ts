import type { ModuleButtonFallback, ModuleFieldFallback } from '@/utils/moduleContext';

export type UserFieldCode = 'username' | 'password' | 'nickname' | 'email' | 'phone' | 'sex' | 'type' | 'status';
export type UserButtonCode = 'create' | 'modify' | 'remove';

export const USER_FORM_COMPONENT = 'user_form';

export const userFieldFallbackMap: Record<UserFieldCode, ModuleFieldFallback> = {
  username: {
    label: '用户名称',
    placeholder: '请输入用户名称',
    required: true,
    visible: true
  },
  password: {
    label: '用户密码',
    placeholder: '请输入用户密码',
    required: true,
    visible: true
  },
  nickname: {
    label: '用户昵称',
    placeholder: '请输入用户昵称',
    required: false,
    visible: true
  },
  email: {
    label: '用户邮箱',
    placeholder: '请输入用户邮箱',
    required: false,
    visible: true
  },
  phone: {
    label: '手机号码',
    placeholder: '请输入手机号码',
    required: false,
    visible: true
  },
  sex: {
    label: '性别',
    placeholder: '请选择性别',
    required: false,
    visible: true
  },
  type: {
    label: '类型',
    placeholder: '请选择类型',
    required: false,
    visible: true
  },
  status: {
    label: '状态',
    placeholder: '请选择状态',
    required: false,
    visible: true
  }
};

export const userButtonFallbackMap: Record<UserButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  remove: { disabled: false, label: '删除', visible: true }
};
