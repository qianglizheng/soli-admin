package com.soli.auth.core.service.impl;

import org.springframework.stereotype.Service;

import com.soli.auth.api.service.AuthService;
import com.soli.auth.api.vo.TokenVO;
import com.soli.system.dto.SysUserDTO;
import com.soli.system.service.SysUserService;

import lombok.AllArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-08 15:59
*/
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService sysUserService;

    @Override
    public TokenVO loginByUsernameAndPassword(String username, String password) {
        SysUserDTO byId = sysUserService.getById(1L);
        return null;
    }
}
