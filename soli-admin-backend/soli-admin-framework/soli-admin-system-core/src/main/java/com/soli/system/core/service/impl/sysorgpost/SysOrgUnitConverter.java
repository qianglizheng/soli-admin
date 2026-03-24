package com.soli.system.core.service.impl.sysorgpost;

import com.soli.system.service.sysorgpost.SysOrgUnitCreateRequest;
import com.soli.system.service.sysorgpost.SysOrgUnitDTO;
import org.mapstruct.Mapper;

/**
 * 组织单元对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-24 23:20
 */
@Mapper(componentModel = "spring")
public interface SysOrgUnitConverter {

    SysOrgUnitDTO toDTO(SysOrgUnitCreateRequest createRequest);

    SysOrgUnitEntity toEntity(SysOrgUnitDTO dto);

}
