package com.soli.system.core.service.impl.sysorgpost;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysorgpost.SysOrgPostCreateRequest;
import com.soli.system.service.sysorgpost.SysOrgPostDetailDTO;
import com.soli.system.service.sysorgpost.SysOrgPostDTO;
import com.soli.system.service.sysorgpost.SysOrgPostModifyRequest;
import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
import com.soli.system.service.sysorgpost.SysOrgPostUserDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 岗位对象转换器
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Mapper(componentModel = "spring")
public interface SysOrgPostConverter extends Converter<SysOrgPostDTO, SysOrgPostEntity> {

    SysOrgPostDTO toDTO(SysOrgPostEntity entity);

    SysOrgPostDTO toDTO(SysOrgPostCreateRequest createRequest);

    SysOrgPostDTO toDTO(SysOrgPostModifyRequest modifyRequest);

    SysOrgPostEntity toEntity(SysOrgPostDTO dto);

    List<SysOrgPostDTO> toDTOList(List<SysOrgPostEntity> entityList);

    List<SysOrgPostEntity> toEntityList(List<SysOrgPostDTO> dtoList);

    SysOrgPostTreeNodeDTO toTreeNodeDTO(SysOrgPostTreeNodeModel model);

    List<SysOrgPostTreeNodeDTO> toTreeNodeDTOList(List<SysOrgPostTreeNodeModel> modelList);

    SysOrgPostDetailDTO toDetailDTO(SysOrgPostDetailModel model);

    SysOrgPostUserDTO toUserDTO(SysOrgPostUserModel model);

    List<SysOrgPostUserDTO> toUserDTOList(List<SysOrgPostUserModel> modelList);

}
