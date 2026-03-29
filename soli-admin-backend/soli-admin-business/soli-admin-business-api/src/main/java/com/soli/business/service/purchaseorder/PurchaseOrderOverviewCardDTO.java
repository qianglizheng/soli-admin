package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

/**
 * 采购订单概览卡片
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderOverviewCardDTO {

    private String key;

    private String label;

    private String value;

    private String unit;

    private String icon;

    private String theme;

    private String footerText;

    private String trendValue;

    private String trendDirection;
}
