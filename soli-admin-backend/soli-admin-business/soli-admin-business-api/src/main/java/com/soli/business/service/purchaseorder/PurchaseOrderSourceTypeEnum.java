package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 来源单据类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:08
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderSourceTypeEnum implements DbEnum<String> {

    PURCHASE_REQUEST("采购申请", "采购申请");

    private final String value;

    private final String name;
}