package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysdictdata.SysDictDataEntity;
import com.soli.system.service.sysdictdata.SysDictDataQuery;
import org.apache.ibatis.annotations.Param;

/**
 * 字典数据 Mapper
 *
 * @author lizhengqiang
 * @since 2026-03-14 00:54
 */
public interface SysDictDataMapper extends BaseCrudMapper<SysDictDataEntity, SysDictDataQuery> {

    /**
     * 根据字典类型 ID 删除字典数据
     *
     * @param dictTypeId 字典类型 ID
     * @return 影响行数
     */
    int deleteByDictTypeId(Long dictTypeId);

    /**
     * 清理同一字典下的默认值标记
     *
     * @param dictTypeId 字典类型 ID
     * @param excludeId  排除的字典数据 ID
     * @return 影响行数
     */
    int clearDefaultByDictTypeId(@Param("dictTypeId") Long dictTypeId, @Param("excludeId") Long excludeId);

}
