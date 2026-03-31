package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 岗位类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:04
 */
@Getter
@RequiredArgsConstructor
public enum PostTypeEnum implements DbEnum<String> {

    MANAGER("MANAGER"),
    BUYER("BUYER"),
    OPERATION("OPERATION"),
    FINANCE("FINANCE");

    private final String value;
}
