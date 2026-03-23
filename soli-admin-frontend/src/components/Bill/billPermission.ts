export type BillPermissionType = 'fields' | 'buttons';

export interface BillFieldPermissionItem {
  label?: string;
  visible?: boolean;
  editable?: boolean;
}

export interface BillButtonPermissionItem {
  label?: string;
  visible?: boolean;
  disabled?: boolean;
}

export interface BillPermissionSet {
  fields?: Record<string, BillFieldPermissionItem>;
  buttons?: Record<string, BillButtonPermissionItem>;
}

export interface BillPermissionConfig {
  billType?: string;
  currentState?: string;
  permissions?: BillPermissionSet;
}

export type BillPermissionSource = BillPermissionConfig | BillPermissionSet;

export interface BillPagePermissions {
  header: Record<string, BillPermissionConfig>;
  detail: Record<string, BillPermissionConfig>;
}

const DEFAULT_FIELD_VISIBLE = true;
const DEFAULT_FIELD_EDITABLE = true;
const DEFAULT_BUTTON_VISIBLE = true;
const DEFAULT_BUTTON_DISABLED = false;

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
 * 创建一个空的权限配置对象，补齐单据类型、状态和权限节点。
 */
export const createEmptyBillPermissionConfig = (): BillPermissionConfig => {
  return {
    billType: '',
    currentState: '',
    permissions: createEmptyBillPermissionSet()
  };
};

/**
 * 创建一个空的页面权限对象，默认保留标准单头权限节点。
 */
export const createEmptyBillPagePermissions = (): BillPagePermissions => {
  return {
    header: {
      standard: createEmptyBillPermissionConfig()
    },
    detail: {}
  };
};

const isBillPermissionConfig = (
  permissionSource: BillPermissionSource
): permissionSource is BillPermissionConfig => {
  return 'permissions' in permissionSource;
};

/**
 * 将权限入参统一解析为权限集合，兼容旧的直接透传 fields/buttons 结构。
 */
const resolvePermissionSet = (
  permissionSource: BillPermissionSource | undefined
): BillPermissionSet | undefined => {
  if (!permissionSource) {
    return undefined;
  }
  if (isBillPermissionConfig(permissionSource)) {
    return permissionSource.permissions;
  }
  return permissionSource;
};

/**
 * 按 fields 或 buttons 读取具体权限项。
 */
const getPermissionItem = (
  permissionSource: BillPermissionSource | undefined,
  type: BillPermissionType,
  key: string
) => {
  return resolvePermissionSet(permissionSource)?.[type]?.[key];
};

const getFieldPermissionItem = (
  permissionSource: BillPermissionSource | undefined,
  key: string
) => {
  return resolvePermissionSet(permissionSource)?.fields?.[key];
};

const getButtonPermissionItem = (
  permissionSource: BillPermissionSource | undefined,
  key: string
) => {
  return resolvePermissionSet(permissionSource)?.buttons?.[key];
};

/**
 * 判断字段或按钮是否可见。
 */
export const isPermissionVisible = (
  permissionSource: BillPermissionSource | undefined,
  type: BillPermissionType,
  key: string,
  defaultVisible = type === 'fields' ? DEFAULT_FIELD_VISIBLE : DEFAULT_BUTTON_VISIBLE
) => {
  return getPermissionItem(permissionSource, type, key)?.visible ?? defaultVisible;
};

/**
 * 判断字段是否可编辑。
 */
export const isFieldEditable = (
  permissionSource: BillPermissionSource | undefined,
  key: string,
  defaultEditable = DEFAULT_FIELD_EDITABLE
) => {
  return getFieldPermissionItem(permissionSource, key)?.editable ?? defaultEditable;
};

/**
 * 判断按钮是否禁用。
 */
export const isButtonDisabled = (
  permissionSource: BillPermissionSource | undefined,
  key: string,
  defaultDisabled = DEFAULT_BUTTON_DISABLED
) => {
  return getButtonPermissionItem(permissionSource, key)?.disabled ?? defaultDisabled;
};

/**
 * 兼容旧调用方式。
 * 字段返回只读状态，按钮返回禁用状态。
 */
