package com.soli.business.service.purchaseorder;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 采购订单分页查询参数
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderPageQuery extends PageQuery {

    private Long companyId;

    private String billNo;

    private Long supplierId;

    private PurchaseOrderStatusEnum status;

    private String startBillDate;

    private String endBillDate;

    private String userName;

    private Long warehouseId;
}
