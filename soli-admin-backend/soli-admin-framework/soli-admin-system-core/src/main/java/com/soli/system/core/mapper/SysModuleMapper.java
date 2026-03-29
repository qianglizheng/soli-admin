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

    /**
     * 查询模块树节点列表
     *
     * @return 模块树节点列表
     */
    List<SysModuleTreeNodeModel> selectTreeNodes();

    /**
     * 查询全部模块列表
     *
     * @return 模块列表
     */
    List<SysModuleEntity> selectAllModuleList();

    /**
     * 根据模块编码查询模块
     *
     * @param moduleCode 模块编码
     * @return 模块实体
     */
    SysModuleEntity selectByModuleCode(@Param("moduleCode") String moduleCode);

    /**
     * 统计子模块数量
     *
     * @param parentId 父模块 ID
     * @return 子模块数量
     */
    int countChildModuleByParentId(@Param("parentId") Long parentId);

    /**
     * 更新后代模块祖级路径
     *
     * @param oldPath 原路径
     * @param newPath 新路径
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateDescendantAncestors(@Param("oldPath") String oldPath,
                                  @Param("newPath") String newPath,
                                  @Param("updateTime") LocalDateTime updateTime);

    /**
     * 递增模块上下文版本
     *
     * @param moduleId 模块 ID
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int incrementContextVersion(@Param("moduleId") Long moduleId, @Param("updateTime") LocalDateTime updateTime);

    /**
     * 根据模块 ID 查询组件列表
     *
     * @param moduleId 模块 ID
     * @return 组件列表
     */
    List<SysModuleComponentEntity> selectComponentsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据组件 ID 查询组件
     *
     * @param id 组件 ID
     * @return 组件实体
     */
    SysModuleComponentEntity selectComponentById(@Param("id") Long id);

    /**
     * 根据模块 ID 和组件编码查询组件
     *
     * @param moduleId 模块 ID
     * @param componentCode 组件编码
     * @return 组件实体
     */
    SysModuleComponentEntity selectComponentByModuleIdAndCode(@Param("moduleId") Long moduleId,
                                                              @Param("componentCode") String componentCode);

    /**
     * 新增模块组件
     *
     * @param entity 组件实体
     * @return 影响行数
     */
    int insertComponent(SysModuleComponentEntity entity);

    /**
     * 修改模块组件
     *
     * @param entity 组件实体
     * @return 影响行数
     */
    int updateComponent(SysModuleComponentEntity entity);

    /**
     * 根据组件 ID 删除模块组件
     *
     * @param id 组件 ID
     * @return 影响行数
     */
    int deleteComponentById(@Param("id") Long id);

    /**
     * 统计模块下组件数量
     *
     * @param moduleId 模块 ID
     * @return 组件数量
     */
    int countComponentByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 查询模块字段定义列表
     *
     * @param moduleId 模块 ID
     * @return 字段定义列表
     */
    List<SysModuleFieldEntity> selectFieldDefinitionsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 查询字段列表
     *
     * @param moduleId 模块 ID
     * @return 字段列表
     */
    List<SysModuleFieldEntity> selectFieldsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据字段 ID 查询字段
     *
     * @param id 字段 ID
     * @return 字段实体
     */
    SysModuleFieldEntity selectFieldById(@Param("id") Long id);

    /**
     * 根据模块 ID、组件 ID 和字段编码查询字段
     *
     * @param moduleId 模块 ID
     * @param componentId 组件 ID
     * @param fieldCode 字段编码
     * @return 字段实体
     */
    SysModuleFieldEntity selectFieldByModuleIdAndComponentIdAndCode(@Param("moduleId") Long moduleId,
                                                                    @Param("componentId") Long componentId,
                                                                    @Param("fieldCode") String fieldCode);

    /**
     * 新增模块字段
     *
     * @param entity 字段实体
     * @return 影响行数
     */
    int insertField(SysModuleFieldEntity entity);

    /**
     * 修改模块字段
     *
     * @param entity 字段实体
     * @return 影响行数
     */
    int updateField(SysModuleFieldEntity entity);

    /**
     * 根据字段 ID 删除模块字段
     *
     * @param id 字段 ID
     * @return 影响行数
     */
    int deleteFieldById(@Param("id") Long id);

    /**
     * 统计模块下字段数量
     *
     * @param moduleId 模块 ID
     * @return 字段数量
     */
    int countFieldByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 统计组件下字段数量
     *
     * @param componentId 组件 ID
     * @return 字段数量
     */
    int countFieldByComponentId(@Param("componentId") Long componentId);

    /**
     * 根据模块 ID 查询按钮列表
     *
     * @param moduleId 模块 ID
     * @return 按钮列表
     */
    List<SysModuleButtonEntity> selectButtonsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据按钮 ID 查询按钮
     *
     * @param id 按钮 ID
     * @return 按钮实体
     */
    SysModuleButtonEntity selectButtonById(@Param("id") Long id);

    /**
     * 根据模块 ID 和按钮编码查询按钮
     *
     * @param moduleId 模块 ID
     * @param buttonCode 按钮编码
     * @return 按钮实体
     */
    SysModuleButtonEntity selectButtonByModuleIdAndCode(@Param("moduleId") Long moduleId, @Param("buttonCode") String buttonCode);

    /**
     * 新增模块按钮
     *
     * @param entity 按钮实体
     * @return 影响行数
     */
    int insertButton(SysModuleButtonEntity entity);

    /**
     * 修改模块按钮
     *
     * @param entity 按钮实体
     * @return 影响行数
     */
    int updateButton(SysModuleButtonEntity entity);

    /**
     * 根据按钮 ID 删除模块按钮
     *
     * @param id 按钮 ID
     * @return 影响行数
     */
    int deleteButtonById(@Param("id") Long id);

    /**
     * 统计模块下按钮数量
     *
     * @param moduleId 模块 ID
     * @return 按钮数量
     */
    int countButtonByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 查询状态列表
     *
     * @param moduleId 模块 ID
     * @return 状态列表
     */
    List<SysModuleStateEntity> selectStatesByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 查询状态流转列表
     *
     * @param moduleId 模块 ID
     * @return 状态流转列表
     */
    List<SysModuleTransitionEntity> selectTransitionsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 删除状态流转
     *
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteTransitionsByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 删除状态定义
     *
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteStatesByModuleId(@Param("moduleId") Long moduleId);

}
