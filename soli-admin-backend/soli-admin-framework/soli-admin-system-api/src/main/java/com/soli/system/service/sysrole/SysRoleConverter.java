package com.soli.system.service.sysrole;

import java.util.List;

import org.mapstruct.Mapper;

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