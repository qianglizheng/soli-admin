package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysuser.SysUserEntity;
import com.soli.system.service.sysuser.SysUserQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户持久层
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:29
 */
public interface SysUserMapper extends BaseCrudMapper<SysUserEntity, SysUserQuery> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，如不存在返回 null
     */
    SysUserEntity selectByUsername(@Param("username") String username);

    /**
     * 根据用户 ID 查询已关联角色 ID
     *
     * @param userId 用户 ID
     * @return 角色 ID 列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 删除用户角色关联
     *
     * @param userId 用户 ID
     * @return 影响行数
     */
    int deleteUserRoleByUserId(@Param("userId") Long userId);

    /**
     * 批量新增用户角色关联
     *
     * @param userId 用户 ID
     * @param roleIds 角色 ID 列表
     * @return 影响行数
     */
    int insertUserRoleRelations(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
}