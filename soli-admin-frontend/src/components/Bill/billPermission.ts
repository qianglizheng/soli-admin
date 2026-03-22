export type BillPermissionMode = '读写' | '只读' | '不可见';

export type BillPermissionType = 'fields' | 'buttons';

export interface BillPermissionItem {
  right?: BillPermissionMode;
  fieldLabel?: string;
}

export interface BillPermissionSet {
  fields?: Record<string, BillPermissionItem>;
  buttons?: Record<string, BillPermissionItem>;
}

export interface BillPagePermissions {
  header: Record<string, BillPermissionSet>;
  detail: Record<string, BillPermissionSet>;
}

export const DEFAULT_BILL_PERMISSION_MODE: BillPermissionMode = '读写';

/**
 * 创建一个空的权限对象，避免页面初次渲染时出现空引用。
 */
export const createEmptyBillPermissionSet = (): BillPermissionSet => {
  return {
    fields: {},
    buttons: {}
  };
};

/**
 * 创建一个空的页面权限对象，默认保留标准单头权限节点。
 */
export const createEmptyBillPagePermissions = (): BillPagePermissions => {
  return {
    header: {
      standard: createEmptyBillPermissionSet()
    },
    detail: {}
  };
};

/**
 * 按 fields 或 buttons 读取具体权限项。
 */
const getPermissionItem = (
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  key: string
) => {
  return permissions?.[type]?.[key];
};

/**
 * 读取字段或按钮的权限模式。
 */
export const getPermissionMode = (
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  key: string,
  defaultMode: BillPermissionMode = DEFAULT_BILL_PERMISSION_MODE
): BillPermissionMode => {
  return getPermissionItem(permissions, type, key)?.right || defaultMode;
};

/**
 * 判断字段或按钮是否可见。
 */
export const isPermissionVisible = (
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  key: string,
  defaultMode: BillPermissionMode = DEFAULT_BILL_PERMISSION_MODE
) => {
  return getPermissionMode(permissions, type, key, defaultMode) !== '不可见';
};

/**
 * 判断字段或按钮是否为只读。
 */
export const isPermissionReadonly = (
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  key: string,
  defaultMode: BillPermissionMode = DEFAULT_BILL_PERMISSION_MODE
) => {
  return getPermissionMode(permissions, type, key, defaultMode) === '只读';
};

/**
 * 判断一组字段或按钮中是否至少存在一个可见项。
 */
export const hasAnyVisiblePermission = (
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  keys: string[],
  defaultMode: BillPermissionMode = DEFAULT_BILL_PERMISSION_MODE
) => {
  return keys.some((key) => {
    return isPermissionVisible(permissions, type, key, defaultMode);
  });
};

/**
 * 过滤出权限允许显示的字段或按钮键值。
 */
export const filterVisibleKeys = (
  keys: string[],
  permissions: BillPermissionSet | undefined,
  type: BillPermissionType,
  defaultMode: BillPermissionMode = DEFAULT_BILL_PERMISSION_MODE
) => {
  return keys.filter((key) => {
    return isPermissionVisible(permissions, type, key, defaultMode);
  });
};

/**
 * 读取字段标题，未配置时回退为组件默认标题。
 */
export const getFieldLabel = (
  permissions: BillPermissionSet | undefined,
  key: string,
  defaultLabel: string
) => {
  return permissions?.fields?.[key]?.fieldLabel || defaultLabel;
};

/**
 * 读取按钮标题，未配置时回退为组件默认标题。
 */
export const getButtonLabel = (
  permissions: BillPermissionSet | undefined,
  key: string,
  defaultLabel: string
) => {
  return permissions?.buttons?.[key]?.fieldLabel || defaultLabel;
};

/**
 * 根据可见字段重新归一化列显示设置，避免保留已无权限的列。
 */
export const normalizeVisibleKeys = (
  currentKeys: string[],
  defaultKeys: string[],
  allowedKeys: string[]
) => {
  const nextCurrent = currentKeys.filter((key) => {
    return allowedKeys.includes(key);
  });
  if (nextCurrent.length) {
    return nextCurrent;
  }
  return defaultKeys.filter((key) => {
    return allowedKeys.includes(key);
  });
};

/**
 * 比较两个字符串数组是否完全一致。
 */
export const isSameStringArray = (left: string[], right: string[]) => {
  if (left.length !== right.length) {
    return false;
  }
  return left.every((item, index) => {
    return item === right[index];
  });
};
