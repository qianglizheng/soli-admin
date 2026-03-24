import {
  buttonAreaLabelMap,
  cloneModuleCenterTree,
  findModuleNode,
  type ModuleNode,
  type YesNo
} from '../module-center/moduleCenterMock';

export type StateFieldLimitLevel = 0 | 1 | 2;
export type StateButtonLimitLevel = 0 | 1 | 2;

export interface ModuleStateDefinition {
  id: number;
  stateCode: string;
  stateName: string;
  sort: number;
  isInitial: YesNo;
  isFinal: YesNo;
  status: YesNo;
  note: string;
}

export interface ModuleStateTransition {
  id: number;
  actionButtonCode: string;
  actionButtonName: string;
  fromStateCode: string;
  toStateCode: string;
  sort: number;
  status: YesNo;
  note: string;
}

export interface ModuleStatePermissionConfig {
  fieldPermissions: Record<string, StateFieldLimitLevel>;
  buttonPermissions: Record<string, StateButtonLimitLevel>;
}

export interface ModuleStateAuthConfig {
  states: ModuleStateDefinition[];
  transitions: ModuleStateTransition[];
  permissionsByState: Record<string, ModuleStatePermissionConfig>;
}

export type StateAuthStore = Record<string, ModuleStateAuthConfig>;

const stateFieldLimitLevelOptionsSeed = [
  { label: '隐藏', value: 0 },
  { label: '只读', value: 1 },
  { label: '不收紧', value: 2 }
] as const;

const stateButtonLimitLevelOptionsSeed = [
  { label: '隐藏', value: 0 },
  { label: '禁用', value: 1 },
  { label: '不收紧', value: 2 }
] as const;

const standardStateSeed: Array<Omit<ModuleStateDefinition, 'id'>> = [
  {
    stateCode: 'unaudited',
    stateName: '未审核',
    sort: 1,
    isInitial: '1',
    isFinal: '0',
    status: '0',
    note: '新建和草稿统一归入未审核，允许保留更多编辑能力。'
  },
  {
    stateCode: 'pre_audited',
    stateName: '预审中',
    sort: 2,
    isInitial: '0',
    isFinal: '0',
    status: '0',
    note: '进入预审后开始收紧编辑权限，等待审核动作。'
  },
  {
    stateCode: 'audited',
    stateName: '已审核',
    sort: 3,
    isInitial: '0',
    isFinal: '0',
    status: '0',
    note: '审核完成后以只读为主，保留查看能力。'
  },
  {
    stateCode: 'shipped',
    stateName: '已发运',
    sort: 4,
    isInitial: '0',
    isFinal: '0',
    status: '0',
    note: '发运后禁止继续改写单据主体，状态限制进一步收紧。'
  },
  {
    stateCode: 'completed',
    stateName: '已完成',
    sort: 5,
    isInitial: '0',
    isFinal: '1',
    status: '0',
    note: '完结状态只保留查询和追溯能力。'
  }
];

const standardTransitionSeed: Array<Omit<ModuleStateTransition, 'id'>> = [
  {
    actionButtonCode: 'submit',
    actionButtonName: '提交',
    fromStateCode: 'unaudited',
    toStateCode: 'pre_audited',
    sort: 1,
    status: '0',
    note: '提交后进入预审环节。'
  },
  {
    actionButtonCode: 'audit',
    actionButtonName: '审核',
    fromStateCode: 'pre_audited',
    toStateCode: 'audited',
    sort: 2,
    status: '0',
    note: '审核通过后进入已审核状态。'
  },
  {
    actionButtonCode: 'ship',
    actionButtonName: '发运',
    fromStateCode: 'audited',
    toStateCode: 'shipped',
    sort: 3,
    status: '0',
    note: '发运动作可由模块后续继续补齐按钮映射。'
  },
  {
    actionButtonCode: 'complete',
    actionButtonName: '完成',
    fromStateCode: 'shipped',
    toStateCode: 'completed',
    sort: 4,
    status: '0',
    note: '完结后进入最终状态。'
  }
];

export const stateFieldLimitLevelOptions = [...stateFieldLimitLevelOptionsSeed];

export const stateButtonLimitLevelOptions = [...stateButtonLimitLevelOptionsSeed];

export const stateFieldLimitTagTypeMap: Record<StateFieldLimitLevel, 'danger' | 'warning' | 'success'> = {
  0: 'danger',
  1: 'warning',
  2: 'success'
};

export const stateButtonLimitTagTypeMap: Record<StateButtonLimitLevel, 'danger' | 'warning' | 'success'> = {
  0: 'danger',
  1: 'warning',
  2: 'success'
};

export const stateFieldLimitLabelMap: Record<StateFieldLimitLevel, string> = {
  0: '隐藏',
  1: '只读',
  2: '不收紧'
};

