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

    INPUT("input"),
    SEARCH_SELECT("search-select"),
    DATE("date"),
    DATETIME("datetime"),
    SELECT("select"),
    RADIO("radio"),
    SWITCH("switch"),
    TEXT("text"),
    TEXTAREA("textarea"),
    NUMBER("number"),
    AMOUNT("amount"),
    TAG("tag"),
    BUTTON("button");

    private final String value;
}
