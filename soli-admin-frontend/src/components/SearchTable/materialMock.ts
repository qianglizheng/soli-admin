export interface MaterialOption {
  itemCode: string;
  itemName: string;
  spec: string;
  unit: string;
  category: string;
  stockQty: number;
}

export interface MaterialQueryParams {
  itemCode: string;
  itemName: string;
  spec: string;
  category: string;
}

export const mockMaterialList: MaterialOption[] = [
  { itemCode: 'HW-001', itemName: '华为 Mate 60 Pro', spec: '12GB+512GB', unit: '台', category: '手机', stockQty: 128 },
  { itemCode: 'HW-002', itemName: '华为 MateBook X Pro', spec: '32GB+1TB', unit: '台', category: '笔记本', stockQty: 36 },
  { itemCode: 'HW-003', itemName: '华为 FreeBuds Pro 3', spec: '陶瓷白', unit: '副', category: '耳机', stockQty: 240 },
  { itemCode: 'XM-001', itemName: '小米 14 Ultra', spec: '16GB+1TB', unit: '台', category: '手机', stockQty: 96 },
  { itemCode: 'XM-002', itemName: '小米 Pad 6S Pro', spec: '12GB+512GB', unit: '台', category: '平板', stockQty: 54 },
  { itemCode: 'XM-003', itemName: '小米电视 S Pro', spec: '75英寸', unit: '台', category: '电视', stockQty: 18 },
  { itemCode: 'OP-001', itemName: 'OPPO Find X7 Ultra', spec: '16GB+512GB', unit: '台', category: '手机', stockQty: 72 },
  { itemCode: 'VV-001', itemName: 'vivo X Fold3 Pro', spec: '16GB+1TB', unit: '台', category: '折叠屏', stockQty: 25 },
  { itemCode: 'AP-001', itemName: 'Apple iPhone 15 Pro Max', spec: '512GB', unit: '台', category: '手机', stockQty: 64 },
  { itemCode: 'AP-002', itemName: 'Apple iPad Pro', spec: '13英寸 512GB', unit: '台', category: '平板', stockQty: 42 },
  { itemCode: 'LN-001', itemName: '联想 ThinkBook 16+', spec: '32GB+1TB', unit: '台', category: '笔记本', stockQty: 31 },
  { itemCode: 'DL-001', itemName: '戴尔 UltraSharp 显示器', spec: 'U2723QE', unit: '台', category: '显示器', stockQty: 57 }
];

const includesText = (source: string, keyword: string) => source.toLowerCase().includes(keyword.toLowerCase());

export const filterMaterialList = (queryParams: MaterialQueryParams) => {
  return mockMaterialList.filter(item => {
    const matchItemCode = !queryParams.itemCode.trim() || includesText(item.itemCode, queryParams.itemCode.trim());
    const matchItemName = !queryParams.itemName.trim() || includesText(item.itemName, queryParams.itemName.trim());
    const matchSpec = !queryParams.spec.trim() || includesText(item.spec, queryParams.spec.trim());
    const matchCategory = !queryParams.category.trim() || includesText(item.category, queryParams.category.trim());
    return matchItemCode && matchItemName && matchSpec && matchCategory;
  });
};

export const findMaterialByCode = (itemCode: string) => {
  const normalizedCode = itemCode.trim().toLowerCase();
  if (!normalizedCode) {
    return undefined;
  }
  return mockMaterialList.find(item => item.itemCode.toLowerCase() === normalizedCode);
};
