package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单明细
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderItemDTO {

    private Long id;

    private String itemCode;

    private String itemName;

    private String spec;

    private String unit;

    private BigDecimal qty;

    private BigDecimal priceExcl;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;

    private String note;
}
