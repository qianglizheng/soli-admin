package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增模块字段请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleFieldCreateRequest {

    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    @NotNull(message = "Tab ID 不能为空")
    private Long tabId;

    @NotBlank(message = "字段作用域不能为空")
    private String fieldScope;

    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    @NotBlank(message = "默认标题不能为空")
    private String defaultTitle;

    @NotBlank(message = "组件类型不能为空")
    private String componentType;

    @NotBlank(message = "数据路径不能为空")
    private String dataPath;

    @NotBlank(message = "值类型不能为空")
    private String valueType;

    private String requiredFlag;

    private Integer sort;

    private String status;

    private String note;

}
