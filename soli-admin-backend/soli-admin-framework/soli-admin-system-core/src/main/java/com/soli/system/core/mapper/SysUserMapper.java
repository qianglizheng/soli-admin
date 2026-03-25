package com.soli.system.core.mapper;

import com.soli.system.core.service.impl.sysuser.SysUserEntity;
import com.soli.system.service.sysuser.SysUserQuery;
import org.apache.ibatis.annotations.Param;

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
}
