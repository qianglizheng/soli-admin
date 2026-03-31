package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 权限级别枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:04
 */
@Getter
@RequiredArgsConstructor
public enum PermissionLevelEnum implements DbEnum<Integer> {

    LEVEL_0(0),
    LEVEL_1(1),
    LEVEL_2(2);

    private final Integer value;
}
