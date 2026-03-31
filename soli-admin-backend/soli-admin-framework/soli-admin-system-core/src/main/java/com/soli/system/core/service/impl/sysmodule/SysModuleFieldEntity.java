package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import com.soli.system.service.enums.ModuleComponentTypeEnum;
import com.soli.system.service.enums.ModuleValueTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段实体
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:58
 */
@Getter
@Setter
public class SysModuleFieldEntity extends BaseEntity {

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
    private ModuleComponentTypeEnum componentType;

    /**
     * 数据路径
     */
    private String dataPath;

    /**
     * 值类型
     */
    private ModuleValueTypeEnum valueType;

    /**
     * 是否必填
     */
    private BinaryFlagEnum requiredFlag;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态
     */
    private NormalDisableStatusEnum status;

}