export const stateButtonLimitLabelMap: Record<StateButtonLimitLevel, string> = {
  0: '隐藏',
  1: '禁用',
  2: '不收紧'
};

const moduleTreeSeed = (() => {
  const filterStatefulModules = (nodes: ModuleNode[]): ModuleNode[] => {
    return nodes.reduce<ModuleNode[]>((result, node) => {
      const children = node.children?.length ? filterStatefulModules(node.children) : [];
      if (node.moduleType === 'CATALOG') {
        if (!children.length) {
          return result;
        }
        result.push({
          ...node,
          children
        });
        return result;
      }
      if (node.statefulFlag === '1') {
        result.push({
          ...node,
          children: []
        });
      }
      return result;
    }, []);
  };

  return filterStatefulModules(cloneModuleCenterTree());
})();

const flattenModules = (nodes: ModuleNode[]): ModuleNode[] => {
  return nodes.flatMap((node) => {
    const children = node.children?.length ? flattenModules(node.children) : [];
    return node.moduleType === 'CATALOG' ? children : [node, ...children];
  });
};

const collectFieldCodes = (module: ModuleNode) => {
  return [...module.headerTabs, ...module.detailTabs].flatMap((tab) => {
    return tab.fields.map((field) => field.fieldCode);
  });
};

const collectButtonCodes = (module: ModuleNode) => {
  return module.buttons.map((button) => button.buttonCode);
};

const createDefaultFieldPermissions = (
  module: ModuleNode,
  stateCode: string
): Record<string, StateFieldLimitLevel> => {
  const fieldPermissions = collectFieldCodes(module).reduce<Record<string, StateFieldLimitLevel>>((result, fieldCode) => {
    result[fieldCode] = 2;
    return result;
  }, {});

  const setFieldLevel = (fieldCodes: string[], level: StateFieldLimitLevel) => {
    fieldCodes.forEach((fieldCode) => {
      if (fieldPermissions[fieldCode] !== undefined) {
        fieldPermissions[fieldCode] = level;
      }
    });
  };

  if (stateCode === 'unaudited') {
    setFieldLevel(['auditUserName', 'auditTime'], 0);
  }

  if (stateCode === 'pre_audited') {
    Object.keys(fieldPermissions).forEach((fieldCode) => {
      fieldPermissions[fieldCode] = 1;
    });
    setFieldLevel(['status', 'auditUserName', 'auditTime'], 2);
  }

  if (stateCode === 'audited' || stateCode === 'shipped' || stateCode === 'completed') {
    Object.keys(fieldPermissions).forEach((fieldCode) => {
      fieldPermissions[fieldCode] = 1;
    });
  }

  return fieldPermissions;
};

const createDefaultButtonPermissions = (
  module: ModuleNode,
  stateCode: string
): Record<string, StateButtonLimitLevel> => {
  const buttonPermissions = collectButtonCodes(module).reduce<Record<string, StateButtonLimitLevel>>((result, buttonCode) => {
    result[buttonCode] = 2;
    return result;
  }, {});

  const setButtonLevel = (buttonCodes: string[], level: StateButtonLimitLevel) => {
    buttonCodes.forEach((buttonCode) => {
      if (buttonPermissions[buttonCode] !== undefined) {
        buttonPermissions[buttonCode] = level;
      }
    });
  };

  if (stateCode === 'unaudited') {
    setButtonLevel(['audit', 'ship', 'complete'], 0);
  }

  if (stateCode === 'pre_audited') {
    setButtonLevel(['submit'], 0);
    setButtonLevel(['edit', 'save', 'addRow', 'removeRow'], 1);
  }

  if (stateCode === 'audited') {
    setButtonLevel(['edit', 'save', 'submit', 'audit', 'addRow', 'removeRow'], 0);
  }

  if (stateCode === 'shipped' || stateCode === 'completed') {
    setButtonLevel(['edit', 'save', 'submit', 'audit', 'addRow', 'removeRow'], 0);
  }

  return buttonPermissions;
};

const buildModuleStateAuthConfig = (module: ModuleNode): ModuleStateAuthConfig => {
  const states = standardStateSeed.map((state, index) => ({
    ...state,
    id: module.id * 100 + index + 1
  }));

  const transitions = standardTransitionSeed.map((transition, index) => ({
    ...transition,
    id: module.id * 1000 + index + 1
  }));

  const permissionsByState = states.reduce<Record<string, ModuleStatePermissionConfig>>((result, state) => {
    result[state.stateCode] = {
      fieldPermissions: createDefaultFieldPermissions(module, state.stateCode),
      buttonPermissions: createDefaultButtonPermissions(module, state.stateCode)
    };
    return result;
  }, {});

  return {
    states,
    transitions,
    permissionsByState
  };
};

