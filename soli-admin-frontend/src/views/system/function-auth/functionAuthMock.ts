import {
  buttonAreaLabelMap,
  cloneModuleCenterTree,
  type ModuleButtonArea,
  type ModuleNode,
  type YesNo
} from '../module-center/moduleCenterFixture';

export type OrgNodeType = 'GROUP' | 'HEADQUARTERS' | 'BRANCH' | 'POST';
export type FieldPermissionLevel = 0 | 1 | 2;
export type ButtonPermissionLevel = 0 | 1 | 2;

export interface OrgPostTreeNode {
  id: number;
  parentId: number;
  nodeCode: string;
  nodeName: string;
  nodeType: OrgNodeType;
  status: YesNo;
  orgName?: string;
  orgTypeLabel?: string;
  postTypeLabel?: string;
  children?: OrgPostTreeNode[];
}

export interface ModulePermissionConfig {
  moduleVisible: boolean;
  navVisible: boolean;
  fieldPermissions: Record<string, FieldPermissionLevel>;
  buttonPermissions: Record<string, ButtonPermissionLevel>;
}

export type FunctionAuthStore = Record<number, Record<string, ModulePermissionConfig>>;

const orgPostTreeSeed: OrgPostTreeNode[] = [
  {
    id: 1,
    parentId: 0,
    nodeCode: 'soli_group',
    nodeName: 'Soli集团',
    nodeType: 'GROUP',
    status: '0',
    children: [
      {
        id: 10,
        parentId: 1,
        nodeCode: 'soli_hq',
        nodeName: '总公司',
        nodeType: 'HEADQUARTERS',
        status: '0',
        orgName: '总公司',
        orgTypeLabel: '总部',
        children: [
          {
            id: 101,
            parentId: 10,
            nodeCode: 'hq_buyer',
            nodeName: '采购员',
            nodeType: 'POST',
            status: '0',
            orgName: '总公司',
            orgTypeLabel: '总公司',
            postTypeLabel: '采购岗'
          },
          {
            id: 102,
            parentId: 10,
            nodeCode: 'hq_pre_auditor',
            nodeName: '预审员',
            nodeType: 'POST',
            status: '0',
            orgName: '总公司',
            orgTypeLabel: '总公司',
            postTypeLabel: '预审岗'
          },
          {
            id: 103,
            parentId: 10,
            nodeCode: 'hq_auditor',
            nodeName: '审核员',
            nodeType: 'POST',
            status: '0',
            orgName: '总公司',
            orgTypeLabel: '总公司',
            postTypeLabel: '审核岗'
          }
        ]
      },
      {
        id: 20,
        parentId: 1,
        nodeCode: 'east_branch',
        nodeName: '华东分公司',
        nodeType: 'BRANCH',
        status: '0',
        orgName: '华东分公司',
        orgTypeLabel: '分公司',
        children: [
          {
            id: 201,
            parentId: 20,
            nodeCode: 'east_branch_buyer',
            nodeName: '采购主管',
            nodeType: 'POST',
            status: '0',
            orgName: '华东分公司',
            orgTypeLabel: '分公司',
            postTypeLabel: '采购岗'
          },
          {
            id: 202,
            parentId: 20,
            nodeCode: 'east_sales_manager',
            nodeName: '销售经理',
            nodeType: 'POST',
            status: '0',
            orgName: '华东分公司',
            orgTypeLabel: '分公司',
            postTypeLabel: '销售岗'
          }
        ]
      },
      {
        id: 30,
        parentId: 1,
        nodeCode: 'south_branch',
        nodeName: '华南分公司',
        nodeType: 'BRANCH',
        status: '0',
        orgName: '华南分公司',
        orgTypeLabel: '分公司',
        children: [
          {
            id: 301,
            parentId: 30,
            nodeCode: 'south_branch_manager',
            nodeName: '运营经理',
            nodeType: 'POST',
            status: '0',
            orgName: '华南分公司',
            orgTypeLabel: '分公司',
            postTypeLabel: '经营岗'
          }
        ]
      }
    ]
  }
];

const moduleTreeSeed = cloneModuleCenterTree();

export const fieldPermissionLevelOptions = [
  { label: '不可见', value: 0 },
  { label: '只读', value: 1 },
  { label: '可写', value: 2 }
] as const;

export const buttonPermissionLevelOptions = [
  { label: '不可见', value: 0 },
  { label: '禁用', value: 1 },
  { label: '可用', value: 2 }
] as const;

export const fieldPermissionTagTypeMap: Record<FieldPermissionLevel, '' | 'warning' | 'success'> = {
  0: '',
  1: 'warning',
  2: 'success'
};

export const buttonPermissionTagTypeMap: Record<ButtonPermissionLevel, '' | 'warning' | 'success'> = {
  0: '',
  1: 'warning',
  2: 'success'
};

