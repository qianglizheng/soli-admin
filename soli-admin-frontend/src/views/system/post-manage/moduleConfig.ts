import type { ModuleButtonFallback, ModuleFieldFallback } from '@/utils/moduleContext';

export type OrgFormFieldCode = 'orgParentNodeKey' | 'orgName' | 'orgCode' | 'leaderUserId' | 'orgSort' | 'orgStatus' | 'orgNote';
export type PostFormFieldCode = 'postParentNodeKey' | 'postName' | 'postCode' | 'postType' | 'managerUserId' | 'postSort' | 'postStatus' | 'postNote';
export type OrgPostButtonCode = 'create' | 'modify' | 'remove' | 'userBind' | 'userUnbind';

export const ORG_FORM_COMPONENT = 'org_form';
export const POST_FORM_COMPONENT = 'post_form';

export const orgFieldFallbackMap: Record<OrgFormFieldCode, ModuleFieldFallback> = {
  orgParentNodeKey: { label: '上级组织', placeholder: '请选择上级组织', required: true, visible: true },
  orgName: { label: '分公司名称', placeholder: '请输入分公司名称', required: true, visible: true },
  orgCode: { label: '分公司编码', placeholder: '请输入分公司编码', required: true, visible: true },
  leaderUserId: { label: '负责人', placeholder: '请输入账号搜索负责人', required: false, visible: true },
  orgSort: { label: '显示顺序', placeholder: '', required: false, visible: true },
  orgStatus: { label: '状态', placeholder: '请选择状态', required: true, visible: true },
  orgNote: { label: '说明', placeholder: '请输入分公司说明', required: false, visible: true }
};

export const postFieldFallbackMap: Record<PostFormFieldCode, ModuleFieldFallback> = {
  postParentNodeKey: { label: '上级节点', placeholder: '请选择上级节点', required: true, visible: true },
  postName: { label: '岗位名称', placeholder: '请输入岗位名称', required: true, visible: true },
  postCode: { label: '岗位编码', placeholder: '请输入岗位编码', required: true, visible: true },
  postType: { label: '岗位类型', placeholder: '请选择岗位类型', required: true, visible: true },
  managerUserId: { label: '岗位负责人', placeholder: '请输入账号搜索负责人', required: false, visible: true },
  postSort: { label: '显示顺序', placeholder: '', required: false, visible: true },
  postStatus: { label: '岗位状态', placeholder: '请选择岗位状态', required: true, visible: true },
  postNote: { label: '岗位说明', placeholder: '请输入岗位说明', required: false, visible: true }
};

export const orgPostButtonFallbackMap: Record<OrgPostButtonCode, ModuleButtonFallback> = {
  create: { disabled: false, label: '新增', visible: true },
  modify: { disabled: false, label: '修改', visible: true },
  remove: { disabled: false, label: '删除', visible: true },
  userBind: { disabled: false, label: '绑定员工', visible: true },
  userUnbind: { disabled: false, label: '解绑员工', visible: true }
};
