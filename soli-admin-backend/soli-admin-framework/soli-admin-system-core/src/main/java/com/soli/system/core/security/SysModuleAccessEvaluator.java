package com.soli.system.core.security;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysModulePermissionMapper;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 模块权限表达式计算器
 *
 * @author lizhengqiang
 * @since 2026-03-25 17:10
 */
@Component("moduleAccess")
@RequiredArgsConstructor
public class SysModuleAccessEvaluator {

    private final SysModulePermissionMapper sysModulePermissionMapper;

    private final SysModuleContextService sysModuleContextService;

    public boolean hasModule(String moduleCode) {
        Long userId = currentUserId();
        Long companyId = currentCompanyId();
        if (userId == null || companyId == null || moduleCode == null || moduleCode.isBlank()) {
            return false;
        }
        return sysModulePermissionMapper.countUserVisibleModule(userId, companyId, moduleCode) > 0;
    }

    public boolean hasButton(String moduleCode, String buttonCode) {
        Long userId = currentUserId();
        Long companyId = currentCompanyId();
        if (userId == null || companyId == null || moduleCode == null || moduleCode.isBlank() || buttonCode == null || buttonCode.isBlank()) {
            return false;
        }
        Integer permissionLevel = sysModulePermissionMapper.selectUserButtonPermissionLevel(userId, companyId, moduleCode, buttonCode);
        return permissionLevel != null && permissionLevel >= 2;
    }

    public boolean hasStateButton(String moduleCode, String buttonCode, String stateCode) {
        Long userId = currentUserId();
        Long companyId = currentCompanyId();
        if (userId == null
                || companyId == null
                || !StringUtils.hasText(moduleCode)
                || !StringUtils.hasText(buttonCode)
                || !StringUtils.hasText(stateCode)) {
            return false;
        }
        try {
            SysModuleContextDTO context = sysModuleContextService.buildContext(moduleCode, userId, companyId, stateCode);
            SysModuleContextDTO.ButtonItem buttonItem = resolveButtonItem(context, buttonCode);
            return buttonItem != null
                    && Boolean.TRUE.equals(buttonItem.getVisible())
                    && !Boolean.TRUE.equals(buttonItem.getDisabled());
        } catch (BusinessException ex) {
            return false;
        }
    }

    private SysModuleContextDTO.ButtonItem resolveButtonItem(SysModuleContextDTO context, String buttonCode) {
        if (context == null || context.getButtons() == null || !StringUtils.hasText(buttonCode)) {
            return null;
        }
        SysModuleContextDTO.ButtonItem buttonItem = resolveButtonItem(context.getButtons().getListToolbar(), buttonCode);
        if (buttonItem != null) {
            return buttonItem;
        }
        buttonItem = resolveButtonItem(context.getButtons().getListRow(), buttonCode);
        if (buttonItem != null) {
            return buttonItem;
        }
        buttonItem = resolveButtonItem(context.getButtons().getHeaderToolbar(), buttonCode);
        if (buttonItem != null) {
            return buttonItem;
        }
        return resolveButtonItem(context.getButtons().getDetailRow(), buttonCode);
    }

    private SysModuleContextDTO.ButtonItem resolveButtonItem(Map<String, SysModuleContextDTO.ButtonItem> buttonMap,
                                                             String buttonCode) {
        if (buttonMap == null || !StringUtils.hasText(buttonCode)) {
            return null;
        }
        return buttonMap.get(buttonCode);
    }

    private Long currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object details = authentication.getDetails();
        if (details instanceof Long companyId) {
            return companyId;
        }
        return null;
    }

}
