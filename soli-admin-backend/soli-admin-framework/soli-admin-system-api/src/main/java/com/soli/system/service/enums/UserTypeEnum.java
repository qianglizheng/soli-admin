package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 用户类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:05
 */
@Getter
@RequiredArgsConstructor
public enum UserTypeEnum implements DbEnum<String> {

    ADMIN("0", "超级管理员"),
    NORMAL("1", "普通用户");

    private final String value;

    private final String name;
}