package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块详情 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleDetailDTO extends SysModuleDTO {

    /**
     * 组件列表
     */
    private List<SysModuleComponentDetailDTO> components;

    /**
     * 按钮列表
     */
    private List<SysModuleButtonDTO> buttons;

    /**
     * 状态列表
     */
    private List<SysModuleStateDTO> states;

    /**
     * 状态流转列表
     */
    private List<SysModuleTransitionDTO> transitions;

}
