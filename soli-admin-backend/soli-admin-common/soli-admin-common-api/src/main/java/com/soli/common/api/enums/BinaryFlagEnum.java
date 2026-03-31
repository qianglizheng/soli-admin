package com.soli.common.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 0/1 标识枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 22:57
 */
@Getter
@RequiredArgsConstructor
public enum BinaryFlagEnum implements DbEnum<String> {

    NO("0"),
    YES("1");

    private final String value;
}
