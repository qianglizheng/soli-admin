export type ModuleType = 'CATALOG' | 'PAGE' | 'BILL';
export type YesNo = '0' | '1';
export type ModuleButtonArea = 'LIST_TOOLBAR' | 'LIST_ROW_BUTTON' | 'HEADER_TOOLBAR' | 'DETAIL_ROW_BUTTON';

export interface ModuleFieldDefinition {
  id: number;
  fieldCode: string;
  defaultTitle: string;
  componentType: string;
  dataPath: string;
  valueType: string;
  requiredFlag: YesNo;
  sort: number;
  note?: string;
}

export interface ModuleTabDefinition {
  id: number;
  tabCode: string;
  tabName: string;
  sort: number;
  note?: string;
  fields: ModuleFieldDefinition[];
}

export interface ModuleButtonDefinition {
  id: number;
  buttonCode: string;
  defaultTitle: string;
  area: ModuleButtonArea;
  sort: number;
  note?: string;
}

export interface ModuleNode {
  id: number;
  parentId: number;
  moduleCode: string;
  moduleName: string;
  moduleType: ModuleType;
  routePath: string;
  componentPath: string;
  icon: string;
  sort: number;
  navVisible: YesNo;
  statefulFlag: YesNo;
  stateFieldCode: string;
  contextVersion: number;
  status: YesNo;
  note: string;
  headerTabs: ModuleTabDefinition[];
  detailTabs: ModuleTabDefinition[];
  buttons: ModuleButtonDefinition[];
  children?: ModuleNode[];
}

