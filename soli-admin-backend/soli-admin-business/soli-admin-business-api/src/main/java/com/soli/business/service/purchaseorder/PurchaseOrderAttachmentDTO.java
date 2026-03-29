package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

/**
 * 采购订单附件
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderAttachmentDTO {

    private Long id;

    private String fileName;

    private String fileType;

    private String fileSize;

    private String uploadUser;

    private String uploadTime;

    private String remark;
}
