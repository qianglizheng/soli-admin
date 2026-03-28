package com.soli.system.core.service.impl.sysmodule;

import com.soli.system.core.service.Converter;
import com.soli.system.service.sysmodule.SysModuleButtonCreateRequest;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleButtonModifyRequest;
import com.soli.system.service.sysmodule.SysModuleComponentCreateRequest;
import com.soli.system.service.sysmodule.SysModuleComponentDTO;
import com.soli.system.service.sysmodule.SysModuleComponentModifyRequest;
import com.soli.system.service.sysmodule.SysModuleCreateRequest;
import com.soli.system.service.sysmodule.SysModuleDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldCreateRequest;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleFieldModifyRequest;
import com.soli.system.service.sysmodule.SysModuleModifyRequest;
import com.soli.system.service.sysmodule.SysModuleStateDTO;
import com.soli.system.service.sysmodule.SysModuleTransitionDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Module converter.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:56
 */
@Mapper(componentModel = "spring")
public interface SysModuleConverter extends Converter<SysModuleDTO, SysModuleEntity> {

    SysModuleDTO toDTO(SysModuleEntity entity);

    SysModuleDetailDTO toDetailDTO(SysModuleEntity entity);

    SysModuleDTO toDTO(SysModuleCreateRequest createRequest);

    SysModuleDTO toDTO(SysModuleModifyRequest modifyRequest);

    SysModuleEntity toEntity(SysModuleDTO dto);

    List<SysModuleDTO> toDTOList(List<SysModuleEntity> entityList);

    List<SysModuleEntity> toEntityList(List<SysModuleDTO> dtoList);

    SysModuleTreeNodeDTO toTreeNodeDTO(SysModuleTreeNodeModel model);

    List<SysModuleTreeNodeDTO> toTreeNodeDTOList(List<SysModuleTreeNodeModel> modelList);

    SysModuleComponentDTO toComponentDTO(SysModuleComponentEntity entity);

    List<SysModuleComponentDTO> toComponentDTOList(List<SysModuleComponentEntity> entityList);

    SysModuleComponentDTO toDTO(SysModuleComponentCreateRequest createRequest);

    SysModuleComponentDTO toDTO(SysModuleComponentModifyRequest modifyRequest);

    SysModuleComponentEntity toComponentEntity(SysModuleComponentDTO dto);

    SysModuleFieldDTO toFieldDTO(SysModuleFieldEntity entity);

    List<SysModuleFieldDTO> toFieldDTOList(List<SysModuleFieldEntity> entityList);

    SysModuleFieldDTO toDTO(SysModuleFieldCreateRequest createRequest);

    SysModuleFieldDTO toDTO(SysModuleFieldModifyRequest modifyRequest);

    SysModuleFieldEntity toFieldEntity(SysModuleFieldDTO dto);

    SysModuleButtonDTO toButtonDTO(SysModuleButtonEntity entity);

    List<SysModuleButtonDTO> toButtonDTOList(List<SysModuleButtonEntity> entityList);

    SysModuleButtonDTO toDTO(SysModuleButtonCreateRequest createRequest);

    SysModuleButtonDTO toDTO(SysModuleButtonModifyRequest modifyRequest);

    SysModuleButtonEntity toButtonEntity(SysModuleButtonDTO dto);

    SysModuleStateDTO toStateDTO(SysModuleStateEntity entity);

    List<SysModuleStateDTO> toStateDTOList(List<SysModuleStateEntity> entityList);

    SysModuleTransitionDTO toTransitionDTO(SysModuleTransitionEntity entity);

    List<SysModuleTransitionDTO> toTransitionDTOList(List<SysModuleTransitionEntity> entityList);

}
