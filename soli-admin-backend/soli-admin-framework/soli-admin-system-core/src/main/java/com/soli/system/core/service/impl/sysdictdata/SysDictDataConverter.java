package com.soli.system.core.service.impl.sysdictdata;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysdictdata.SysDictDataCreateRequest;
import com.soli.system.service.sysdictdata.SysDictDataDTO;
import com.soli.system.service.sysdictdata.SysDictDataModifyRequest;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 字典数据对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:47
 */
@Mapper(componentModel = "spring")
public interface SysDictDataConverter extends Converter<SysDictDataDTO, SysDictDataEntity> {

    SysDictDataDTO toDTO(SysDictDataEntity entity);

    SysDictDataDTO toDTO(SysDictDataCreateRequest createRequest);

    SysDictDataDTO toDTO(SysDictDataModifyRequest modifyRequest);

    SysDictDataEntity toEntity(SysDictDataDTO dto);

    List<SysDictDataDTO> toDTOList(List<SysDictDataEntity> entityList);

    List<SysDictDataEntity> toEntityList(List<SysDictDataDTO> dtoList);

}