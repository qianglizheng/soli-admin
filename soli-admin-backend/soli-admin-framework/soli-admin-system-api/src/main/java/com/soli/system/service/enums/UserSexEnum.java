package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户性别枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:05
 */
@Getter
@RequiredArgsConstructor
public enum UserSexEnum implements DbEnum<String> {

    MALE("0"),
    FEMALE("1"),
    UNKNOWN("2");

    private final String value;
}
