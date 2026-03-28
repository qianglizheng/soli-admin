package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Create module field request.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleFieldCreateRequest {

    @NotNull(message = "moduleId cannot be null")
    private Long moduleId;

    @NotNull(message = "componentId cannot be null")
    private Long componentId;

    @NotBlank(message = "fieldCode cannot be blank")
    private String fieldCode;

    @NotBlank(message = "defaultTitle cannot be blank")
    private String defaultTitle;

    @NotBlank(message = "componentType cannot be blank")
    private String componentType;

    @NotBlank(message = "dataPath cannot be blank")
    private String dataPath;

    @NotBlank(message = "valueType cannot be blank")
    private String valueType;

    private String requiredFlag;

    private Integer sort;

    private String status;

    private String note;

}
