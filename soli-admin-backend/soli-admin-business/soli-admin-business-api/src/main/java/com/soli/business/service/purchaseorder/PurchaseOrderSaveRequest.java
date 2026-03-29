package com.soli.business.service.purchaseorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 采购订单保存请求
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderSaveRequest {

    /**
     * 单据 ID，新增时为空
     */
    private Long id;

    /**
     * 单据日期
     */
    @NotBlank(message = "单据日期不能为空")
    private String billDate;

    /**
     * 供应商 ID
     */
    @NotNull(message = "供应商不能为空")
    private Long supplierId;

    /**
     * 结算方式
     */
    @NotBlank(message = "结算方式不能为空")
    private String settleType;

    /**
     * 仓库 ID
     */
    @NotNull(message = "仓库不能为空")
    private Long warehouseId;

    /**
     * 采购员
     */
    @NotBlank(message = "采购员不能为空")
    private String userName;

    /**
     * 币种
     */
    @NotBlank(message = "币种不能为空")
    private String currency;

    /**
     * 备注
     */
    private String remark;

    /**
     * 订单明细列表
     */
    @Valid
    @NotEmpty(message = "订单明细不能为空")
    private List<PurchaseOrderItemSaveRequest> items = new ArrayList<>();
}
