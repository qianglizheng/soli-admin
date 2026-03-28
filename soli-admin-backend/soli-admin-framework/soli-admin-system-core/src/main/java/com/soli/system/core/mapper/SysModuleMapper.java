package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysmodule.SysModuleButtonEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleComponentEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleFieldEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleStateEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleTransitionEntity;
import com.soli.system.core.service.impl.sysmodule.SysModuleTreeNodeModel;
import com.soli.system.service.sysmodule.SysModuleQuery;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Module mapper.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:57
 */
public interface SysModuleMapper extends BaseCrudMapper<SysModuleEntity, SysModuleQuery> {

    List<SysModuleTreeNodeModel> selectTreeNodes();

    List<SysModuleEntity> selectAllModuleList();

    SysModuleEntity selectByModuleCode(@Param("moduleCode") String moduleCode);

    int countChildModuleByParentId(@Param("parentId") Long parentId);

    int updateDescendantAncestors(@Param("oldPath") String oldPath,
                                  @Param("newPath") String newPath,
                                  @Param("updateTime") LocalDateTime updateTime);

    int incrementContextVersion(@Param("moduleId") Long moduleId, @Param("updateTime") LocalDateTime updateTime);

    List<SysModuleComponentEntity> selectComponentsByModuleId(@Param("moduleId") Long moduleId);

    SysModuleComponentEntity selectComponentById(@Param("id") Long id);

    SysModuleComponentEntity selectComponentByModuleIdAndCode(@Param("moduleId") Long moduleId,
                                                              @Param("componentCode") String componentCode);

    int insertComponent(SysModuleComponentEntity entity);

    int updateComponent(SysModuleComponentEntity entity);

    int deleteComponentById(@Param("id") Long id);

    int countComponentByModuleId(@Param("moduleId") Long moduleId);

    List<SysModuleFieldEntity> selectFieldDefinitionsByModuleId(@Param("moduleId") Long moduleId);

    List<SysModuleFieldEntity> selectFieldsByModuleId(@Param("moduleId") Long moduleId);

    SysModuleFieldEntity selectFieldById(@Param("id") Long id);

    SysModuleFieldEntity selectFieldByModuleIdAndCode(@Param("moduleId") Long moduleId, @Param("fieldCode") String fieldCode);

    int insertField(SysModuleFieldEntity entity);

    int updateField(SysModuleFieldEntity entity);

    int deleteFieldById(@Param("id") Long id);

    int countFieldByModuleId(@Param("moduleId") Long moduleId);

    int countFieldByComponentId(@Param("componentId") Long componentId);

    List<SysModuleButtonEntity> selectButtonsByModuleId(@Param("moduleId") Long moduleId);

    SysModuleButtonEntity selectButtonById(@Param("id") Long id);

    SysModuleButtonEntity selectButtonByModuleIdAndCode(@Param("moduleId") Long moduleId, @Param("buttonCode") String buttonCode);

    int insertButton(SysModuleButtonEntity entity);

    int updateButton(SysModuleButtonEntity entity);

    int deleteButtonById(@Param("id") Long id);

    int countButtonByModuleId(@Param("moduleId") Long moduleId);

    List<SysModuleStateEntity> selectStatesByModuleId(@Param("moduleId") Long moduleId);

    List<SysModuleTransitionEntity> selectTransitionsByModuleId(@Param("moduleId") Long moduleId);

    int deleteTransitionsByModuleId(@Param("moduleId") Long moduleId);

    int deleteStatesByModuleId(@Param("moduleId") Long moduleId);

}
