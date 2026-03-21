package com.soli.system.core.service.impl.sysrole;

import java.time.LocalDateTime;
import java.util.List;

import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysrole.SysRoleQuery;
import org.apache.commons.lang3.StringUtils;
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
    public void modify(SysRoleDTO dto) {
        dto.setUpdateTime(LocalDateTime.now());
        super.modify(dto);
    }

    @Override
    protected String moduleName() {
        return "角色管理";
    }

}