package com.soli.common.web.controller;

import com.soli.common.core.security.CompanyContextHolder;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 控制器基类
 *
 * @author lizhengqiang
 * @since 2026-03-27 16:40
 */
public abstract class BaseController {

    private final SysModuleContextService sysModuleContextService;

    protected BaseController(final SysModuleContextService sysModuleContextService) {
        this.sysModuleContextService = sysModuleContextService;
    }

    protected SysModuleContextDTO buildModuleContext(String moduleCode) {
        return buildModuleContext(moduleCode, null);
    }

    protected SysModuleContextDTO buildModuleContext(String moduleCode, String stateCode) {
        return sysModuleContextService.buildContext(moduleCode, currentUserId(), currentCompanyId(), stateCode);
    }

    protected Long currentUserId() {
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

    protected Long currentCompanyId() {
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
