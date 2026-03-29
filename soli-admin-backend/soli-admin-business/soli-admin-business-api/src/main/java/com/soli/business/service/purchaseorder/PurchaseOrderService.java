package com.soli.business.service.purchaseorder;

/**
 * 采购订单服务接口
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
public interface PurchaseOrderService {

    /**
     * 查询采购订单分页数据
     *
     * @param query 分页查询参数
     * @param companyId 公司 ID
     * @return 分页结果
     */
    PurchaseOrderPageDTO queryPage(PurchaseOrderPageQuery query, Long companyId);

    /**
     * 查询采购订单详情
     *
     * @param id 单据 ID
     * @param companyId 公司 ID
     * @return 详情对象
     */
    PurchaseOrderDetailDTO queryDetail(Long id, Long companyId);

    /**
     * 新增采购订单
     *
     * @param request 保存请求
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 单据 ID
     */
    Long create(PurchaseOrderSaveRequest request, Long userId, Long companyId);

    /**
     * 修改采购订单
     *
     * @param request 保存请求
     * @param userId 用户 ID
     * @param companyId 公司 ID
     */
    void modify(PurchaseOrderSaveRequest request, Long userId, Long companyId);

    /**
     * 执行采购订单动作
     *
     * @param id 单据 ID
     * @param actionCode 动作编码
     * @param userId 用户 ID
     * @param companyId 公司 ID
     */
    void executeAction(Long id, String actionCode, Long userId, Long companyId);
}
