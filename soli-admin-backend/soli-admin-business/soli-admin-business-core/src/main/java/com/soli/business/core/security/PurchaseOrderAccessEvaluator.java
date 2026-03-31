package com.soli.business.core.security;

import com.soli.business.core.mapper.PurchaseOrderMapper;
import com.soli.business.core.service.impl.purchaseorder.PurchaseOrderEntity;
import com.soli.business.service.purchaseorder.PurchaseOrderStatusEnum;
import com.soli.common.core.security.CompanyContextHolder;
import com.soli.system.core.security.SysModuleAccessEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 采购订单按钮权限校验器
 *
 * @author lizhengqiang
 * @since 2026-03-29 23:55
 */
@Component("purchaseOrderAccess")
@RequiredArgsConstructor
public class PurchaseOrderAccessEvaluator {

    private static final String MODULE_CODE = "purchase_order";

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final SysModuleAccessEvaluator sysModuleAccessEvaluator;

    /**
     * 按状态编码校验按钮权限。
     *
     * @param buttonCode 按钮编码
     * @param stateCode 状态编码
     * @return 是否允许访问
     */
    public boolean hasStateButton(final String buttonCode, final String stateCode) {
        return sysModuleAccessEvaluator.hasStateButton(MODULE_CODE, buttonCode, stateCode);
    }

    /**
     * 按当前单据状态校验按钮权限。
     *
     * @param buttonCode 按钮编码
     * @param orderId 单据 ID
     * @return 是否允许访问
     */
    public boolean hasOrderButton(final String buttonCode, final Long orderId) {
        Long companyId = currentCompanyId();
        if (orderId == null || companyId == null) {
            return false;
        }
        PurchaseOrderEntity entity = purchaseOrderMapper.selectById(orderId, companyId);
        if (entity == null) {
            return false;
        }
        PurchaseOrderStatusEnum status = entity.getStatus();
        return sysModuleAccessEvaluator.hasStateButton(MODULE_CODE, buttonCode, status == null ? null : status.getValue());
    }

    /**
     * 获取当前公司 ID。
     *
     * @return 公司 ID
     */
    private Long currentCompanyId() {
        Long companyId = CompanyContextHolder.getCurrentCompanyId();
        if (companyId != null) {
            return companyId;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details instanceof Long currentCompanyId) {
            return currentCompanyId;
        }
        return null;
    }

}