export const fieldPermissionLabelMap: Record<FieldPermissionLevel, string> = {
  0: '不可见',
  1: '只读',
  2: '可写'
};

export const buttonPermissionLabelMap: Record<ButtonPermissionLevel, string> = {
  0: '不可见',
  1: '禁用',
  2: '可用'
};

const isPurchaseModule = (moduleCode: string) => moduleCode.startsWith('purchase');
const isSalesModule = (moduleCode: string) => moduleCode.startsWith('sales');

const flattenModules = (nodes: ModuleNode[]): ModuleNode[] => {
  return nodes.flatMap((node) => {
    const children = node.children?.length ? flattenModules(node.children) : [];
    return [node, ...children];
  });
};

const collectPostLeaves = (nodes: OrgPostTreeNode[]): OrgPostTreeNode[] => {
  return nodes.flatMap((node) => {
    if (node.nodeType === 'POST') {
      return [node];
    }
    return node.children?.length ? collectPostLeaves(node.children) : [];
  });
};

const resolveModuleVisible = (postCode: string, moduleCode: string, nodeType: ModuleNode['moduleType']) => {
  if (nodeType === 'CATALOG') {
    return true;
  }
  if (postCode === 'east_sales_manager') {
    return isSalesModule(moduleCode);
  }
  if (postCode === 'south_branch_manager') {
    return true;
  }
  return isPurchaseModule(moduleCode);
};

const resolveFieldPermission = (postCode: string, moduleCode: string, fieldCode: string): FieldPermissionLevel => {
  if (postCode === 'south_branch_manager') {
    return 2;
  }
  if (postCode === 'east_sales_manager') {
    return isSalesModule(moduleCode) ? 2 : 0;
  }
  if (!isPurchaseModule(moduleCode)) {
    return 0;
  }
  if (postCode === 'hq_buyer') {
    return ['status', 'auditUserName', 'auditTime', 'sourceBillNo', 'sourceType'].includes(fieldCode) ? 1 : 2;
  }
  if (postCode === 'east_branch_buyer') {
    if (['amount', 'status', 'auditUserName', 'auditTime'].includes(fieldCode)) {
      return 1;
    }
    return 2;
  }
  if (postCode === 'hq_pre_auditor') {
    return 1;
  }
  if (postCode === 'hq_auditor') {
    return ['auditUserName', 'auditTime'].includes(fieldCode) ? 2 : 1;
  }
  return 0;
};

const resolveButtonPermission = (postCode: string, moduleCode: string, buttonCode: string): ButtonPermissionLevel => {
  if (postCode === 'south_branch_manager') {
    return 2;
  }
  if (postCode === 'east_sales_manager') {
    if (!isSalesModule(moduleCode)) {
      return 0;
    }
    return ['create', 'detail', 'edit', 'save'].includes(buttonCode) ? 2 : 1;
  }
  if (!isPurchaseModule(moduleCode)) {
    return 0;
  }
  if (postCode === 'hq_buyer') {
    if (['create', 'export', 'detail', 'edit', 'save', 'submit', 'addRow', 'removeRow'].includes(buttonCode)) {
      return 2;
    }
    return 0;
  }
  if (postCode === 'east_branch_buyer') {
    if (['create', 'detail', 'save', 'addRow', 'removeRow'].includes(buttonCode)) {
      return 2;
    }
    return ['submit', 'edit'].includes(buttonCode) ? 1 : 0;
  }
  if (postCode === 'hq_pre_auditor') {
    if (['detail', 'export'].includes(buttonCode)) {
      return 2;
    }
    return ['submit', 'audit'].includes(buttonCode) ? 1 : 0;
  }
  if (postCode === 'hq_auditor') {
    if (['detail', 'export', 'audit'].includes(buttonCode)) {
      return 2;
    }
    return ['submit', 'save'].includes(buttonCode) ? 1 : 0;
  }
  return 0;
};

const buildModulePermissionConfig = (postCode: string, module: ModuleNode): ModulePermissionConfig => {
  const fieldPermissions = [...module.headerTabs, ...module.detailTabs].reduce<Record<string, FieldPermissionLevel>>((result, tab) => {
    tab.fields.forEach((field) => {
      result[field.fieldCode] = resolveFieldPermission(postCode, module.moduleCode, field.fieldCode);
    });
    return result;
  }, {});

  const buttonPermissions = module.buttons.reduce<Record<string, ButtonPermissionLevel>>((result, button) => {
    result[button.buttonCode] = resolveButtonPermission(postCode, module.moduleCode, button.buttonCode);
    return result;
  }, {});

  const moduleVisible = resolveModuleVisible(postCode, module.moduleCode, module.moduleType);

  return {
    moduleVisible,
    navVisible: moduleVisible && module.navVisible === '1',
    fieldPermissions,
    buttonPermissions
  };
};

