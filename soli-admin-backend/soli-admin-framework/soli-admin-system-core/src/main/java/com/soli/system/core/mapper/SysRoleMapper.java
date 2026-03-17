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

    /**
     * 根据 id 查询角色信息
     * @param id 角色 id
     * @return 角色信息
     */
    SysRoleEntity selectById(Long id);

    /**
     * 插入一条角色信息
     *
     * @param entity 角色实体
     * @return 影响的行数
     */
    int insert(SysRoleEntity entity);

    /**
     * 更新角色信息
     *
     * @param entity 角色实体
     * @return 影响的行数
     */
    int update(SysRoleEntity entity);

    /**
     * 根据 id 删除一条角色信息
     * @param id 角色 id
     * @return 影响的行数
     */
    int deleteById(Long id);
}