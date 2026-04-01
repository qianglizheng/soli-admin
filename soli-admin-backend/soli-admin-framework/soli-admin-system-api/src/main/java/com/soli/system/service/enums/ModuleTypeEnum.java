package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 模块类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:03
 */
@Getter
@RequiredArgsConstructor
public enum ModuleTypeEnum implements DbEnum<String> {

    CATALOG("CATALOG", "目录"),
    PAGE("PAGE", "页面"),
    BILL("BILL", "单据");

    private final String value;

    private final String name;
}