package com.soli.common.core.security;

/**
 * 当前公司上下文持有器
 *
 * @author lizhengqiang
 * @since 2026-03-29 21:18
 */
public final class CompanyContextHolder {

    private static final ThreadLocal<Long> CURRENT_COMPANY_ID = new ThreadLocal<>();

    private CompanyContextHolder() {
    }

    public static void setCurrentCompanyId(final Long companyId) {
        if (companyId == null) {
            CURRENT_COMPANY_ID.remove();
            return;
        }
        CURRENT_COMPANY_ID.set(companyId);
    }

    public static Long getCurrentCompanyId() {
        return CURRENT_COMPANY_ID.get();
    }

    public static void clear() {
        CURRENT_COMPANY_ID.remove();
    }

}
