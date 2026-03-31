package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单列表项
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderListItemDTO {

    private Long id;

    private String billNo;

    private String billType;

    private Long supplierId;

    private String supplierName;

    private String billDate;

    private String userName;

    private Long warehouseId;

    private String warehouseName;

    private BigDecimal totalAmount;

    private BigDecimal netAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalQty;

    private PurchaseOrderStatusEnum status;

    private String statusName;
}
