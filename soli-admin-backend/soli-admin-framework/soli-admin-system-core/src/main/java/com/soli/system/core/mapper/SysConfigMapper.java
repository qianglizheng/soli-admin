package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysconfig.SysConfigEntity;
import com.soli.system.service.sysconfig.SysConfigQuery;

/**
 * 参数配置 Mapper
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:42
 */
public interface SysConfigMapper extends BaseCrudMapper<SysConfigEntity, SysConfigQuery> {

    /**
     * 根据参数键名查询
     *
     * @param configKey 参数键名
     * @return 参数配置
     */
    SysConfigEntity selectByConfigKey(String configKey);

}
