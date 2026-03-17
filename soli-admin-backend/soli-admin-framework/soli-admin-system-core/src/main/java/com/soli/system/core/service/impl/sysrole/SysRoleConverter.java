package com.soli.system.core.service.impl.sysrole;

import java.util.List;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysrole.SysRoleCreateRequest;
import com.soli.system.service.sysrole.SysRoleModifyRequest;
import org.mapstruct.Mapper;

import com.soli.system.service.sysrole.SysRoleDTO;

/**
 * 角色对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-14 16:00
 */
@Mapper(componentModel = "spring")
public interface SysRoleConverter extends Converter<SysRoleDTO, SysRoleEntity> {

    SysRoleDTO toDTO(SysRoleEntity entity);

    SysRoleDTO toDTO(SysRoleCreateRequest createRequest);

    SysRoleDTO toDTO(SysRoleModifyRequest modifyRequest);

    SysRoleEntity toEntity(SysRoleDTO dto);

    List<SysRoleDTO> toDTOList(List<SysRoleEntity> entityList);

    List<SysRoleEntity> toEntityList(List<SysRoleDTO> dtoList);

}