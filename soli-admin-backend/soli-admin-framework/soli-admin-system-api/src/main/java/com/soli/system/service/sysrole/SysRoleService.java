package com.soli.system.service.sysrole;

import com.soli.system.service.BaseCrudService;

import java.util.List;

/**
 * 系统角色服务
 *
 * @author lizhengqiang
 * @since 2026-03-14 15:32
*/
public interface SysRoleService extends BaseCrudService<SysRoleDTO, SysRoleQuery> {

    /**
     * 根据系统用户 Id 查询角色
     *
     * @param userId 系统用户 Id
     * @return 角色
     */
    List<SysRoleDTO> getByUserId(Long userId);

}