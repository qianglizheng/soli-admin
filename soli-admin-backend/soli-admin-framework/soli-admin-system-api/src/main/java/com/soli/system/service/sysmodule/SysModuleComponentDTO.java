package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块组件 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentDTO extends BaseDTO {

    /**
     * 模块 ID
     */
    private Long moduleId;

    /**
     * 组件编码
     */
    private String componentCode;

    /**
     * 组件名称
     */
    private String componentName;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private NormalDisableStatusEnum status;

}
