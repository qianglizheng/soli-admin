package com.soli.system.core.service.impl.sysuser;

import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysuser.SysUserQuery;
import org.springframework.stereotype.Service;

import com.soli.system.core.mapper.SysUserMapper;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserService;

/**
 * 用户管理服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:44
*/
@Service
public class SysUserServiceImpl extends BaseCrudServiceImpl<SysUserDTO, SysUserEntity, SysUserQuery> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    public SysUserServiceImpl(SysUserMapper mapper, SysUserConverter converter) {
        super(mapper, converter);
        this.sysUserMapper = mapper;
    }

    @Override
    protected String moduleName() {
        return "用户管理";
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = sysUserMapper.selectByUsername(username);
        return converter.toDTO(entity);
    }

}