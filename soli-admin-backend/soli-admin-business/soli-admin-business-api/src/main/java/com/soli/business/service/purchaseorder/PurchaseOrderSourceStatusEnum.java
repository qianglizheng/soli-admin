package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 来源单据状态枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:07
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderSourceStatusEnum implements DbEnum<String> {

    APPROVED("已审批");

    private final String value;
}
