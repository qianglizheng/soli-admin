package com.soli.system.core.service.impl.sysdict;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysdict.SysDictCreateRequest;
import com.soli.system.service.sysdict.SysDictDTO;
import com.soli.system.service.sysdict.SysDictModifyRequest;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典类型对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:57
 */
@Mapper(componentModel = "spring")
public interface SysDictConverter extends Converter<SysDictDTO, SysDictEntity> {

    SysDictDTO toDTO(SysDictEntity entity);

    SysDictDTO toDTO(SysDictCreateRequest createRequest);

    SysDictDTO toDTO(SysDictModifyRequest modifyRequest);

    SysDictEntity toEntity(SysDictDTO dto);

    List<SysDictDTO> toDTOList(List<SysDictEntity> entityList);

    List<SysDictEntity> toEntityList(List<SysDictDTO> dtoList);

}