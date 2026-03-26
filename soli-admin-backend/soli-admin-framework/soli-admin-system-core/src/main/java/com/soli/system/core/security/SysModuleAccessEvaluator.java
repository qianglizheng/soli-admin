package com.soli.system.core.security;

import com.soli.system.core.mapper.SysModulePermissionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

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
