package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * Module field DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleFieldDTO extends BaseDTO {

    private Long moduleId;

    private Long componentId;

    private String componentCode;

    private String fieldCode;

    private String defaultTitle;

    private String displayTitle;

    private String placeholder;

    private String helpText;

    private String componentType;

    private String dataPath;

    private String valueType;

    private String requiredFlag;

    private Integer sort;

    private String status;

}
