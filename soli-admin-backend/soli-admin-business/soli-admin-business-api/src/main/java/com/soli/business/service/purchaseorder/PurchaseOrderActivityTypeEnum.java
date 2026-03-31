package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 采购订单日志类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:06
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderActivityTypeEnum implements DbEnum<String> {

    PRIMARY("primary"),
    WARNING("warning"),
    SUCCESS("success");

    private final String value;
}
