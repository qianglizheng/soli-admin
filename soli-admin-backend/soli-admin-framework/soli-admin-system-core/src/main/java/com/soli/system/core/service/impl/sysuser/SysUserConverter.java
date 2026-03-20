package com.soli.system.core.service.impl.sysuser;

import java.util.List;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysuser.SysUserCreateRequest;
import com.soli.system.service.sysuser.SysUserModifyRequest;
import com.soli.system.service.sysuser.SysUserQuery;
import org.mapstruct.Mapper;

import com.soli.system.service.sysuser.SysUserDTO;

/**
 * 用户对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-08 21:06
 */
@Mapper(componentModel = "spring")
public interface SysUserConverter extends Converter<SysUserDTO, SysUserEntity> {

    SysUserDTO toDTO(SysUserEntity entity);

    SysUserDTO toDTO(SysUserModifyRequest modifyRequest);

    SysUserDTO toDTO(SysUserCreateRequest createRequest);

    SysUserEntity toEntity(SysUserDTO dto);

    List<SysUserDTO> toDTOList(List<SysUserEntity> entityList);

    List<SysUserEntity> toEntityList(List<SysUserDTO> dtoList);

}