package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Runtime module context DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:59
 */
@Getter
@Setter
public class SysModuleContextDTO {

    private Long moduleId;

    private String moduleCode;

    private String moduleName;

    private Integer contextVersion;

    private String statefulFlag;

    private String stateFieldCode;

    private CurrentState state;

    private Map<String, FieldConfig> fieldConfigs;

    @Getter
    @Setter
    public static class CurrentState {
        private String currentValue;
        private String currentLabel;
        private String stateField;
    }

    @Getter
    @Setter
    public static class FieldConfig {
        private String configKey;
        private String configType;
        private String component;
        private String code;
        private Long fieldId;
        private Long buttonId;
        private String defaultTitle;
        private String displayTitle;
        private String label;
        private String placeholder;
        private String helpText;
        private String componentType;
        private String dataPath;
        private String valueType;
        private Integer permissionLevel;
        private Boolean visible;
        private Boolean editable;
        private Boolean required;
        private Boolean disabled;
    }

}
