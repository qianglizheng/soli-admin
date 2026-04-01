package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 采购订单状态枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:07
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderStatusEnum implements DbEnum<String> {

    UNAUDITED("unaudited", "未审核"),
    PRE_AUDITED("pre_audited", "待审核"),
    AUDITED("audited", "已审核"),
    SHIPPED("shipped", "已发运"),
    COMPLETED("completed", "已完成");

    private final String value;

    private final String name;
}