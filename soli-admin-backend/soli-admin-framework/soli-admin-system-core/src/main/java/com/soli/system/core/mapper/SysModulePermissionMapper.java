package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostModuleAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleButtonPermissionModel;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleFieldPermissionModel;
import com.soli.system.core.service.impl.sysmodulepermission.SysUserModuleNavPermissionModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块权限平台持久层
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:10
 */
public interface SysModulePermissionMapper {

    /**
     * 查询管理员岗位 ID 列表
     *
     * @return 岗位 ID 列表
     */
    List<Long> selectAdminOrgPostIdList();

    /**
     * 查询用户可见导航模块 ID 列表
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 模块 ID 列表
     */
    List<Long> selectUserVisibleNavModuleIdList(@Param("userId") Long userId, @Param("companyId") Long companyId);

    /**
     * 查询用户导航权限列表
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 导航权限列表
     */
    List<SysUserModuleNavPermissionModel> selectUserNavPermissionList(@Param("userId") Long userId, @Param("companyId") Long companyId);

    /**
     * 统计用户可见模块数量
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @param moduleCode 模块编码
     * @return 可见模块数量
     */
    int countUserVisibleModule(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("moduleCode") String moduleCode);

    /**
     * 查询用户按钮权限级别
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @param moduleCode 模块编码
     * @param buttonCode 按钮编码
     * @return 权限级别
     */
    Integer selectUserButtonPermissionLevel(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("moduleCode") String moduleCode, @Param("buttonCode") String buttonCode);

    /**
     * 查询用户字段权限列表
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @param moduleId 模块 ID
     * @return 字段权限列表
     */
    List<SysUserModuleFieldPermissionModel> selectUserFieldPermissionList(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("moduleId") Long moduleId);

    /**
     * 查询用户按钮权限列表
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @param moduleId 模块 ID
     * @return 按钮权限列表
     */
    List<SysUserModuleButtonPermissionModel> selectUserButtonPermissionList(@Param("userId") Long userId, @Param("companyId") Long companyId, @Param("moduleId") Long moduleId);

    /**
     * 查询岗位模块权限
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 岗位模块权限
     */
    SysOrgPostModuleAuthEntity selectOrgPostModuleAuth(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 统计岗位可见模块数量
     *
     * @param orgPostId 岗位 ID
     * @return 可见模块数量
     */
    int countVisibleModuleByOrgPostId(@Param("orgPostId") Long orgPostId);

    /**
     * 查询岗位字段权限列表
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 字段权限列表
     */
    List<SysOrgPostFieldAuthEntity> selectOrgPostFieldAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 查询岗位按钮权限列表
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 按钮权限列表
     */
    List<SysOrgPostButtonAuthEntity> selectOrgPostButtonAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 查询岗位按钮权限
     *
     * @param orgPostId 岗位 ID
     * @param buttonId 按钮 ID
     * @return 岗位按钮权限
     */
    SysOrgPostButtonAuthEntity selectOrgPostButtonAuth(@Param("orgPostId") Long orgPostId, @Param("buttonId") Long buttonId);

    /**
     * 删除岗位模块权限
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteOrgPostModuleAuth(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 根据模块 ID 删除岗位模块权限
     *
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteOrgPostModuleAuthByModuleId(@Param("moduleId") Long moduleId);

    /**
     * 删除岗位字段权限列表
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteOrgPostFieldAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 根据字段 ID 删除岗位字段权限
     *
     * @param fieldId 字段 ID
     * @return 影响行数
     */
    int deleteOrgPostFieldAuthByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 删除岗位按钮权限列表
     *
     * @param orgPostId 岗位 ID
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteOrgPostButtonAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    /**
     * 根据按钮 ID 删除岗位按钮权限
     *
     * @param buttonId 按钮 ID
     * @return 影响行数
     */
    int deleteOrgPostButtonAuthByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 新增岗位模块权限
     *
     * @param entity 岗位模块权限实体
     * @return 影响行数
     */
    int insertOrgPostModuleAuth(SysOrgPostModuleAuthEntity entity);

    /**
     * 批量新增岗位字段权限
     *
     * @param entityList 岗位字段权限列表
     * @return 影响行数
     */
    int insertOrgPostFieldAuthBatch(@Param("list") List<SysOrgPostFieldAuthEntity> entityList);

    /**
     * 批量新增岗位按钮权限
     *
     * @param entityList 岗位按钮权限列表
     * @return 影响行数
     */
    int insertOrgPostButtonAuthBatch(@Param("list") List<SysOrgPostButtonAuthEntity> entityList);

    /**
     * 查询状态字段权限列表
     *
     * @param moduleId 模块 ID
     * @return 状态字段权限列表
     */
    List<SysModuleStateFieldAuthEntity> selectStateFieldAuthList(@Param("moduleId") Long moduleId);

    /**
     * 查询状态按钮权限列表
     *
     * @param moduleId 模块 ID
     * @return 状态按钮权限列表
     */
    List<SysModuleStateButtonAuthEntity> selectStateButtonAuthList(@Param("moduleId") Long moduleId);

    /**
     * 删除状态字段权限列表
     *
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteStateFieldAuthList(@Param("moduleId") Long moduleId);

    /**
     * 根据字段 ID 删除状态字段权限
     *
     * @param fieldId 字段 ID
     * @return 影响行数
     */
    int deleteStateFieldAuthByFieldId(@Param("fieldId") Long fieldId);

    /**
     * 删除状态按钮权限列表
     *
     * @param moduleId 模块 ID
     * @return 影响行数
     */
    int deleteStateButtonAuthList(@Param("moduleId") Long moduleId);

    /**
     * 根据按钮 ID 删除状态按钮权限
     *
     * @param buttonId 按钮 ID
     * @return 影响行数
     */
    int deleteStateButtonAuthByButtonId(@Param("buttonId") Long buttonId);

    /**
     * 批量新增状态字段权限
     *
     * @param entityList 状态字段权限列表
     * @return 影响行数
     */
    int insertStateFieldAuthBatch(@Param("list") List<SysModuleStateFieldAuthEntity> entityList);

    /**
     * 批量新增状态按钮权限
     *
     * @param entityList 状态按钮权限列表
     * @return 影响行数
     */
    int insertStateButtonAuthBatch(@Param("list") List<SysModuleStateButtonAuthEntity> entityList);

}
