package com.soli.business.service.purchaseorder;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 采购订单明细保存请求
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderItemSaveRequest {

    /**
     * 明细 ID，新增时为空
     */
    private Long id;

    /**
     * 物料编码
     */
    @NotBlank(message = "物料编码不能为空")
    private String itemCode;

    /**
     * 物料名称
     */
    @NotBlank(message = "物料名称不能为空")
    private String itemName;

    /**
     * 规格型号
     */
    private String spec;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    private BigDecimal qty;

    /**
     * 不含税单价
     */
    @NotNull(message = "单价不能为空")
    private BigDecimal priceExcl;

    /**
     * 税率
     */
    @NotNull(message = "税率不能为空")
    private BigDecimal taxRate;

    /**
     * 行备注
     */
    private String note;
}
