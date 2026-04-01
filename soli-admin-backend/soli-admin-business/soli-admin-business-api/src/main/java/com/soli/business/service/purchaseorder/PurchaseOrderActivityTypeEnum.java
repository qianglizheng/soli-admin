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

    PRIMARY("primary", "主要"),
    WARNING("warning", "警告"),
    SUCCESS("success", "成功");

    private final String value;

    private final String name;
}