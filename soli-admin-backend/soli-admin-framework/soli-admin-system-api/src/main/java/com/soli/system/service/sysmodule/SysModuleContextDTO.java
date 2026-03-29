package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 模块运行上下文 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:59
 */
@Getter
@Setter
public class SysModuleContextDTO {

    /**
     * 模块 ID
     */
    private Long moduleId;

    /**
     * 模块编码
     */
    private String moduleCode;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 上下文版本
     */
    private Integer contextVersion;

    /**
     * 是否状态型模块
     */
    private String statefulFlag;

    /**
     * 状态字段编码
     */
    private String stateFieldCode;

    /**
     * 当前状态
     */
    private CurrentState state;

    /**
     * 字段配置集合
     */
    private Map<String, FieldConfig> fieldConfigs;

    @Getter
    @Setter
    public static class CurrentState {

        /**
         * 当前状态值
         */
        private String currentValue;

        /**
         * 当前状态名称
         */
        private String currentLabel;

        /**
         * 状态字段
         */
        private String stateField;
    }

    @Getter
    @Setter
    public static class FieldConfig {

        /**
         * 配置键
         */
        private String configKey;

        /**
         * 配置类型
         */
        private String configType;

        /**
         * 组件编码
         */
        private String component;

        /**
         * 字段或按钮编码
         */
        private String code;

        /**
         * 字段 ID
         */
        private Long fieldId;

        /**
         * 按钮 ID
         */
        private Long buttonId;

        /**
         * 默认标题
         */
        private String defaultTitle;

        /**
         * 展示标题
         */
        private String displayTitle;

        /**
         * 标签名称
         */
        private String label;

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
         * 权限级别
         */
        private Integer permissionLevel;

        /**
         * 是否可见
         */
        private Boolean visible;

        /**
         * 是否可编辑
         */
        private Boolean editable;

        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * 是否禁用
         */
        private Boolean disabled;
    }

}
