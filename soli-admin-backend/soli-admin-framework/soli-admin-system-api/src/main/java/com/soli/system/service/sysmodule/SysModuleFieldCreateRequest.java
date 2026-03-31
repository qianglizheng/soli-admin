package com.soli.system.service.sysmodule;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.system.service.enums.ModuleComponentTypeEnum;
import com.soli.system.service.enums.ModuleValueTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增模块字段请求
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleFieldCreateRequest {

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
    private ModuleComponentTypeEnum componentType;

    /**
     * 数据路径
     */
    @NotBlank(message = "数据路径不能为空")
    private String dataPath;

    /**
     * 值类型
     */
    @NotBlank(message = "值类型不能为空")
    private ModuleValueTypeEnum valueType;

    /**
     * 是否必填
     */
    @JsonSetter(nulls = Nulls.SKIP)
    private BinaryFlagEnum requiredFlag = BinaryFlagEnum.NO;

    /**
     * 排序
     */
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer sort = 1;

    /**
     * 状态
     */
    @JsonSetter(nulls = Nulls.SKIP)
    private NormalDisableStatusEnum status = NormalDisableStatusEnum.NORMAL;

    /**
     * 备注
     */
    private String note;

}
