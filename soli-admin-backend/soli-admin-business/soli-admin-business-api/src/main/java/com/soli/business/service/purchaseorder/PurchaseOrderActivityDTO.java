package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

/**
 * 采购订单操作日志
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderActivityDTO {

    private Long id;

    private String content;

    private String timestamp;

    private String operator;

    private PurchaseOrderActivityTypeEnum type;
}
