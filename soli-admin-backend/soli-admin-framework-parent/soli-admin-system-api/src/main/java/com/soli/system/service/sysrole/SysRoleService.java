package com.soli.system.service.sysrole;

import java.util.List;

/**
 * 系统角色服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 15:32
*/
public interface SysRoleService {

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<SysRoleDTO> queryAll();

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleDTO> getByUserId(Long userId);

}