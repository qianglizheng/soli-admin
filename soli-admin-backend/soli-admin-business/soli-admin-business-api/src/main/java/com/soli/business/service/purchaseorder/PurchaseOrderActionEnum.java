package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 采购订单动作枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:08
 */
@Getter
@RequiredArgsConstructor
public enum PurchaseOrderActionEnum implements DbEnum<String> {

    CREATE("create", "创建", null, null, PurchaseOrderActivityTypeEnum.PRIMARY),
    MODIFY("modify", "修改", PurchaseOrderStatusEnum.UNAUDITED, PurchaseOrderStatusEnum.UNAUDITED, PurchaseOrderActivityTypeEnum.PRIMARY),
    SUBMIT("submit", "提交", PurchaseOrderStatusEnum.UNAUDITED, PurchaseOrderStatusEnum.PRE_AUDITED, PurchaseOrderActivityTypeEnum.WARNING),
    AUDIT("audit", "审核", PurchaseOrderStatusEnum.PRE_AUDITED, PurchaseOrderStatusEnum.AUDITED, PurchaseOrderActivityTypeEnum.SUCCESS),
    SHIP("ship", "发运", PurchaseOrderStatusEnum.AUDITED, PurchaseOrderStatusEnum.SHIPPED, PurchaseOrderActivityTypeEnum.SUCCESS),
    COMPLETE("complete", "完成", PurchaseOrderStatusEnum.SHIPPED, PurchaseOrderStatusEnum.COMPLETED, PurchaseOrderActivityTypeEnum.SUCCESS);

    private final String value;

    private final String label;

    private final PurchaseOrderStatusEnum fromStatus;

    private final PurchaseOrderStatusEnum toStatus;

    private final PurchaseOrderActivityTypeEnum activityType;
}
