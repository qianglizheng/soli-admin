package com.soli.business.service.purchaseorder;

import com.soli.common.api.vo.PageResult;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 采购订单分页结果
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderPageDTO {

    private List<PurchaseOrderOverviewCardDTO> overviewCards;

    private PageResult<PurchaseOrderListItemDTO> page;
}
