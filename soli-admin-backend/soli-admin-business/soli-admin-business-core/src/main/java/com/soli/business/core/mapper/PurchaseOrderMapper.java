package com.soli.business.core.mapper;

import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderActivityEntity;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderAttachmentEntity;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderEntity;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderItemEntity;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderOverviewModel;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderSourceEntity;
import com.soli.business.service.purchaseorder.PurchaseOrderPageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 采购订单 Mapper
 *
 * @author lizhengqiang
 * @since 2026-03-29 16:20
 */
public interface PurchaseOrderMapper {

    /**
     * 查询分页数据
     *
     * @param query 分页查询参数
     * @return 实体列表
     */
    List<PurchaseOrderEntity> selectPage(PurchaseOrderPageQuery query);

    /**
     * 查询概览统计数据
     *
     * @param companyId 公司 ID
     * @return 统计模型
     */
    PurchaseOrderOverviewModel selectOverview(@Param("companyId") Long companyId);

    /**
     * 按 ID 查询单据
     *
     * @param id 单据 ID
     * @param companyId 公司 ID
     * @return 单据实体
     */
    PurchaseOrderEntity selectById(@Param("id") Long id, @Param("companyId") Long companyId);

    /**
     * 新增单据表头
     *
     * @param entity 单据实体
     * @return 影响行数
     */
    int insert(PurchaseOrderEntity entity);

    /**
     * 修改单据表头
     *
     * @param entity 单据实体
     * @return 影响行数
     */
    int update(PurchaseOrderEntity entity);

    /**
     * 查询单据明细
     *
     * @param orderId 单据 ID
     * @return 明细列表
     */
    List<PurchaseOrderItemEntity> selectItemListByOrderId(@Param("orderId") Long orderId);

    /**
     * 删除单据明细
     *
     * @param orderId 单据 ID
     * @return 影响行数
     */
    int deleteItemListByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量新增单据明细
     *
     * @param entityList 明细列表
     * @return 影响行数
     */
    int insertItemBatch(@Param("list") List<PurchaseOrderItemEntity> entityList);

    /**
     * 查询来源单据
     *
     * @param orderId 单据 ID
     * @return 来源单据列表
     */
    List<PurchaseOrderSourceEntity> selectSourceListByOrderId(@Param("orderId") Long orderId);

    /**
     * 删除来源单据
     *
     * @param orderId 单据 ID
     * @return 影响行数
     */
    int deleteSourceListByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量新增来源单据
     *
     * @param entityList 来源单据列表
     * @return 影响行数
     */
    int insertSourceBatch(@Param("list") List<PurchaseOrderSourceEntity> entityList);

    /**
     * 查询附件
     *
     * @param orderId 单据 ID
     * @return 附件列表
     */
    List<PurchaseOrderAttachmentEntity> selectAttachmentListByOrderId(@Param("orderId") Long orderId);

    /**
     * 删除附件
     *
     * @param orderId 单据 ID
     * @return 影响行数
     */
    int deleteAttachmentListByOrderId(@Param("orderId") Long orderId);

    /**
     * 批量新增附件
     *
     * @param entityList 附件列表
     * @return 影响行数
     */
    int insertAttachmentBatch(@Param("list") List<PurchaseOrderAttachmentEntity> entityList);

    /**
     * 查询操作日志
     *
     * @param orderId 单据 ID
     * @return 操作日志列表
     */
    List<PurchaseOrderActivityEntity> selectActivityListByOrderId(@Param("orderId") Long orderId);

    /**
     * 新增操作日志
     *
     * @param entity 日志实体
     * @return 影响行数
     */
    int insertActivity(PurchaseOrderActivityEntity entity);
}
