package com.soli.system.core.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.soli.system.core.entity.sysuser.SysUserEntity;

/**
 * 系统用户持久层
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:29
*/
@Mapper
public interface SysUserMapper {

    /**
     * 根据用户 ID 查询用户
     *
     * @param id 用户 ID
     * @return 用户实体，如果不存在返回 null
     */
    SysUserEntity selectById(@Param("id") Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户实体，如果不存在返回 null
     */
    SysUserEntity selectByUsername(@Param("username") String username);

}