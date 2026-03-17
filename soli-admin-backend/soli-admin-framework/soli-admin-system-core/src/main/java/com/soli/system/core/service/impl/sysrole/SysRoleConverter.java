package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import com.soli.system.service.sysrole.SysRoleCreateRequest;
import com.soli.system.service.sysrole.SysRoleModifyRequest;
import org.mapstruct.Mapper;

import com.soli.system.service.sysrole.SysRoleDTO;

/**
 * @author lizhengqiang
 * @since 2026-03-14 16:00
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter {

    SysRoleDTO toDTO(SysRoleEntity entity);

    SysRoleDTO toDTO(SysRoleCreateRequest createRequest);

    SysRoleDTO toDTO(SysRoleModifyRequest modifyRequest);

    SysRoleEntity toEntity(SysRoleDTO dto);

    List<SysRoleDTO> toDTOList(List<SysRoleEntity> entityList);

    List<SysRoleEntity> toEntityList(List<SysRoleDTO> dtoList);

}