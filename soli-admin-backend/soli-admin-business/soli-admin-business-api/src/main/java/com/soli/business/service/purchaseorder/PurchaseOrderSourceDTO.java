package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单来源单据
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderSourceDTO {

    private Long id;

    private String sourceBillNo;

    private String sourceType;

    private String supplierName;

    private String billDate;

    private BigDecimal totalAmount;

    private String status;

    private String remark;
}
