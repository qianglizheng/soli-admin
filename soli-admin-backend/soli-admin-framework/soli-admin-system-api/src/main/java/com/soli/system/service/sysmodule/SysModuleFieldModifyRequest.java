package com.soli.system.service.sysmodule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改模块字段请求
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleFieldModifyRequest {

    /**
     * 字段 ID
     */
    @NotNull(message = "字段 ID 不能为空")
    private Long id;

    /**
     * 模块 ID
     */
    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    /**
     * 组件 ID
     */
    @NotNull(message = "组件 ID 不能为空")
    private Long componentId;

    /**
     * 字段编码
     */
    @NotBlank(message = "字段编码不能为空")
    private String fieldCode;

    /**
     * 默认标题
     */
    @NotBlank(message = "默认标题不能为空")
    private String defaultTitle;

    /**
     * 组件类型
     */
    @NotBlank(message = "组件类型不能为空")
    private String componentType;

    /**
     * 数据路径
     */
    @NotBlank(message = "数据路径不能为空")
    private String dataPath;

    /**
     * 值类型
     */
    @NotBlank(message = "值类型不能为空")
    private String valueType;

    /**
     * 是否必填
     */
    private String requiredFlag;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String note;

}
