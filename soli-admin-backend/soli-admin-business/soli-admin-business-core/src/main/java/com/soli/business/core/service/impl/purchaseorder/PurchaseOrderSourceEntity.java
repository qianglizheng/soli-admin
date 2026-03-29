package com.soli.business.core.service.impl.purchaseorder;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 采购订单来源单据实体
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderSourceEntity extends BaseEntity {

    private Long orderId;

    private Integer sort;

    private String sourceBillNo;

    private String sourceType;

    private String supplierName;

    private LocalDate billDate;

    private BigDecimal totalAmount;

    private String status;
}
