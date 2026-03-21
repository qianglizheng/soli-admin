package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysdict.SysDictEntity;
import com.soli.system.service.sysdict.SysDictQuery;

/**
 * 字典类型 Mapper
 *
 * @author lizhengqiang
 * @since 2026-03-14 00:55
 */
public interface SysDictMapper extends BaseCrudMapper<SysDictEntity, SysDictQuery> {

    /**
     * 根据字典类型查询
     *
     * @param type 字典类型
     * @return 字典信息
     */
    SysDictEntity selectByType(String type);

}
