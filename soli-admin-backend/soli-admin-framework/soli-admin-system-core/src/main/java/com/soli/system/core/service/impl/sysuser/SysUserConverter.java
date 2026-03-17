package com.soli.system.core.service.impl.sysuser;

import java.util.List;

import org.mapstruct.Mapper;

import com.soli.system.service.sysuser.SysUserDTO;

/**
 * 用户对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-08 21:06
 */
@Mapper(componentModel = "spring")
public interface SysUserConverter {

    SysUserDTO toDTO(SysUserEntity entity);

    SysUserEntity toEntity(SysUserDTO dto);

    List<SysUserDTO> toDTOList(List<SysUserEntity> entityList);

    List<SysUserEntity> toEntityList(List<SysUserDTO> dtoList);

}