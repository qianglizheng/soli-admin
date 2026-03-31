package com.soli.system.service.sysmodule;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改模块组件请求
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleComponentModifyRequest {

    /**
     * 组件 ID
     */
    @NotNull(message = "组件 ID 不能为空")
    private Long id;

    /**
     * 模块 ID
     */
    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    /**
     * 组件编码
     */
    @NotBlank(message = "组件编码不能为空")
    private String componentCode;

    /**
     * 组件名称
     */
    @NotBlank(message = "组件名称不能为空")
    private String componentName;

    /**
     * 排序
     */
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer sort = 1;

    /**
     * 状态
     */
    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    /**
     * 备注
     */
    private String note;

}