const moduleTreeSeed: ModuleNode[] = [
  {
    id: 100,
    parentId: 0,
    moduleCode: 'business_center',
    moduleName: '业务中心',
    moduleType: 'CATALOG',
    routePath: '/business',
    componentPath: '',
    icon: 'Grid',
    sort: 1,
    navVisible: '1',
    statefulFlag: '0',
    stateFieldCode: '',
    contextVersion: 8,
    status: '0',
    note: '业务模块目录，承载采购、销售、仓储等状态型模块。',
    headerTabs: [],
    detailTabs: [],
    buttons: [],
    children: [
      {
        id: 110,
        parentId: 100,
        moduleCode: 'purchase_center',
        moduleName: '采购管理',
        moduleType: 'CATALOG',
        routePath: '/purchase',
        componentPath: '',
        icon: 'ShoppingCart',
        sort: 1,
        navVisible: '1',
        statefulFlag: '0',
        stateFieldCode: '',
        contextVersion: 6,
        status: '0',
        note: '采购业务目录。',
        headerTabs: [],
        detailTabs: [],
        buttons: [],
        children: [
          {
            id: 111,
            parentId: 110,
            moduleCode: 'purchase_order',
            moduleName: '采购订单',
            moduleType: 'BILL',
            routePath: '/purchase/order',
            componentPath: 'purchase/order/index',
            icon: 'Document',
            sort: 1,
            navVisible: '1',
            statefulFlag: '1',
            stateFieldCode: 'status',
            contextVersion: 17,
            status: '0',
            note: '标准采购订单模块，支持列表、单头、明细、状态流转。',
            headerTabs: [
              {
                id: 11101,
                tabCode: 'basic',
                tabName: '基础信息',
                sort: 1,
                note: '采购订单基础单头字段。',
                fields: [
                  {
                    id: 1110101,
                    fieldCode: 'billNo',
                    defaultTitle: '单据编号',
                    componentType: 'text',
                    dataPath: 'header.billNo',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  },
                  {
                    id: 1110102,
                    fieldCode: 'supplierName',
                    defaultTitle: '供应商',
                    componentType: 'search-select',
                    dataPath: 'header.supplierName',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 2
                  },
                  {
                    id: 1110103,
                    fieldCode: 'billDate',
                    defaultTitle: '单据日期',
                    componentType: 'date',
                    dataPath: 'header.billDate',
                    valueType: 'date',
                    requiredFlag: '1',
                    sort: 3
                  },
                  {
                    id: 1110104,
                    fieldCode: 'amount',
                    defaultTitle: '金额',
                    componentType: 'amount',
                    dataPath: 'header.amount',
                    valueType: 'decimal',
                    requiredFlag: '1',
                    sort: 4
                  }
                ]
              },
              {
                id: 11102,
                tabCode: 'audit',
                tabName: '审核信息',
                sort: 2,
                note: '状态流转和审核留痕信息。',
                fields: [
                  {
                    id: 1110201,
                    fieldCode: 'status',
                    defaultTitle: '单据状态',
                    componentType: 'tag',
                    dataPath: 'header.status',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  },
                  {
                    id: 1110202,
                    fieldCode: 'auditUserName',
                    defaultTitle: '审核人',
                    componentType: 'text',
                    dataPath: 'header.auditUserName',
                    valueType: 'string',
                    requiredFlag: '0',
                    sort: 2
                  },
                  {
                    id: 1110203,
                    fieldCode: 'auditTime',
                    defaultTitle: '审核时间',
                    componentType: 'datetime',
                    dataPath: 'header.auditTime',
                    valueType: 'datetime',
                    requiredFlag: '0',
                    sort: 3
                  }
                ]
              }
            ],
            detailTabs: [
              {
                id: 11111,
                tabCode: 'items',
                tabName: '物料明细',
                sort: 1,
                note: '采购订单主明细。',
                fields: [
                  {
                    id: 1111101,
                    fieldCode: 'materialCode',
                    defaultTitle: '物料编码',
                    componentType: 'search-select',
                    dataPath: 'detail.items.materialCode',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  },
                  {
                    id: 1111102,
                    fieldCode: 'materialName',
                    defaultTitle: '物料名称',
                    componentType: 'text',
                    dataPath: 'detail.items.materialName',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 2
                  },
                  {
                    id: 1111103,
                    fieldCode: 'qty',
                    defaultTitle: '数量',
                    componentType: 'number',
                    dataPath: 'detail.items.qty',
                    valueType: 'decimal',
                    requiredFlag: '1',
                    sort: 3
                  },
                  {
                    id: 1111104,
                    fieldCode: 'price',
                    defaultTitle: '单价',
                    componentType: 'amount',
                    dataPath: 'detail.items.price',
                    valueType: 'decimal',
                    requiredFlag: '1',
                    sort: 4
                  }
                ]
              },
              {
                id: 11112,
                tabCode: 'source',
                tabName: '来源单据',
                sort: 2,
                note: '采购申请或询价单关联信息。',
                fields: [
                  {
                    id: 1111201,
                    fieldCode: 'sourceBillNo',
                    defaultTitle: '来源单号',
                    componentType: 'text',
                    dataPath: 'detail.source.sourceBillNo',
                    valueType: 'string',
                    requiredFlag: '0',
                    sort: 1
                  },
                  {
                    id: 1111202,
                    fieldCode: 'sourceType',
                    defaultTitle: '来源类型',
                    componentType: 'tag',
                    dataPath: 'detail.source.sourceType',
                    valueType: 'string',
                    requiredFlag: '0',
                    sort: 2
                  }
                ]
              }
            ],
            buttons: [
              { id: 111201, buttonCode: 'create', defaultTitle: '新增', area: 'LIST_TOOLBAR', sort: 1 },
              { id: 111202, buttonCode: 'export', defaultTitle: '导出', area: 'LIST_TOOLBAR', sort: 2 },
              { id: 111203, buttonCode: 'detail', defaultTitle: '详情', area: 'LIST_ROW_BUTTON', sort: 1 },
              { id: 111204, buttonCode: 'edit', defaultTitle: '编辑', area: 'LIST_ROW_BUTTON', sort: 2 },
              { id: 111205, buttonCode: 'save', defaultTitle: '保存', area: 'HEADER_TOOLBAR', sort: 1 },
              { id: 111206, buttonCode: 'submit', defaultTitle: '提交', area: 'HEADER_TOOLBAR', sort: 2 },
              { id: 111207, buttonCode: 'audit', defaultTitle: '审核', area: 'HEADER_TOOLBAR', sort: 3 },
              { id: 111208, buttonCode: 'addRow', defaultTitle: '新增行', area: 'DETAIL_ROW_BUTTON', sort: 1 },
              { id: 111209, buttonCode: 'removeRow', defaultTitle: '删除行', area: 'DETAIL_ROW_BUTTON', sort: 2 }
            ],
            children: []
          },
          {
            id: 112,
            parentId: 110,
            moduleCode: 'purchase_return',
            moduleName: '采购退货单',
            moduleType: 'BILL',
            routePath: '/purchase/return',
            componentPath: 'purchase/return/index',
            icon: 'RefreshLeft',
            sort: 2,
            navVisible: '1',
            statefulFlag: '1',
            stateFieldCode: 'status',
            contextVersion: 9,
            status: '0',
            note: '采购退货业务模块。',
            headerTabs: [
              {
                id: 11201,
                tabCode: 'basic',
                tabName: '基础信息',
                sort: 1,
                fields: [
                  {
                    id: 1120101,
                    fieldCode: 'returnNo',
                    defaultTitle: '退货单号',
                    componentType: 'text',
                    dataPath: 'header.returnNo',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  }
                ]
              }
            ],
            detailTabs: [
              {
                id: 11211,
                tabCode: 'items',
                tabName: '退货明细',
                sort: 1,
                fields: [
                  {
                    id: 1121101,
                    fieldCode: 'materialCode',
                    defaultTitle: '物料编码',
                    componentType: 'text',
                    dataPath: 'detail.items.materialCode',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  }
                ]
              }
            ],
            buttons: [
              { id: 112201, buttonCode: 'create', defaultTitle: '新增', area: 'LIST_TOOLBAR', sort: 1 },
              { id: 112202, buttonCode: 'save', defaultTitle: '保存', area: 'HEADER_TOOLBAR', sort: 1 }
            ],
            children: []
          }
        ]
      },
      {
        id: 120,
        parentId: 100,
        moduleCode: 'sales_center',
        moduleName: '销售管理',
        moduleType: 'CATALOG',
        routePath: '/sales',
        componentPath: '',
        icon: 'Sell',
        sort: 2,
        navVisible: '1',
        statefulFlag: '0',
        stateFieldCode: '',
        contextVersion: 4,
        status: '0',
        note: '销售业务目录。',
        headerTabs: [],
        detailTabs: [],
        buttons: [],
        children: [
          {
            id: 121,
            parentId: 120,
            moduleCode: 'sales_order',
            moduleName: '销售订单',
            moduleType: 'BILL',
            routePath: '/sales/order',
            componentPath: 'sales/order/index',
            icon: 'DocumentChecked',
            sort: 1,
            navVisible: '1',
            statefulFlag: '1',
            stateFieldCode: 'status',
            contextVersion: 11,
            status: '0',
            note: '销售订单模块原型。',
            headerTabs: [
              {
                id: 12101,
                tabCode: 'basic',
                tabName: '基础信息',
                sort: 1,
                fields: [
                  {
                    id: 1210101,
                    fieldCode: 'customerName',
                    defaultTitle: '客户名称',
                    componentType: 'search-select',
                    dataPath: 'header.customerName',
                    valueType: 'string',
                    requiredFlag: '1',
                    sort: 1
                  }
                ]
              }
            ],
            detailTabs: [],
            buttons: [
              { id: 121201, buttonCode: 'create', defaultTitle: '新增', area: 'LIST_TOOLBAR', sort: 1 },
              { id: 121202, buttonCode: 'save', defaultTitle: '保存', area: 'HEADER_TOOLBAR', sort: 1 }
            ],
            children: []
          }
        ]
      }
    ]
  }
];

