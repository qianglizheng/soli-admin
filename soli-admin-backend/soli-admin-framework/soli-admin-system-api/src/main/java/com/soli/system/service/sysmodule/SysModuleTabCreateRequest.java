package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增模块 Tab 请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleTabCreateRequest {

    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    @NotBlank(message = "Tab 作用域不能为空")
    private String tabScope;

    @NotBlank(message = "Tab 编码不能为空")
    private String tabCode;

    @NotBlank(message = "Tab 名称不能为空")
    private String tabName;

    private Integer sort;

    private String status;

    private String note;

}
