package com.soli.common.api.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Y/N 标识枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 22:57
 */
@Getter
@RequiredArgsConstructor
public enum YesNoEnum implements DbEnum<String> {

    YES("Y"),
    NO("N");

    private final String value;
}
