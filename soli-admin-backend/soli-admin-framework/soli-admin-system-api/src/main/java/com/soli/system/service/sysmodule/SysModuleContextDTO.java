package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 模块上下文对象
 *
 * @author lizhengqiang
 * @since 2026-03-27 15:35
 */
@Getter
@Setter
public class SysModuleContextDTO {

    /** 模块 ID */
    private Long moduleId;

    /** 模块编码 */
    private String moduleCode;

    /** 模块名称 */
    private String moduleName;

    /** 上下文版本 */
    private Integer contextVersion;

    /** 状态型模块标识 */
    private String statefulFlag;

    /** 状态字段编码 */
    private String stateFieldCode;

    /** 当前状态 */
    private CurrentState state;

    /** 表头 Tab */
    private List<TabContext> headerTabs;

    /** 明细 Tab */
    private List<TabContext> detailTabs;

    /** 按钮上下文 */
    private ButtonContext buttons;

    @Getter
    @Setter
    public static class CurrentState {
        private String currentValue;
        private String currentLabel;
        private String stateField;
    }

    @Getter
    @Setter
    public static class TabContext {
        private SysModuleTabDTO tabInfo;
        private Boolean visible;
        private List<FieldContext> fields;
    }

    @Getter
    @Setter
    public static class FieldContext {
        private Long fieldId;
        private String fieldCode;
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
    }

    @Getter
    @Setter
    public static class ButtonContext {
        private Map<String, ButtonItem> listToolbar;
        private Map<String, ButtonItem> listRow;
        private Map<String, ButtonItem> headerToolbar;
        private Map<String, ButtonItem> detailRow;
    }

    @Getter
    @Setter
    public static class ButtonItem {
        private Long buttonId;
        private String buttonCode;
        private String defaultTitle;
        private String label;
        private String area;
        private Integer permissionLevel;
        private Boolean visible;
        private Boolean disabled;
    }

}
