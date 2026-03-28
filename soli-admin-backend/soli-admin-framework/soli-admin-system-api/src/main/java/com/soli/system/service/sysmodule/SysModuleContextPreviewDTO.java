package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Runtime module context preview DTO.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:59
 */
@Getter
@Setter
public class SysModuleContextPreviewDTO {

    private String moduleCode;

    private String moduleName;

    private Integer contextVersion;

    private CurrentPost currentPost;

    private CurrentState state;

    private Map<String, FieldConfig> fieldConfigs;

    @Getter
    @Setter
    public static class CurrentPost {
        private Long postId;
        private String postCode;
        private String postName;
    }

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
        private String label;
        private String componentType;
        private Boolean visible;
        private Boolean editable;
        private Boolean required;
        private Boolean disabled;
    }

}
