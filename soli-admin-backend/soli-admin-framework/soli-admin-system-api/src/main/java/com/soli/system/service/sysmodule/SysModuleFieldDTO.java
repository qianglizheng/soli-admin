package com.soli.system.service.sysmodule;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleFieldDTO extends BaseDTO {

    /** 模块 ID */
    private Long moduleId;

    /** Tab ID */
    private Long tabId;

    /** 字段作用域 */
    private String fieldScope;

    /** 字段编码 */
    private String fieldCode;

    /** 默认标题 */
    private String defaultTitle;

    /** 显示标题 */
    private String displayTitle;

    /** 占位提示 */
    private String placeholder;

    /** 帮助说明 */
    private String helpText;

    /** 组件类型 */
    private String componentType;

    /** 数据路径 */
    private String dataPath;

    /** 值类型 */
    private String valueType;

    /** 必填标识 */
    private String requiredFlag;

    /** 排序 */
    private Integer sort;

    /** 状态 */
    private String status;

}
