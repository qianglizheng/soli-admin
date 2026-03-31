package com.soli.business.service.purchaseorder;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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
    private PurchaseOrderSettleTypeEnum settleType;

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
    private CurrencyEnum currency;

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

    /**
     * 采购订单明细保存请求
     *
     * 将明细请求内聚到主请求对象，避免增量编译或运行时类路径中出现独立类缺失，
     * 导致 Jackson 反序列化失败。
     */
    @Getter
    @Setter
    public static class PurchaseOrderItemSaveRequest {

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
}
