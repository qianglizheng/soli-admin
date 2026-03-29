package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块组件详情 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentDetailDTO {

    /**
     * 组件信息
     */
    private SysModuleComponentDTO componentInfo;

    /**
     * 字段列表
     */
    private List<SysModuleFieldDTO> fields;

}
