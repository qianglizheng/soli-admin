package com.soli.system.core.service.impl.sysconfig;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysconfig.SysConfigCreateRequest;
import com.soli.system.service.sysconfig.SysConfigDTO;
import com.soli.system.service.sysconfig.SysConfigModifyRequest;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 参数配置对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:42
 */
@Mapper(componentModel = "spring")
public interface SysConfigConverter extends Converter<SysConfigDTO, SysConfigEntity> {

    SysConfigDTO toDTO(SysConfigEntity entity);

    SysConfigDTO toDTO(SysConfigCreateRequest createRequest);

    SysConfigDTO toDTO(SysConfigModifyRequest modifyRequest);

    SysConfigEntity toEntity(SysConfigDTO dto);

    List<SysConfigDTO> toDTOList(List<SysConfigEntity> entityList);

    List<SysConfigEntity> toEntityList(List<SysConfigDTO> dtoList);

}
