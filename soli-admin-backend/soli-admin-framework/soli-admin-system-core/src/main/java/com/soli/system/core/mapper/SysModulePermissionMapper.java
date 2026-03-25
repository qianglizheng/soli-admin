package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysModuleStateFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostButtonAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostFieldAuthEntity;
import com.soli.system.core.service.impl.sysmodulepermission.SysOrgPostModuleAuthEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模块权限平台持久层
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:10
 */
public interface SysModulePermissionMapper {

    List<Long> selectAdminOrgPostIdList();

    List<Long> selectUserVisibleNavModuleIdList(@Param("userId") Long userId);

    int countUserVisibleModule(@Param("userId") Long userId, @Param("moduleCode") String moduleCode);

    Integer selectUserButtonPermissionLevel(@Param("userId") Long userId,
                                            @Param("moduleCode") String moduleCode,
                                            @Param("buttonCode") String buttonCode);

    SysOrgPostModuleAuthEntity selectOrgPostModuleAuth(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    int countVisibleModuleByOrgPostId(@Param("orgPostId") Long orgPostId);

    List<SysOrgPostFieldAuthEntity> selectOrgPostFieldAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    List<SysOrgPostButtonAuthEntity> selectOrgPostButtonAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    SysOrgPostButtonAuthEntity selectOrgPostButtonAuth(@Param("orgPostId") Long orgPostId, @Param("buttonId") Long buttonId);

    int deleteOrgPostModuleAuth(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    int deleteOrgPostModuleAuthByModuleId(@Param("moduleId") Long moduleId);

    int deleteOrgPostFieldAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    int deleteOrgPostFieldAuthByFieldId(@Param("fieldId") Long fieldId);

    int deleteOrgPostButtonAuthList(@Param("orgPostId") Long orgPostId, @Param("moduleId") Long moduleId);

    int deleteOrgPostButtonAuthByButtonId(@Param("buttonId") Long buttonId);

    int insertOrgPostModuleAuth(SysOrgPostModuleAuthEntity entity);

    int insertOrgPostFieldAuthBatch(@Param("list") List<SysOrgPostFieldAuthEntity> entityList);

    int insertOrgPostButtonAuthBatch(@Param("list") List<SysOrgPostButtonAuthEntity> entityList);

    List<SysModuleStateFieldAuthEntity> selectStateFieldAuthList(@Param("moduleId") Long moduleId);

    List<SysModuleStateButtonAuthEntity> selectStateButtonAuthList(@Param("moduleId") Long moduleId);

    int deleteStateFieldAuthList(@Param("moduleId") Long moduleId);

    int deleteStateFieldAuthByFieldId(@Param("fieldId") Long fieldId);

    int deleteStateButtonAuthList(@Param("moduleId") Long moduleId);

    int deleteStateButtonAuthByButtonId(@Param("buttonId") Long buttonId);

    int insertStateFieldAuthBatch(@Param("list") List<SysModuleStateFieldAuthEntity> entityList);

    int insertStateButtonAuthBatch(@Param("list") List<SysModuleStateButtonAuthEntity> entityList);

}
