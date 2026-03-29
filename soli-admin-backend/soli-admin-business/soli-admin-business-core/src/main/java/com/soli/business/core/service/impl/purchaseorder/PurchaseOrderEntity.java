package com.soli.business.core.service.impl.purchaseorder;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 采购订单实体
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderEntity extends BaseEntity {

    private Long companyId;

    private String billNo;

    private LocalDate billDate;

    private Long supplierId;

    private String supplierName;

    private String settleType;

    private Long warehouseId;

    private String warehouseName;

    private String userName;

    private String currency;

    private String remark;

    private String status;

    private Long createUserId;

    private String createByName;

    private Long auditUserId;

    private String auditUserName;

    private LocalDateTime auditTime;

    private LocalDateTime submitTime;

    private LocalDateTime shipTime;

    private LocalDateTime completeTime;

    private BigDecimal totalQty;

    private BigDecimal netAmount;

    private BigDecimal taxAmount;

    private BigDecimal totalAmount;
}
