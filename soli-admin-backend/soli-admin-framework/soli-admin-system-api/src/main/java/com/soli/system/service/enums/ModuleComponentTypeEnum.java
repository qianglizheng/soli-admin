package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 模块字段组件类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:03
 */
@Getter
@RequiredArgsConstructor
public enum ModuleComponentTypeEnum implements DbEnum<String> {

    INPUT("input", "输入框"),
    SEARCH_SELECT("search-select", "搜索选择"),
    DATE("date", "日期"),
    DATETIME("datetime", "日期时间"),
    SELECT("select", "下拉选择"),
    RADIO("radio", "单选框"),
    SWITCH("switch", "开关"),
    TEXT("text", "文本"),
    TEXTAREA("textarea", "多行文本"),
    NUMBER("number", "数字"),
    AMOUNT("amount", "金额"),
    TAG("tag", "标签"),
    BUTTON("button", "按钮");

    private final String value;

    private final String name;
}