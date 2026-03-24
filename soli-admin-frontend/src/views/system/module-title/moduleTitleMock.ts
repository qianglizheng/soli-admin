import {
  cloneModuleCenterTree,
  findFirstEditableModule,
  findModuleNode,
  type ModuleFieldDefinition,
  type ModuleNode
} from '../module-center/moduleCenterFixture';

export interface ModuleFieldTitleConfig {
  displayTitle: string;
  placeholder: string;
  helpText: string;
}

export type ModuleTitleStore = Record<string, Record<string, ModuleFieldTitleConfig>>;

const moduleTreeSeed = cloneModuleCenterTree();

const flattenModules = (nodes: ModuleNode[]): ModuleNode[] => {
  return nodes.flatMap((node) => {
    const children = node.children?.length ? flattenModules(node.children) : [];
    return node.moduleType === 'CATALOG' ? children : [node, ...children];
  });
};

const getDefaultPlaceholder = (field: ModuleFieldDefinition) => {
  if (field.componentType === 'tag' || field.componentType === 'text') {
    return '';
  }
  if (['date', 'datetime', 'search-select'].includes(field.componentType)) {
    return `请选择${field.defaultTitle}`;
  }
  return `请输入${field.defaultTitle}`;
};

const getDefaultHelpText = (field: ModuleFieldDefinition) => {
  if (field.note) {
    return field.note;
  }
  if (field.requiredFlag === '1') {
    return `${field.defaultTitle}为必填项，请按业务规范维护。`;
  }
  return `${field.defaultTitle}用于补充当前业务单据信息。`;
};

const buildModuleFieldTitleConfig = (field: ModuleFieldDefinition): ModuleFieldTitleConfig => {
  return {
    displayTitle: field.defaultTitle,
    placeholder: getDefaultPlaceholder(field),
    helpText: getDefaultHelpText(field)
  };
};

const applySeedOverrides = (store: ModuleTitleStore) => {
  const purchaseOrder = store.purchase_order;
  if (purchaseOrder) {
    if (purchaseOrder.supplierName) {
      purchaseOrder.supplierName.displayTitle = '供应商名称';
      purchaseOrder.supplierName.placeholder = '请选择采购供应商';
      purchaseOrder.supplierName.helpText = '支持按名称、编码联想，选择后自动带出供应商基础资料。';
    }
    if (purchaseOrder.amount) {
      purchaseOrder.amount.displayTitle = '订单金额';
      purchaseOrder.amount.placeholder = '系统自动汇总明细金额';
      purchaseOrder.amount.helpText = '金额由明细行数量和单价汇总得出，默认不建议手工修改。';
    }
    if (purchaseOrder.sourceBillNo) {
      purchaseOrder.sourceBillNo.displayTitle = '来源单号';
      purchaseOrder.sourceBillNo.placeholder = '请输入或关联来源单号';
    }
  }

  const salesOrder = store.sales_order;
  if (salesOrder?.customerName) {
    salesOrder.customerName.displayTitle = '客户名称';
    salesOrder.customerName.placeholder = '请选择下单客户';
    salesOrder.customerName.helpText = '客户变更后会联动信用额度、价格策略等业务校验。';
  }
};

const moduleTitleStoreSeed = (() => {
  const modules = flattenModules(moduleTreeSeed);
  const result = modules.reduce<ModuleTitleStore>((moduleResult, module) => {
    const fieldMap = [...module.headerTabs, ...module.detailTabs].reduce<Record<string, ModuleFieldTitleConfig>>((fieldResult, tab) => {
      tab.fields.forEach((field) => {
        fieldResult[field.fieldCode] = buildModuleFieldTitleConfig(field);
      });
      return fieldResult;
    }, {});

    moduleResult[module.moduleCode] = fieldMap;
    return moduleResult;
  }, {});

  applySeedOverrides(result);
  return result;
})();

export const cloneModuleTitleTree = () => {
  return JSON.parse(JSON.stringify(moduleTreeSeed)) as ModuleNode[];
};

export const cloneModuleTitleStore = () => {
  return JSON.parse(JSON.stringify(moduleTitleStoreSeed)) as ModuleTitleStore;
};

export const findModuleTitleNode = (nodes: ModuleNode[], id?: number) => {
  return findModuleNode(nodes, id);
};

export const findFirstModuleTitleLeaf = (nodes: ModuleNode[]) => {
  return findFirstEditableModule(nodes);
};

export const findFirstModuleTitleLeafInNode = (node: ModuleNode): ModuleNode | undefined => {
  if (node.moduleType !== 'CATALOG') {
    return node;
  }
  return node.children?.length ? findFirstEditableModule(node.children) : undefined;
};

export const getModuleTypeLabel = (moduleType: ModuleNode['moduleType']) => {
  const labelMap: Record<ModuleNode['moduleType'], string> = {
    BILL: '单据模块',
    CATALOG: '目录',
    PAGE: '页面'
  };
  return labelMap[moduleType];
};

export const getModuleTypeTagType = (moduleType: ModuleNode['moduleType']) => {
  if (moduleType === 'CATALOG') {
    return 'info';
  }
  if (moduleType === 'BILL') {
    return 'warning';
  }
  return 'success';
};

export const getFieldDefaultPlaceholder = (field: ModuleFieldDefinition) => {
  return getDefaultPlaceholder(field);
};

export const getFieldDefaultHelpText = (field: ModuleFieldDefinition) => {
  return getDefaultHelpText(field);
};

export const isFieldConfigCustomized = (
  field: ModuleFieldDefinition,
  config: ModuleFieldTitleConfig | undefined
) => {
  if (!config) {
    return false;
  }
  return config.displayTitle !== field.defaultTitle
    || config.placeholder !== getDefaultPlaceholder(field)
    || config.helpText !== getDefaultHelpText(field);
};

export const buildModuleTitlePreview = (
  module: ModuleNode,
  moduleFieldMap: Record<string, ModuleFieldTitleConfig>
) => {
  const mapFields = (fields: ModuleFieldDefinition[]) => {
    return fields.map((field) => {
      const config = moduleFieldMap[field.fieldCode];
      return {
        componentType: field.componentType,
        dataPath: field.dataPath,
        defaultTitle: field.defaultTitle,
        displayTitle: config?.displayTitle || field.defaultTitle,
        fieldCode: field.fieldCode,
        helpText: config?.helpText || getDefaultHelpText(field),
        placeholder: config?.placeholder || getDefaultPlaceholder(field),
        required: field.requiredFlag === '1'
      };
    });
  };

  return {
    contextVersion: module.contextVersion,
    detailTabs: module.detailTabs.map((tab) => ({
      fields: mapFields(tab.fields),
      tabInfo: { ...tab.tabInfo }
    })),
    headerTabs: module.headerTabs.map((tab) => ({
      fields: mapFields(tab.fields),
      tabInfo: { ...tab.tabInfo }
    })),
    moduleCode: module.moduleCode,
    moduleName: module.moduleName
  };
};
