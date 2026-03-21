package com.soli.system.service.sysconfig;

import com.soli.system.service.BaseCrudService;

/**
 * 参数配置服务
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:41
 */
public interface SysConfigService extends BaseCrudService<SysConfigDTO, SysConfigQuery> {

    /**
     * 刷新参数缓存
     */
    void refreshCache();

}
