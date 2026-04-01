export interface EnumValue<TCode extends string | number> {
  code: TCode;
  name: string;
}

export type EnumValueCode<TEnum extends EnumValue<string | number>> = TEnum['code'];

export type YesNoEnum = EnumValue<'N' | 'Y'>;
export type YesNoEnumCode = EnumValueCode<YesNoEnum>;

export type BinaryFlagEnum = EnumValue<'0' | '1'>;
export type BinaryFlagEnumCode = EnumValueCode<BinaryFlagEnum>;

export type NormalDisableStatusEnum = EnumValue<'0' | '1'>;
export type NormalDisableStatusEnumCode = EnumValueCode<NormalDisableStatusEnum>;

export type ModuleTypeEnum = EnumValue<'CATALOG' | 'PAGE' | 'BILL'>;
export type ModuleTypeEnumCode = EnumValueCode<ModuleTypeEnum>;

export type ModuleComponentTypeEnum = EnumValue<
  'input'
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
  | 'button'
>;
export type ModuleComponentTypeEnumCode = EnumValueCode<ModuleComponentTypeEnum>;

export type ModuleValueTypeEnum = EnumValue<'string' | 'long' | 'date' | 'datetime' | 'decimal' | 'int' | 'boolean'>;
export type ModuleValueTypeEnumCode = EnumValueCode<ModuleValueTypeEnum>;

export type PermissionLevelEnum = EnumValue<0 | 1 | 2>;
export type PermissionLevelEnumCode = EnumValueCode<PermissionLevelEnum>;

export type OrgTypeEnum = EnumValue<'GROUP' | 'HEADQUARTERS' | 'BRANCH'>;
export type OrgTypeEnumCode = EnumValueCode<OrgTypeEnum>;

export type PostTypeEnum = EnumValue<'MANAGER' | 'BUYER' | 'OPERATION' | 'FINANCE'>;
export type PostTypeEnumCode = EnumValueCode<PostTypeEnum>;

export type UserSexEnum = EnumValue<'0' | '1' | '2'>;
export type UserSexEnumCode = EnumValueCode<UserSexEnum>;

export type UserTypeEnum = EnumValue<'0' | '1'>;
export type UserTypeEnumCode = EnumValueCode<UserTypeEnum>;

export type CurrencyEnum = EnumValue<'CNY'>;
export type CurrencyEnumCode = EnumValueCode<CurrencyEnum>;

export type PurchaseOrderStatusEnum = EnumValue<'unaudited' | 'pre_audited' | 'audited' | 'shipped' | 'completed'>;
export type PurchaseOrderStatusEnumCode = EnumValueCode<PurchaseOrderStatusEnum>;

export type PurchaseOrderSettleTypeEnum = EnumValue<'monthly' | 'cash'>;
export type PurchaseOrderSettleTypeEnumCode = EnumValueCode<PurchaseOrderSettleTypeEnum>;

export type PurchaseOrderSourceTypeEnum = EnumValue<'采购申请'>;
export type PurchaseOrderSourceTypeEnumCode = EnumValueCode<PurchaseOrderSourceTypeEnum>;

export type PurchaseOrderSourceStatusEnum = EnumValue<'已审核'>;
export type PurchaseOrderSourceStatusEnumCode = EnumValueCode<PurchaseOrderSourceStatusEnum>;

export type PurchaseOrderActivityTypeEnum = EnumValue<'primary' | 'warning' | 'success'>;
export type PurchaseOrderActivityTypeEnumCode = EnumValueCode<PurchaseOrderActivityTypeEnum>;

export type PurchaseOrderActionEnum = EnumValue<'create' | 'modify' | 'submit' | 'audit' | 'ship' | 'complete'>;
export type PurchaseOrderActionEnumCode = EnumValueCode<PurchaseOrderActionEnum>;