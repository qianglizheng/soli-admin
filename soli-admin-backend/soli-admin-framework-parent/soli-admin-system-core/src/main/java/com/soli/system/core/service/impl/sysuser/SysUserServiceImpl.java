package com.soli.system.core.service.impl.sysuser;

import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysUserMapper;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserService;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-08 0:44
*/
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper mapper;

    private final SysUserConverter converter;

    @Override
    public SysUserDTO getById(Long id) {
        SysUserEntity entity = mapper.selectById(id);
        return converter.toDTO(entity);
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = mapper.selectByUsername(username);
        return converter.toDTO(entity);
    }

}