const permissionStoreSeed = (() => {
  const modules = flattenModules(moduleTreeSeed);
  const posts = collectPostLeaves(orgPostTreeSeed);
  return posts.reduce<FunctionAuthStore>((result, post) => {
    result[post.id] = modules.reduce<Record<string, ModulePermissionConfig>>((moduleResult, module) => {
      moduleResult[module.moduleCode] = buildModulePermissionConfig(post.nodeCode, module);
      return moduleResult;
    }, {});
    return result;
  }, {});
})();

export const cloneOrgPostTree = () => {
  return JSON.parse(JSON.stringify(orgPostTreeSeed)) as OrgPostTreeNode[];
};

export const cloneFunctionAuthModuleTree = () => {
  return JSON.parse(JSON.stringify(moduleTreeSeed)) as ModuleNode[];
};

export const cloneFunctionAuthPermissionStore = () => {
  return JSON.parse(JSON.stringify(permissionStoreSeed)) as FunctionAuthStore;
};

export const findOrgPostNode = (nodes: OrgPostTreeNode[], id?: number): OrgPostTreeNode | undefined => {
  if (id === undefined) {
    return undefined;
  }
  for (const node of nodes) {
    if (node.id === id) {
      return node;
    }
    if (node.children?.length) {
      const matched = findOrgPostNode(node.children, id);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const findFirstPostLeaf = (nodes: OrgPostTreeNode[]): OrgPostTreeNode | undefined => {
  for (const node of nodes) {
    if (node.nodeType === 'POST') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstPostLeaf(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const findFirstPostLeafInNode = (node: OrgPostTreeNode): OrgPostTreeNode | undefined => {
  if (node.nodeType === 'POST') {
    return node;
  }
  return node.children?.length ? findFirstPostLeaf(node.children) : undefined;
};

export const findFirstModuleLeafInNode = (node: ModuleNode): ModuleNode | undefined => {
  if (node.moduleType !== 'CATALOG') {
    return node;
  }
  if (!node.children?.length) {
    return undefined;
  }
  for (const child of node.children) {
    const matched = findFirstModuleLeafInNode(child);
    if (matched) {
      return matched;
    }
  }
  return undefined;
};

export const getOrgNodeTypeLabel = (nodeType: OrgNodeType) => {
  const labelMap: Record<OrgNodeType, string> = {
    BRANCH: '分公司',
    GROUP: '集团',
    HEADQUARTERS: '总公司',
    POST: '岗位'
  };
  return labelMap[nodeType];
};

export const buildFunctionAuthPreview = (
  orgPost: OrgPostTreeNode,
  module: ModuleNode,
  config: ModulePermissionConfig
) => {
  const fields = [...module.headerTabs, ...module.detailTabs].reduce<Record<string, { label: string; visible: boolean; editable: boolean }>>((result, tab) => {
    tab.fields.forEach((field) => {
      const level = config.fieldPermissions[field.fieldCode] ?? 0;
      result[field.fieldCode] = {
        label: field.defaultTitle,
        visible: level > 0,
        editable: level === 2
      };
    });
    return result;
  }, {});

  const buttons = module.buttons.reduce<Record<string, { label: string; visible: boolean; disabled: boolean }>>((result, button) => {
    const level = config.buttonPermissions[button.buttonCode] ?? 0;
    result[button.buttonCode] = {
      label: button.defaultTitle,
      visible: level > 0,
      disabled: level !== 2
    };
    return result;
  }, {});

  return {
    currentOrgPost: {
      orgName: orgPost.orgName,
      orgPostCode: orgPost.nodeCode,
      orgPostId: orgPost.id,
      orgPostName: orgPost.nodeName
    },
    layout: {
      detailTabs: module.detailTabs.map((tab) => ({
        fieldCodes: tab.fields.map((field) => field.fieldCode),
        tabInfo: { ...tab.tabInfo }
      })),
      headerTabs: module.headerTabs.map((tab) => ({
        fieldCodes: tab.fields.map((field) => field.fieldCode),
        tabInfo: { ...tab.tabInfo }
      }))
    },
    moduleCode: module.moduleCode,
    moduleName: module.moduleName,
    permissions: {
      buttons,
      fields,
      module: {
        navVisible: config.navVisible,
        visible: config.moduleVisible
      }
    }
  };
};

export const groupModuleButtons = (module: ModuleNode) => {
  return (Object.keys(buttonAreaLabelMap) as ModuleButtonArea[])
    .map((area) => ({
      area,
      buttons: module.buttons.filter((button) => button.area === area).sort((left, right) => left.sort - right.sort)
    }))
    .filter((item) => item.buttons.length > 0);
};
