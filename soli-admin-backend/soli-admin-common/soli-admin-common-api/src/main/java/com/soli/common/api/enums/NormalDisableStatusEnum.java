package com.soli.common.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 通用状态枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 22:58
 */
@Getter
@RequiredArgsConstructor
public enum NormalDisableStatusEnum implements DbEnum<String> {

    NORMAL("0"),
    DISABLED("1");

    private final String value;
}
