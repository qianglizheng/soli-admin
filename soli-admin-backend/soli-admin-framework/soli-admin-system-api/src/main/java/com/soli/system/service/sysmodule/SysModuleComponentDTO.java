package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Module component DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentDTO extends BaseDTO {

    private Long moduleId;

    private String componentCode;

    private String componentName;

    private Integer sort;

    private String status;

}
