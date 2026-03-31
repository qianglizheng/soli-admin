package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 采购订单结算方式枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:07
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderSettleTypeEnum implements DbEnum<String> {

    MONTHLY("monthly"),
    CASH("cash");

    private final String value;
}
