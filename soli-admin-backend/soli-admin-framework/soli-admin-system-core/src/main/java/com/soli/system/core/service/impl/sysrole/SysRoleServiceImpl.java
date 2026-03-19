package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysrole.SysRoleQuery;
import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysRoleMapper;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleService;

/**
 * @author lizhengqiang
 * @since 2026-03-14 15:59
*/
@Service
public class SysRoleServiceImpl extends BaseCrudServiceImpl<SysRoleDTO, SysRoleEntity, SysRoleQuery>
        implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    public SysRoleServiceImpl(final SysRoleMapper mapper, final SysRoleConverter converter) {
        super(mapper, converter);
        this.sysRoleMapper = mapper;
    }

    @Override
    public List<SysRoleDTO> getByUserId(Long userId) {
        List<SysRoleEntity> sysRoleEntities = sysRoleMapper.selectByUserId(userId);
        return converter.toDTOList(sysRoleEntities);
    }

    @Override
    protected String moduleName() {
        return "系统用户";
    }

}