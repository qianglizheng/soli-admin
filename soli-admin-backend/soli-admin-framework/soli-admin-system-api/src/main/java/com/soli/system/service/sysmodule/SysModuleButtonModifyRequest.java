package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改模块按钮请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleButtonModifyRequest {

    @NotNull(message = "按钮 ID 不能为空")
    private Long id;

    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    @NotBlank(message = "按钮编码不能为空")
    private String buttonCode;

    @NotBlank(message = "默认标题不能为空")
    private String defaultTitle;

    private Integer sort;

    private String status;

    private String note;

}
