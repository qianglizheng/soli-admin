package com.soli.business.service.purchaseorder;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 采购订单详情
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
@Getter
@Setter
public class PurchaseOrderDetailDTO {

    private PurchaseOrderHeaderDTO header;

    private List<PurchaseOrderItemDTO> items;

    private List<PurchaseOrderSourceDTO> sourceBills;

    private List<PurchaseOrderAttachmentDTO> attachments;

    private List<PurchaseOrderActivityDTO> activities;
}
