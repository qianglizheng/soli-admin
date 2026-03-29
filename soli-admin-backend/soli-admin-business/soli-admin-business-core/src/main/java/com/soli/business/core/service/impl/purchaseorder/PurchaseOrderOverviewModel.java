package com.soli.business.core.service.impl.purchaseorder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单统计模型
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderOverviewModel {

    private Long totalCount;

    private Long unauditedCount;

    private Long preAuditedCount;

    private Long completedCount;

    private BigDecimal totalAmount;
}
