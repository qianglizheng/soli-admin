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

    STRING("string"),
    LONG("long"),
    DATE("date"),
    DATETIME("datetime"),
    DECIMAL("decimal"),
    INT("int"),
    BOOLEAN("boolean");

    private final String value;
}
