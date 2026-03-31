package com.soli.system.service.sysmodule;

import com.soli.system.service.enums.ModuleComponentTypeEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * 模块上下文预览 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:59
 */
@Getter
@Setter
public class SysModuleContextPreviewDTO {

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
     * 当前岗位
     */
    private CurrentPost currentPost;

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
    public static class CurrentPost {

        /**
         * 岗位 ID
         */
        private Long postId;

        /**
         * 岗位编码
         */
        private String postCode;

        /**
         * 岗位名称
         */
        private String postName;
    }

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
         * 标签名称
         */
        private String label;

        /**
         * 组件类型
         */
        private ModuleComponentTypeEnum componentType;

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
