package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块 Tab 明细对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleTabDetailDTO {

    /** Tab 信息 */
    private SysModuleTabDTO tabInfo;

    /** 字段列表 */
    private List<SysModuleFieldDTO> fields;

}
