package com.soli.system.core.mapper;

import java.util.List;

import com.soli.system.core.service.impl.sysrole.SysRoleEntity;
import com.soli.system.service.sysrole.SysRoleQuery;

/**
 * @author lizhengqiang
 * @since 2026-03-14 16:07
 */
public interface SysRoleMapper extends BaseCrudMapper<SysRoleEntity, SysRoleQuery> {

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleEntity> selectByUserId(Long userId);

    /**
     * 查询所有状态正常的角色
     *
     * @return 角色集合
     */
    List<SysRoleEntity> selectAllEnabled();

}