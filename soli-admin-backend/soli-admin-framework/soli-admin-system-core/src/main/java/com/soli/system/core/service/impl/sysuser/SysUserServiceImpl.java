package com.soli.system.core.service.impl.sysuser;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysUserMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserQuery;
import com.soli.system.service.sysuser.SysUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
    public void create(SysUserDTO dto) throws BusinessException {
        if (dto == null) {
            throw new BusinessException("用户信息不能为空");
        }
        if (StringUtils.isBlank(dto.getUsername())) {
            throw new BusinessException("用户名不能为空");
        }
        if (StringUtils.isBlank(dto.getPassword())) {
            throw new BusinessException("密码不能为空");
        }
        if (getByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (StringUtils.isBlank(dto.getType())) {
            dto.setType("1");
        }
        if (StringUtils.isBlank(dto.getSex())) {
            dto.setSex("0");
        }
        if (StringUtils.isBlank(dto.getStatus())) {
            dto.setStatus("0");
        }
        dto.setCreateTime(LocalDateTime.now());
        super.create(dto);
    }

    @Override
    public void modify(SysUserDTO dto) throws BusinessException {
        if (dto == null || dto.getId() == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        dto.setUpdateTime(LocalDateTime.now());
        super.modify(dto);
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = sysUserMapper.selectByUsername(username);
        return converter.toDTO(entity);
    }
}
