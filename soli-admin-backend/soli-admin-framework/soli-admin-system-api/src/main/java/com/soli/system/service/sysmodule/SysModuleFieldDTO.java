package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:52
 */
@Getter
@Setter
public class SysModuleFieldDTO extends BaseDTO {

    /**
     * 模块 ID
     */
    private Long moduleId;

    /**
     * 组件 ID
     */
    private Long componentId;

    /**
     * 组件编码
     */
    private String componentCode;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 默认标题
     */
    private String defaultTitle;

    /**
     * 展示标题
     */
    private String displayTitle;

    /**
     * 占位提示
     */
    private String placeholder;

    /**
     * 帮助文本
     */
    private String helpText;

    /**
     * 组件类型
     */
    private String componentType;

    /**
     * 数据路径
     */
    private String dataPath;

    /**
     * 值类型
     */
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

}
