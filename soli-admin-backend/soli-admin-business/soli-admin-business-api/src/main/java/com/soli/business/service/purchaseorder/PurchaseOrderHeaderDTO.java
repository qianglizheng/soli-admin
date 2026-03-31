package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

/**
 * 采购订单表头信息
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderHeaderDTO {

    private Long id;

    private String billNo;

    private String billDate;

    private PurchaseOrderStatusEnum status;

    private String statusName;

    private Long supplierId;

    private String supplierName;

    private PurchaseOrderSettleTypeEnum settleType;

    private String deptId;

    private String userName;

    private Long warehouseId;

    private String warehouseName;

    private CurrencyEnum currency;

    private String remark;

    private String createByName;
}
