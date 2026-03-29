package com.soli.business.core.service.impl.purchaseorder;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 采购订单附件实体
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderAttachmentEntity extends BaseEntity {

    private Long orderId;

    private Integer sort;

    private String fileName;

    private String fileType;

    private String fileSize;

    private String uploadUser;

    private LocalDateTime uploadTime;
}