const stateAuthStoreSeed = flattenModules(moduleTreeSeed).reduce<StateAuthStore>((result, module) => {
  result[module.moduleCode] = buildModuleStateAuthConfig(module);
  return result;
}, {});

export const cloneStateAuthModuleTree = () => {
  return JSON.parse(JSON.stringify(moduleTreeSeed)) as ModuleNode[];
};

export const cloneStateAuthStore = () => {
  return JSON.parse(JSON.stringify(stateAuthStoreSeed)) as StateAuthStore;
};

export const findStateAuthModuleNode = (nodes: ModuleNode[], id?: number) => {
  return findModuleNode(nodes, id);
};

export const findFirstStatefulModule = (nodes: ModuleNode[]): ModuleNode | undefined => {
  for (const node of nodes) {
    if (node.moduleType !== 'CATALOG') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstStatefulModule(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const findFirstStatefulModuleInNode = (node: ModuleNode): ModuleNode | undefined => {
  if (node.moduleType !== 'CATALOG') {
    return node;
  }
  return node.children?.length ? findFirstStatefulModule(node.children) : undefined;
};

export const getStateSummaryTagType = (state: ModuleStateDefinition): 'primary' | 'success' | 'warning' | 'info' => {
  if (state.isInitial === '1') {
    return 'primary';
  }
  if (state.isFinal === '1') {
    return 'success';
  }
  if (state.stateCode === 'pre_audited') {
    return 'warning';
  }
  return 'info';
};

export const getRestrictedFieldCount = (
  config: ModuleStateAuthConfig | undefined,
  stateCode: string
) => {
  const stateConfig = config?.permissionsByState[stateCode];
  if (!stateConfig) {
    return 0;
  }
  return Object.values(stateConfig.fieldPermissions).filter((level) => level < 2).length;
};

export const getRestrictedButtonCount = (
  config: ModuleStateAuthConfig | undefined,
  stateCode: string
) => {
  const stateConfig = config?.permissionsByState[stateCode];
  if (!stateConfig) {
    return 0;
  }
  return Object.values(stateConfig.buttonPermissions).filter((level) => level < 2).length;
};

export const buildStateAuthPreview = (
  module: ModuleNode,
  state: ModuleStateDefinition,
  config: ModuleStateAuthConfig
) => {
  const currentPermissions = config.permissionsByState[state.stateCode];

  return {
    moduleCode: module.moduleCode,
    moduleName: module.moduleName,
    stateFieldCode: module.stateFieldCode,
    currentState: {
      stateCode: state.stateCode,
      stateName: state.stateName
    },
    rules: {
      onlyTighten: true,
      mergeFormula: 'finalPermission = min(orgPostBaseline, stateLimit)',
      fieldLimitLevels: stateFieldLimitLabelMap,
      buttonLimitLevels: stateButtonLimitLabelMap
    },
    states: config.states.map((item) => ({
      code: item.stateCode,
      name: item.stateName,
      isFinal: item.isFinal === '1',
      isInitial: item.isInitial === '1',
      status: item.status === '0' ? 'enabled' : 'disabled'
    })),
    transitions: config.transitions.map((item) => ({
      actionButtonCode: item.actionButtonCode,
      actionButtonName: item.actionButtonName,
      fromStateCode: item.fromStateCode,
      status: item.status === '0' ? 'enabled' : 'disabled',
      toStateCode: item.toStateCode
    })),
    permissions: {
      buttons: module.buttons.reduce<Record<string, { area: string; label: string; limitLevel: StateButtonLimitLevel; limitLabel: string }>>((result, button) => {
        const limitLevel = currentPermissions?.buttonPermissions[button.buttonCode] ?? 2;
        result[button.buttonCode] = {
          area: buttonAreaLabelMap[button.area],
          label: button.defaultTitle,
          limitLabel: stateButtonLimitLabelMap[limitLevel],
          limitLevel
        };
        return result;
      }, {}),
      fields: [...module.headerTabs, ...module.detailTabs].reduce<Record<string, { label: string; limitLevel: StateFieldLimitLevel; limitLabel: string; scope: string; tabInfo: { id: number; tabCode: string; tabName: string; sort: number; note?: string } }>>((result, tab) => {
        tab.fields.forEach((field) => {
          const limitLevel = currentPermissions?.fieldPermissions[field.fieldCode] ?? 2;
          result[field.fieldCode] = {
            label: field.defaultTitle,
            limitLabel: stateFieldLimitLabelMap[limitLevel],
            limitLevel,
            scope: module.headerTabs.includes(tab) ? 'HEADER' : 'DETAIL',
            tabInfo: { ...tab.tabInfo }
          };
        });
        return result;
      }, {})
    }
  };
};
