package com.soli.system.core.security;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.core.security.CompanyContextHolder;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 模块权限表达式计算器
 *
 * @author lizhengqiang
 * @since 2026-03-25 17:10
 */
@Component("moduleAccess")
@RequiredArgsConstructor
public class SysModuleAccessEvaluator {

    private static final String BUTTON_COMPONENT = "button";

    private final SysModulePermissionMapper sysModulePermissionMapper;

    private final SysModuleContextService sysModuleContextService;

    /**
     * 判断当前用户是否可见指定模块。
     *
     * @param moduleCode 模块编码
     * @return 是否可见
     */
    public boolean hasModule(final String moduleCode) {
        Long userId = currentUserId();
        Long companyId = currentCompanyId();
        if (userId == null || companyId == null || !StringUtils.hasText(moduleCode)) {
            return false;
        }
        return sysModulePermissionMapper.countUserVisibleModule(userId, companyId, moduleCode.trim()) > 0;
    }

    /**
     * 判断当前用户是否拥有指定按钮权限。
     *
     * @param moduleCode 模块编码
     * @param buttonCode 按钮编码
     * @return 是否可用
     */
    public boolean hasButton(final String moduleCode, final String buttonCode) {
        Long userId = currentUserId();
        Long companyId = currentCompanyId();
        if (userId == null || companyId == null || !StringUtils.hasText(moduleCode) || !StringUtils.hasText(buttonCode)) {
            return false;
        }
        Integer permissionLevel = sysModulePermissionMapper.selectUserButtonPermissionLevel(
                userId,
                companyId,
                moduleCode.trim(),
                buttonCode.trim()
        );
        return permissionLevel != null && permissionLevel >= 2;
    }

    /**
     * 按当前登录上下文判断状态按钮权限。
     *
     * @param moduleCode 模块编码
     * @param buttonCode 按钮编码
     * @param stateCode 状态编码
     * @return 是否可用
     */
    public boolean hasStateButton(final String moduleCode, final String buttonCode, final String stateCode) {
        return hasStateButton(moduleCode, buttonCode, stateCode, currentUserId(), currentCompanyId());
    }

    /**
     * 按指定用户上下文判断状态按钮权限。
     *
     * @param moduleCode 模块编码
     * @param buttonCode 按钮编码
     * @param stateCode 状态编码
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 是否可用
     */
    public boolean hasStateButton(final String moduleCode,
                                  final String buttonCode,
                                  final String stateCode,
                                  final Long userId,
                                  final Long companyId) {
        if (userId == null
                || companyId == null
                || !StringUtils.hasText(moduleCode)
                || !StringUtils.hasText(buttonCode)
                || !StringUtils.hasText(stateCode)) {
            return false;
        }
        try {
            SysModuleContextDTO context = sysModuleContextService.buildContext(
                    moduleCode.trim(),
                    userId,
                    companyId,
                    stateCode.trim()
            );
            String configKey = BUTTON_COMPONENT + ":" + buttonCode.trim();
            SysModuleContextDTO.FieldConfig buttonConfig = context.getFieldConfigs() == null
                    ? null
                    : context.getFieldConfigs().get(configKey);
            return buttonConfig != null
                    && Boolean.TRUE.equals(buttonConfig.getVisible())
                    && !Boolean.TRUE.equals(buttonConfig.getDisabled());
        } catch (BusinessException ex) {
            return false;
        }
    }

    private Long currentUserId() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long userId) {
            return userId;
        }
        return null;
    }

    private Long currentCompanyId() {
        Long companyId = CompanyContextHolder.getCurrentCompanyId();
        if (companyId != null) {
            return companyId;
        }
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details instanceof Long currentCompanyId) {
            return currentCompanyId;
        }
        return null;
    }

    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

}
