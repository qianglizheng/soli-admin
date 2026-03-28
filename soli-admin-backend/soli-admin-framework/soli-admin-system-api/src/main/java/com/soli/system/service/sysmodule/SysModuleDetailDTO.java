package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Module detail DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleDetailDTO extends SysModuleDTO {

    private List<SysModuleComponentDetailDTO> components;

    private List<SysModuleButtonDTO> buttons;

    private List<SysModuleStateDTO> states;

    private List<SysModuleTransitionDTO> transitions;

}
