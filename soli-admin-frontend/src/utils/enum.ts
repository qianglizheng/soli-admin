import type { EnumValue } from '@/types/enums';

export function getEnumCode<TCode extends string | number>(
  value?: EnumValue<TCode> | TCode | null
): TCode | undefined {
  if (value === null || value === undefined) {
    return undefined;
  }
  if (typeof value === 'object') {
    return value.code;
  }
  return value;
}

export function getEnumName<TCode extends string | number>(
  value?: EnumValue<TCode> | null
): string {
  return value?.name || '';
}
