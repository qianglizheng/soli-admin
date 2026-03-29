export const normalizeText = (value: unknown) => {
  return String(value ?? '').trim();
};

export const buildTextFilters = <T>(rows: T[], getter: (row: T) => unknown) => {
  const seen = new Set<string>();
  return rows.reduce<Array<{ text: string; value: string }>>((list, row) => {
    const text = normalizeText(getter(row));
    if (!text || seen.has(text)) {
      return list;
    }
    seen.add(text);
    list.push({ text, value: text });
    return list;
  }, []);
};

export const matchTextFilter = <T>(value: string, row: T, getter: (row: T) => unknown) => {
  return normalizeText(getter(row)) === normalizeText(value);
};

export const compareText = (left: unknown, right: unknown) => {
  return normalizeText(left).localeCompare(normalizeText(right), 'zh-CN');
};

export const toNumber = (value: unknown) => {
  const amount = Number(String(value ?? '').replace(/,/g, ''));
  return Number.isFinite(amount) ? amount : 0;
};

export const compareNumber = (left: unknown, right: unknown) => {
  return toNumber(left) - toNumber(right);
};

export const parseFileSize = (value: unknown) => {
  const text = normalizeText(value).toUpperCase();
  const match = text.match(/^([\d.]+)\s*(B|KB|MB|GB)$/);
  if (!match) {
    return 0;
  }
  const size = Number(match[1]);
  const unitMap = {
    B: 1,
    KB: 1024,
    MB: 1024 * 1024,
    GB: 1024 * 1024 * 1024
  } as const;
  const unit = match[2] as keyof typeof unitMap;
  return size * unitMap[unit];
};
