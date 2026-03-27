package com.soli.system.service.sysmodule;

/**
 * 模块上下文服务
 *
 * @author lizhengqiang
 * @since 2026-03-27 15:35
 */
public interface SysModuleContextService {

    /**
     * 构建模块上下文
     *
     * @param moduleCode 模块编码
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @param stateCode 当前状态编码
     * @return 模块上下文
     */
    SysModuleContextDTO buildContext(String moduleCode, Long userId, Long companyId, String stateCode);

}
