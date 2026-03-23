export type YesNo = '0' | '1';
export type OrgNodeType = 'GROUP' | 'HEADQUARTERS' | 'BRANCH' | 'POST';

export interface PostManageTreeNode {
  id: number;
  parentId: number;
  nodeCode: string;
  nodeName: string;
  nodeType: OrgNodeType;
  sort: number;
  status: YesNo;
  orgName: string;
  orgTypeLabel: string;
  postTypeLabel?: string;
  managerName?: string;
  note?: string;
  employeeIds?: number[];
  children?: PostManageTreeNode[];
}

export interface PostManageEmployee {
  id: number;
  employeeNo: string;
  employeeName: string;
  companyName: string;
  deptName: string;
  mobile: string;
  status: YesNo;
}

export const postTypeOptions = ['管理岗', '采购岗', '审核岗', '销售岗', '运营岗', '财务岗'] as const;

const postManageTreeSeed: PostManageTreeNode[] = [
  {
    id: 1,
    parentId: 0,
    nodeCode: 'soli_group',
    nodeName: 'Soli集团',
    nodeType: 'GROUP',
    sort: 1,
    status: '0',
    orgName: 'Soli集团',
    orgTypeLabel: '集团',
    note: '集团层级仅用于承接总公司与分公司。',
    children: [
      {
        id: 10,
        parentId: 1,
        nodeCode: 'soli_hq',
        nodeName: '总公司',
        nodeType: 'HEADQUARTERS',
        sort: 1,
        status: '0',
        orgName: '总公司',
        orgTypeLabel: '总公司',
        note: '总部组织节点。',
        children: [
          {
            id: 100,
            parentId: 10,
            nodeCode: 'hq_purchase_center',
            nodeName: '采购中心',
            nodeType: 'POST',
            sort: 1,
            status: '0',
            orgName: '总公司',
            orgTypeLabel: '总公司',
            postTypeLabel: '管理岗',
            managerName: '王立',
            note: '负责总部采购条线组织与人员管理。',
            employeeIds: [1001, 1002],
            children: [
              {
                id: 101,
                parentId: 100,
                nodeCode: 'hq_buyer',
                nodeName: '采购员',
                nodeType: 'POST',
                sort: 1,
                status: '0',
                orgName: '总公司',
                orgTypeLabel: '总公司',
                postTypeLabel: '采购岗',
                managerName: '陈楠',
                note: '负责采购下单与供应商对接。',
                employeeIds: [1003, 1004]
              },
              {
                id: 102,
                parentId: 100,
                nodeCode: 'hq_purchase_manager',
                nodeName: '采购主管',
                nodeType: 'POST',
                sort: 2,
                status: '0',
                orgName: '总公司',
                orgTypeLabel: '总公司',
                postTypeLabel: '采购岗',
                managerName: '周静',
                note: '负责采购计划审批与资源协调。',
                employeeIds: [1005]
              }
            ]
          },
          {
            id: 110,
            parentId: 10,
            nodeCode: 'hq_audit_center',
            nodeName: '审核中心',
            nodeType: 'POST',
            sort: 2,
            status: '0',
            orgName: '总公司',
            orgTypeLabel: '总公司',
            postTypeLabel: '管理岗',
            managerName: '刘波',
            note: '负责预审与终审岗位管理。',
            employeeIds: [1006],
            children: [
              {
                id: 111,
                parentId: 110,
                nodeCode: 'hq_pre_auditor',
                nodeName: '预审岗',
                nodeType: 'POST',
                sort: 1,
                status: '0',
                orgName: '总公司',
                orgTypeLabel: '总公司',
                postTypeLabel: '审核岗',
                managerName: '孙倩',
                note: '负责单据预审。',
                employeeIds: [1007]
              },
              {
                id: 112,
                parentId: 110,
                nodeCode: 'hq_auditor',
                nodeName: '审核岗',
                nodeType: 'POST',
                sort: 2,
                status: '0',
                orgName: '总公司',
                orgTypeLabel: '总公司',
                postTypeLabel: '审核岗',
                managerName: '赵晨',
                note: '负责单据终审。',
                employeeIds: [1008]
              }
            ]
          }
        ]
      },
      {
        id: 20,
        parentId: 1,
        nodeCode: 'east_branch',
        nodeName: '华东分公司',
        nodeType: 'BRANCH',
        sort: 2,
        status: '0',
        orgName: '华东分公司',
        orgTypeLabel: '分公司',
        note: '华东分公司组织节点。',
        children: [
          {
            id: 200,
            parentId: 20,
            nodeCode: 'east_sales_center',
            nodeName: '销售中心',
            nodeType: 'POST',
            sort: 1,
            status: '0',
            orgName: '华东分公司',
            orgTypeLabel: '分公司',
            postTypeLabel: '管理岗',
            managerName: '韩雪',
            note: '负责华东销售团队管理。',
            employeeIds: [1009],
            children: [
              {
                id: 201,
                parentId: 200,
                nodeCode: 'east_sales_manager',
                nodeName: '销售经理',
                nodeType: 'POST',
                sort: 1,
                status: '0',
                orgName: '华东分公司',
                orgTypeLabel: '分公司',
                postTypeLabel: '销售岗',
                managerName: '杨帆',
                note: '负责区域客户与订单推进。',
                employeeIds: [1010]
              },
              {
                id: 202,
                parentId: 200,
                nodeCode: 'east_sales_assistant',
                nodeName: '销售助理',
                nodeType: 'POST',
                sort: 2,
                status: '0',
                orgName: '华东分公司',
                orgTypeLabel: '分公司',
                postTypeLabel: '销售岗',
                managerName: '朱敏',
                note: '负责销售资料与跟单。',
                employeeIds: [1011]
              }
            ]
          }
        ]
      },
      {
        id: 30,
        parentId: 1,
        nodeCode: 'south_branch',
        nodeName: '华南分公司',
        nodeType: 'BRANCH',
        sort: 3,
        status: '0',
        orgName: '华南分公司',
        orgTypeLabel: '分公司',
        note: '华南分公司组织节点。',
        children: [
          {
            id: 300,
            parentId: 30,
            nodeCode: 'south_operation_center',
            nodeName: '运营中心',
            nodeType: 'POST',
            sort: 1,
            status: '0',
            orgName: '华南分公司',
            orgTypeLabel: '分公司',
            postTypeLabel: '运营岗',
            managerName: '李岩',
            note: '负责华南运营协调。',
            employeeIds: [1012]
          }
        ]
      }
    ]
  }
];

