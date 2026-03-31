package com.soli.business.core.service.impl.purchaseorder;

import com.soli.business.service.purchaseorder.CurrencyEnum;
import com.soli.business.service.purchaseorder.PurchaseOrderSettleTypeEnum;
import com.soli.business.service.purchaseorder.PurchaseOrderStatusEnum;
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

    private PurchaseOrderSettleTypeEnum settleType;

    private Long warehouseId;

    private String warehouseName;

    private String userName;

    private CurrencyEnum currency;

    private String remark;

    private PurchaseOrderStatusEnum status;

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
