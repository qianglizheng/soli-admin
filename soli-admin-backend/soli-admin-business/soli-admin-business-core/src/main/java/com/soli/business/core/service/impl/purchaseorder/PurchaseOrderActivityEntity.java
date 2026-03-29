package com.soli.business.core.service.impl.purchaseorder;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 采购订单操作日志实体
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderActivityEntity extends BaseEntity {

    private Long orderId;

    private String actionCode;

    private String actionName;

    private String content;

    private Long operatorUserId;

    private String operatorName;

    private String activityType;

    private LocalDateTime operateTime;
}