const employeeSeed: PostManageEmployee[] = [
  { id: 1001, employeeNo: 'E1001', employeeName: '王磊', companyName: '总公司', deptName: '采购中心', mobile: '13800001001', status: '0' },
  { id: 1002, employeeNo: 'E1002', employeeName: '刘倩', companyName: '总公司', deptName: '采购中心', mobile: '13800001002', status: '0' },
  { id: 1003, employeeNo: 'E1003', employeeName: '陈晨', companyName: '总公司', deptName: '采购中心', mobile: '13800001003', status: '0' },
  { id: 1004, employeeNo: 'E1004', employeeName: '孙浩', companyName: '总公司', deptName: '采购中心', mobile: '13800001004', status: '0' },
  { id: 1005, employeeNo: 'E1005', employeeName: '周宁', companyName: '总公司', deptName: '采购中心', mobile: '13800001005', status: '0' },
  { id: 1006, employeeNo: 'E1006', employeeName: '何敏', companyName: '总公司', deptName: '审核中心', mobile: '13800001006', status: '0' },
  { id: 1007, employeeNo: 'E1007', employeeName: '徐航', companyName: '总公司', deptName: '审核中心', mobile: '13800001007', status: '0' },
  { id: 1008, employeeNo: 'E1008', employeeName: '赵亮', companyName: '总公司', deptName: '审核中心', mobile: '13800001008', status: '0' },
  { id: 1009, employeeNo: 'E1009', employeeName: '杨雪', companyName: '华东分公司', deptName: '销售中心', mobile: '13800001009', status: '0' },
  { id: 1010, employeeNo: 'E1010', employeeName: '陆川', companyName: '华东分公司', deptName: '销售中心', mobile: '13800001010', status: '0' },
  { id: 1011, employeeNo: 'E1011', employeeName: '朱琳', companyName: '华东分公司', deptName: '销售中心', mobile: '13800001011', status: '0' },
  { id: 1012, employeeNo: 'E1012', employeeName: '林远', companyName: '华南分公司', deptName: '运营中心', mobile: '13800001012', status: '0' },
  { id: 1013, employeeNo: 'E1013', employeeName: '高翔', companyName: '总公司', deptName: '财务部', mobile: '13800001013', status: '0' },
  { id: 1014, employeeNo: 'E1014', employeeName: '郑洁', companyName: '华东分公司', deptName: '销售中心', mobile: '13800001014', status: '0' }
];

export const clonePostManageTree = () => {
  return JSON.parse(JSON.stringify(postManageTreeSeed)) as PostManageTreeNode[];
};

export const clonePostManageEmployees = () => {
  return JSON.parse(JSON.stringify(employeeSeed)) as PostManageEmployee[];
};

export const findPostManageNode = (nodes: PostManageTreeNode[], id?: number): PostManageTreeNode | undefined => {
  if (id === undefined) {
    return undefined;
  }
  for (const node of nodes) {
    if (node.id === id) {
      return node;
    }
    if (node.children?.length) {
      const matched = findPostManageNode(node.children, id);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const findFirstPostNode = (nodes: PostManageTreeNode[]): PostManageTreeNode | undefined => {
  for (const node of nodes) {
    if (node.nodeType === 'POST') {
      return node;
    }
    if (node.children?.length) {
      const matched = findFirstPostNode(node.children);
      if (matched) {
        return matched;
      }
    }
  }
  return undefined;
};

export const countPostNodes = (nodes: PostManageTreeNode[]): number => {
  return nodes.reduce((sum, node) => {
    const current = node.nodeType === 'POST' ? 1 : 0;
    return sum + current + (node.children?.length ? countPostNodes(node.children) : 0);
  }, 0);
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
