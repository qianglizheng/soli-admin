package com.soli.system.core.service.impl.sysmenu;

import java.util.List;

import org.mapstruct.Mapper;

import com.soli.system.service.sysmenu.SysMenuCreateRequest;
import com.soli.system.service.sysmenu.SysMenuDTO;
import com.soli.system.service.sysmenu.SysMenuUpdateRequest;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:53
 */
@Mapper(componentModel = "spring")
public interface SysMenuConverter {

    SysMenuDTO toDTO(SysMenuEntity entity);

    SysMenuDTO toDTO(SysMenuCreateRequest createRequest);

    SysMenuDTO toDTO(SysMenuUpdateRequest updateRequest);

    SysMenuEntity toEntity(SysMenuDTO dto);

    List<SysMenuDTO> toDTOList(List<SysMenuEntity> entityList);

    List<SysMenuEntity> toEntityList(List<SysMenuDTO> dtoList);

}