export const isPermissionReadonly = (
  permissionSource: BillPermissionSource | undefined,
  type: BillPermissionType,
  key: string
) => {
  if (type === 'fields') {
    return !isFieldEditable(permissionSource, key);
  }
  return isButtonDisabled(permissionSource, key);
};

/**
 * 判断一组字段或按钮中是否至少存在一个可见项。
 */
export const hasAnyVisiblePermission = (
  permissionSource: BillPermissionSource | undefined,
  type: BillPermissionType,
  keys: string[]
) => {
  return keys.some((key) => {
    return isPermissionVisible(permissionSource, type, key);
  });
};

/**
 * 过滤出权限允许显示的字段或按钮键值。
 */
export const filterVisibleKeys = (
  keys: string[],
  permissionSource: BillPermissionSource | undefined,
  type: BillPermissionType
) => {
  return keys.filter((key) => {
    return isPermissionVisible(permissionSource, type, key);
  });
};

/**
 * 读取字段标题，未配置时回退为组件默认标题。
 */
export const getFieldLabel = (
  permissionSource: BillPermissionSource | undefined,
  key: string,
  defaultLabel: string
) => {
  return getFieldPermissionItem(permissionSource, key)?.label || defaultLabel;
};

/**
 * 读取按钮标题，未配置时回退为组件默认标题。
 */
export const getButtonLabel = (
  permissionSource: BillPermissionSource | undefined,
  key: string,
  defaultLabel: string
) => {
  return getButtonPermissionItem(permissionSource, key)?.label || defaultLabel;
};

/**
 * 创建统一的权限访问器，避免组件重复包一层字段和按钮判断。
 */
export const createBillPermissionAccessor = <
  FieldKey extends string = string,
  ButtonKey extends string = string
>(
  getPermissionSource: () => BillPermissionSource | undefined,
  options?: {
    fieldLabels?: Partial<Record<FieldKey, string>>;
    buttonLabels?: Partial<Record<ButtonKey, string>>;
  }
) => {
  const fieldLabels = options?.fieldLabels;
  const buttonLabels = options?.buttonLabels;

  return {
    isFieldVisible: (key: FieldKey | string) => {
      return isPermissionVisible(getPermissionSource(), 'fields', String(key));
    },
    isFieldReadonly: (key: FieldKey | string) => {
      return isPermissionReadonly(getPermissionSource(), 'fields', String(key));
    },
    isFieldEditable: (key: FieldKey | string) => {
      return isFieldEditable(getPermissionSource(), String(key));
    },
    getFieldLabel: (key: FieldKey | string) => {
      const fieldKey = String(key);
      const defaultLabel = fieldLabels?.[fieldKey as FieldKey] || fieldKey;
      return getFieldLabel(getPermissionSource(), fieldKey, defaultLabel);
    },
    hasVisibleFields: (keys: Array<FieldKey | string>) => {
      return hasAnyVisiblePermission(getPermissionSource(), 'fields', keys.map((key) => {
        return String(key);
      }));
    },
    filterVisibleFields: (keys: Array<FieldKey | string>) => {
      return filterVisibleKeys(keys.map((key) => {
        return String(key);
      }), getPermissionSource(), 'fields');
    },
    isButtonVisible: (key: ButtonKey | string) => {
      return isPermissionVisible(getPermissionSource(), 'buttons', String(key));
    },
    isButtonReadonly: (key: ButtonKey | string) => {
      return isPermissionReadonly(getPermissionSource(), 'buttons', String(key));
    },
    isButtonDisabled: (key: ButtonKey | string) => {
      return isButtonDisabled(getPermissionSource(), String(key));
    },
    getButtonLabel: (key: ButtonKey | string) => {
      const buttonKey = String(key);
      const defaultLabel = buttonLabels?.[buttonKey as ButtonKey] || buttonKey;
      return getButtonLabel(getPermissionSource(), buttonKey, defaultLabel);
    },
    hasVisibleButtons: (keys: Array<ButtonKey | string>) => {
      return hasAnyVisiblePermission(getPermissionSource(), 'buttons', keys.map((key) => {
        return String(key);
      }));
    },
    filterVisibleButtons: (keys: Array<ButtonKey | string>) => {
      return filterVisibleKeys(keys.map((key) => {
        return String(key);
      }), getPermissionSource(), 'buttons');
    }
  };
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
