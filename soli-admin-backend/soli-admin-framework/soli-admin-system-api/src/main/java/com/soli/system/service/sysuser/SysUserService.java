package com.soli.system.service.sysuser;

import com.soli.system.service.BaseCrudService;

/**
 * 用户管理服务接口
 *
 * @author lizhengqiang
 * @since 2026-03-08 16:04
 */
public interface SysUserService extends BaseCrudService<SysUserDTO, SysUserQuery> {

    /**
     * 根据用户名查询系统用户
     *
     * @param username 用户名
     * @return 系统用户
     */
    SysUserDTO getByUsername(String username);

}
