package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块详情对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleDetailDTO extends SysModuleDTO {

    /** 单头 Tab 列表 */
    private List<SysModuleTabDetailDTO> headerTabs;

    /** 明细 Tab 列表 */
    private List<SysModuleTabDetailDTO> detailTabs;

    /** 按钮列表 */
    private List<SysModuleButtonDTO> buttons;

}
