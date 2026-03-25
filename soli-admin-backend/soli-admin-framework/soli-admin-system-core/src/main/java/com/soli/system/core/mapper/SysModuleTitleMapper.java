package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysmoduletitle.SysModuleFieldTitleEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 模块字段标题持久层
 *
 * @author lizhengqiang
 * @since 2026-03-25 22:10
 */
public interface SysModuleTitleMapper {

    /**
     * 根据字段 ID 和语言区域查询字段标题
     *
     * @param fieldId 字段 ID
     * @param locale 语言区域
     * @return 字段标题实体
     */
    SysModuleFieldTitleEntity selectByFieldIdAndLocale(@Param("fieldId") Long fieldId, @Param("locale") String locale);

    /**
     * 新增字段标题
     *
     * @param entity 字段标题实体
     * @return 影响行数
     */
    int insert(SysModuleFieldTitleEntity entity);

    /**
     * 更新字段标题
     *
     * @param entity 字段标题实体
     * @return 影响行数
     */
    int update(SysModuleFieldTitleEntity entity);

    /**
     * 根据字段 ID 删除字段标题
     *
     * @param fieldId 字段 ID
     * @return 影响行数
     */
    int deleteByFieldId(@Param("fieldId") Long fieldId);

}
