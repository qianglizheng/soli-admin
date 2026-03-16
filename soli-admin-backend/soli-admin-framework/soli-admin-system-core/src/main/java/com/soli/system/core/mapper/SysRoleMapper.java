package com.soli.system.core.mapper;

import java.util.List;

import com.soli.system.core.service.impl.sysrole.SysRoleEntity;

/**
 * @author lizhengqiang
 * @since 2026-03-14 16:07
 */
public interface SysRoleMapper {

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRoleEntity> selectAll();

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleEntity> selectByUserId(Long userId);

}