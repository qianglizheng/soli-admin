package com.soli.system.core.mapper;

import java.util.List;

import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysrole.SysRoleEntity;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleQuery;

/**
 * @author lizhengqiang
 * @since 2026-03-14 16:07
 */
public interface SysRoleMapper {

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleEntity> selectByUserId(Long userId);

    /**
     * 分页查询角色信息
     *
     * @param query 查询参数
     * @return 分页角色信息
     */
    List<SysRoleEntity> select(SysRoleQuery query);
}