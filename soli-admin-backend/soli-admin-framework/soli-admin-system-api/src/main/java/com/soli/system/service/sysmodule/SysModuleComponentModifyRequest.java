package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Modify module component request.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentModifyRequest {

    @NotNull(message = "id cannot be null")
    private Long id;

    @NotNull(message = "moduleId cannot be null")
    private Long moduleId;

    @NotBlank(message = "componentCode cannot be blank")
    private String componentCode;

    @NotBlank(message = "componentName cannot be blank")
    private String componentName;

    private Integer sort;

    private String status;

    private String note;

}
