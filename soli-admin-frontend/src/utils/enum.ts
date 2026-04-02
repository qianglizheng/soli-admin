import type { EnumCode, EnumValue } from '@/types/enums';

export function getEnumCode<TCode extends EnumCode>(
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

export function getEnumName<TCode extends EnumCode>(
  value?: EnumValue<TCode> | null
): string {
  return value?.name || '';
}
