package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Module component detail DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentDetailDTO {

    private SysModuleComponentDTO componentInfo;

    private List<SysModuleFieldDTO> fields;

}
