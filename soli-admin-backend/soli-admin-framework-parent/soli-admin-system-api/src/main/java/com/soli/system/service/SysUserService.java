package com.soli.system.service;


import com.soli.system.dto.SysUserDTO;

/**
 * @author lizhengqiang
 * @since 2026-03-08 16:04
 */
public interface SysUserService {

    SysUserDTO getById(Long id);

    SysUserDTO getByUsername(String username);

}
