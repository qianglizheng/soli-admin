package com.soli.system.service.sysmodule;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 模块上下文预览对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleContextPreviewDTO {

    /** 模块编码 */
    private String moduleCode;

    /** 模块名称 */
    private String moduleName;

    /** 上下文版本 */
    private Integer contextVersion;

    /** 当前岗位 */
    private CurrentPost currentPost;

    /** 当前状态 */
    private CurrentState state;

    /** 单头 Tab */
    private List<TabPreview> headerTabs;

    /** 明细 Tab */
    private List<TabPreview> detailTabs;

    /** 按钮权限 */
    private ButtonPreview buttons;

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
    public static class TabPreview {
        private SysModuleTabDTO tabInfo;
        private Boolean visible;
        private List<FieldPreview> fields;
    }

    @Getter
    @Setter
    public static class FieldPreview {
        private String fieldCode;
        private String label;
        private String componentType;
        private Boolean visible;
        private Boolean editable;
        private Boolean required;
    }

    @Getter
    @Setter
    public static class ButtonPreview {
        private Map<String, ButtonItem> listToolbar;
        private Map<String, ButtonItem> listRow;
        private Map<String, ButtonItem> headerToolbar;
        private Map<String, ButtonItem> detailRow;
    }

    @Getter
    @Setter
    public static class ButtonItem {
        private String label;
        private Boolean visible;
        private Boolean disabled;
    }

}
