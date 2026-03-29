import type { ModuleContextConfigItem } from '@/api/moduleCenter';

export interface ResolvedModuleFieldConfig {
  label: string;
  placeholder: string;
  helpText: string;
  visible: boolean;
  editable: boolean;
  required: boolean;
}

export interface ResolvedModuleButtonConfig {
  label: string;
  visible: boolean;
  disabled: boolean;
}

export interface ModuleFieldFallback {
  label: string;
  placeholder?: string;
  helpText?: string;
  visible?: boolean;
  editable?: boolean;
  required?: boolean;
}

export interface ModuleButtonFallback {
  label: string;
  visible?: boolean;
  disabled?: boolean;
}

export const resolveModuleConfig = (
  fieldConfigs: Record<string, ModuleContextConfigItem>,
  component: string,
  code: string
) => {
  return fieldConfigs[`${component}:${code}`];
};

export const buildResolvedFieldConfigMap = <T extends string>(
  fieldConfigs: Record<string, ModuleContextConfigItem>,
  component: string,
  fallbackMap: Record<T, ModuleFieldFallback>
) => {
  return (Object.keys(fallbackMap) as T[]).reduce<Record<T, ResolvedModuleFieldConfig>>((result, fieldCode) => {
    const fallback = fallbackMap[fieldCode];
    const field = resolveModuleConfig(fieldConfigs, component, fieldCode);
    result[fieldCode] = {
      editable: field?.editable ?? fallback.editable ?? true,
      helpText: field?.helpText || fallback.helpText || '',
      label: field?.label || field?.displayTitle || field?.defaultTitle || fallback.label,
      placeholder: field?.placeholder || fallback.placeholder || '',
      required: field?.required ?? fallback.required ?? false,
      visible: field?.visible ?? fallback.visible ?? true
    };
    return result;
  }, {} as Record<T, ResolvedModuleFieldConfig>);
};

export const buildResolvedButtonConfigMap = <T extends string>(
  fieldConfigs: Record<string, ModuleContextConfigItem>,
  fallbackMap: Record<T, ModuleButtonFallback>
) => {
  return (Object.keys(fallbackMap) as T[]).reduce<Record<T, ResolvedModuleButtonConfig>>((result, buttonCode) => {
    const fallback = fallbackMap[buttonCode];
    const button = resolveModuleConfig(fieldConfigs, 'button', buttonCode);
    result[buttonCode] = {
      disabled: button?.disabled ?? fallback.disabled ?? false,
      label: button?.label || button?.displayTitle || button?.defaultTitle || fallback.label,
      visible: button?.visible ?? fallback.visible ?? true
    };
    return result;
  }, {} as Record<T, ResolvedModuleButtonConfig>);
};

export const pickWritableModuleValue = <TField extends string, TValue>(
  fieldConfigMap: Record<TField, ResolvedModuleFieldConfig>,
  fieldCode: TField,
  value: TValue | undefined
) => {
  const fieldConfig = fieldConfigMap[fieldCode];
  if (!fieldConfig?.visible || !fieldConfig.editable) {
    return undefined;
  }
  return value;
};