export const buttonAreaLabelMap: Record<ModuleButtonArea, string> = {
  DETAIL_ROW_BUTTON: '明细行按钮',
  HEADER_TOOLBAR: '详情顶部按钮',
  LIST_ROW_BUTTON: '列表行按钮',
  LIST_TOOLBAR: '列表顶部按钮'
};

export const cloneModuleCenterTree = () => {
  return JSON.parse(JSON.stringify(moduleTreeSeed)) as ModuleNode[];
};

export const findModuleNode = (nodes: ModuleNode[], id?: number): ModuleNode | undefined => {
  if (id === undefined) {
    return undefined;
  }
  for (const node of nodes) {
    if (node.id === id) {
      return node;
    }
    if (node.children?.length) {
      const matched = findModuleNode(node.children, id);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const findFirstEditableModule = (nodes: ModuleNode[]): ModuleNode | undefined => {
  for (const node of nodes) {
    if (node.moduleType !== 'CATALOG') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstEditableModule(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const buildModuleContextPreview = (module: ModuleNode) => {
  const groupButtons = (area: ModuleButtonArea) => {
    return module.buttons
      .filter((button) => button.area === area)
      .sort((left, right) => left.sort - right.sort)
      .reduce<Record<string, { label: string; visible: boolean; disabled: boolean }>>((result, button) => {
        result[button.buttonCode] = {
          label: button.defaultTitle,
          visible: true,
          disabled: false
        };
        return result;
      }, {});
  };

  return {
    moduleCode: module.moduleCode,
    moduleName: module.moduleName,
    contextVersion: module.contextVersion,
    currentPost: {
      postCode: 'buyer',
      postId: 3,
      postName: '采购员'
    },
    state: module.statefulFlag === '1'
      ? {
          currentLabel: '未审核',
          currentValue: 'unaudited',
          stateField: module.stateFieldCode
        }
      : null,
    headerTabs: module.headerTabs.map((tab) => ({
      fields: tab.fields.map((field) => ({
        componentType: field.componentType,
        editable: true,
        fieldCode: field.fieldCode,
        label: field.defaultTitle,
        required: field.requiredFlag === '1',
        visible: true
      })),
      tabCode: tab.tabCode,
      tabName: tab.tabName,
      visible: tab.fields.length > 0
    })),
    detailTabs: module.detailTabs.map((tab) => ({
      fields: tab.fields.map((field) => ({
        componentType: field.componentType,
        editable: true,
        fieldCode: field.fieldCode,
        label: field.defaultTitle,
        required: field.requiredFlag === '1',
        visible: true
      })),
      tabCode: tab.tabCode,
      tabName: tab.tabName,
      visible: tab.fields.length > 0
    })),
    buttons: {
      detailRow: groupButtons('DETAIL_ROW_BUTTON'),
      headerToolbar: groupButtons('HEADER_TOOLBAR'),
      listRow: groupButtons('LIST_ROW_BUTTON'),
      listToolbar: groupButtons('LIST_TOOLBAR')
    }
  };
};
