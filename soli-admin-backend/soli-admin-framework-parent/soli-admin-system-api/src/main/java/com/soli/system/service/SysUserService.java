package com.soli.system.service;


import com.soli.system.dto.SysUserDTO;

/**
 * @author lizhengqiang
 * @since 2026-03-08 16:04
 */
public interface SysUserService {

    /**
     * 根据 id 查询系统用户
     * @param id 用户 id
     * @return 系统用户
     */
    SysUserDTO getById(Long id);

    /**
     * 根据用户名查询系统用户
     * @param username 用户名
     * @return 系统用户
     */
    SysUserDTO getByUsername(String username);

}
