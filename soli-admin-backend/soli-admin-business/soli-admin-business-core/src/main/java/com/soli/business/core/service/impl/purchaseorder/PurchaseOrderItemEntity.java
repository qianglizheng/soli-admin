package com.soli.business.core.service.impl.purchaseorder;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单明细实体
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderItemEntity extends BaseEntity {

    private Long orderId;

    private Integer sort;

    private String itemCode;

    private String itemName;

    private String spec;

    private String unit;

    private BigDecimal qty;

    private BigDecimal priceExcl;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;
}
