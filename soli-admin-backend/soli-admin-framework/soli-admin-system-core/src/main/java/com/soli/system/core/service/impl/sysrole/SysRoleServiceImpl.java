package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysRoleMapper;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleService;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 15:59
*/
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    private final SysRoleConverter sysRoleConverter;

    @Override
    public List<SysRoleDTO> queryAll() {
        return List.of();
    }

    @Override
    public List<SysRoleDTO> getByUserId(Long userId) {
        List<SysRoleEntity> sysRoleEntities = sysRoleMapper.selectByUserId(userId);
        return sysRoleConverter.toDTOList(sysRoleEntities);
    }

}