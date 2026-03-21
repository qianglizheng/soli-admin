package com.soli.system.core.service.impl.sysuser;

import com.soli.common.api.exception.BusinessException;
import com.soli.system.core.mapper.SysUserMapper;
import com.soli.system.core.service.impl.BaseCrudServiceImpl;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserQuery;
import com.soli.system.service.sysuser.SysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    protected void beforeCreate(SysUserDTO dto) {
        if (getByUsername(dto.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        dto.setCreateTime(LocalDateTime.now());
    }

    @Override
    protected void afterCreate(SysUserDTO dto, SysUserEntity entity) {
        saveUserRoles(entity.getId(), dto.getRoleIds());
    }

    @Override
    protected void beforeModify(SysUserDTO dto) {
        dto.setUpdateTime(LocalDateTime.now());
    }

    @Override
    protected void afterModify(SysUserDTO dto, SysUserEntity entity) {
        if (dto.getRoleIds() != null) {
            saveUserRoles(entity.getId(), dto.getRoleIds());
        }
    }

    @Override
    protected void beforeRemove(Long id) {
        sysUserMapper.deleteUserRoleByUserId(id);
    }

    @Override
    public Optional<SysUserDTO> getById(Long id) {
        Optional<SysUserDTO> userOptional = super.getById(id);
        userOptional.ifPresent(user -> user.setRoleIds(sysUserMapper.selectRoleIdsByUserId(id)));
        return userOptional;
    }

    @Override
    public SysUserDTO getByUsername(String username) {
        SysUserEntity entity = sysUserMapper.selectByUsername(username);
        return converter.toDTO(entity);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        sysUserMapper.deleteUserRoleByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        List<Long> distinctRoleIds = roleIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (distinctRoleIds.isEmpty()) {
            return;
        }
        sysUserMapper.insertUserRoleRelations(userId, distinctRoleIds);
    }

}