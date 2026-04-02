export type EnumCode = string | number;

export interface EnumValue<TCode extends EnumCode> {
  code: TCode;
  name: string;
}

type SwitchCode = '0' | '1';

export type YesNoCode = 'N' | 'Y';
export type YesNoValue = EnumValue<YesNoCode>;

export type BinaryFlagCode = SwitchCode;
export type BinaryFlagValue = EnumValue<BinaryFlagCode>;

export type NormalDisableStatusCode = SwitchCode;
export type NormalDisableStatusValue = EnumValue<NormalDisableStatusCode>;

export type ModuleTypeCode = 'CATALOG' | 'PAGE' | 'BILL';
export type ModuleTypeValue = EnumValue<ModuleTypeCode>;

export type ModuleComponentTypeCode =
  | 'input'
  | 'search-select'
  | 'date'
  | 'datetime'
  | 'select'
  | 'radio'
  | 'switch'
  | 'text'
  | 'textarea'
  | 'number'
  | 'amount'
  | 'tag'
  | 'button';
export type ModuleComponentTypeValue = EnumValue<ModuleComponentTypeCode>;

export type ModuleValueTypeCode = 'string' | 'long' | 'date' | 'datetime' | 'decimal' | 'int' | 'boolean';
export type ModuleValueTypeValue = EnumValue<ModuleValueTypeCode>;

export type PermissionLevelCode = 0 | 1 | 2;
export type PermissionLevelValue = EnumValue<PermissionLevelCode>;

export type OrgTypeCode = 'GROUP' | 'HEADQUARTERS' | 'BRANCH';
export type OrgTypeValue = EnumValue<OrgTypeCode>;

export type PostTypeCode = 'MANAGER' | 'BUYER' | 'OPERATION' | 'FINANCE';
export type PostTypeValue = EnumValue<PostTypeCode>;

export type UserSexCode = '0' | '1' | '2';
export type UserSexValue = EnumValue<UserSexCode>;

export type UserTypeCode = SwitchCode;
export type UserTypeValue = EnumValue<UserTypeCode>;

export type CurrencyCode = 'CNY';
export type CurrencyValue = EnumValue<CurrencyCode>;

export type PurchaseOrderStatusCode = 'unaudited' | 'pre_audited' | 'audited' | 'shipped' | 'completed';
export type PurchaseOrderStatusValue = EnumValue<PurchaseOrderStatusCode>;

export type PurchaseOrderSettleTypeCode = 'monthly' | 'cash';
export type PurchaseOrderSettleTypeValue = EnumValue<PurchaseOrderSettleTypeCode>;

export type PurchaseOrderSourceTypeCode = '采购申请';
export type PurchaseOrderSourceTypeValue = EnumValue<PurchaseOrderSourceTypeCode>;

export type PurchaseOrderSourceStatusCode = '已审核';
export type PurchaseOrderSourceStatusValue = EnumValue<PurchaseOrderSourceStatusCode>;

export type PurchaseOrderActivityTypeCode = 'primary' | 'warning' | 'success';
export type PurchaseOrderActivityTypeValue = EnumValue<PurchaseOrderActivityTypeCode>;

export type PurchaseOrderActionCode = 'create' | 'modify' | 'submit' | 'audit' | 'ship' | 'complete';
export type PurchaseOrderActionValue = EnumValue<PurchaseOrderActionCode>;
