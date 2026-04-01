package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 模块字段值类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:03
 */
@Getter
@RequiredArgsConstructor
public enum ModuleValueTypeEnum implements DbEnum<String> {

    STRING("string", "字符串"),
    LONG("long", "长整数"),
    DATE("date", "日期"),
    DATETIME("datetime", "日期时间"),
    DECIMAL("decimal", "小数"),
    INT("int", "整数"),
    BOOLEAN("boolean", "布尔");

    private final String value;

    private final String name;
}