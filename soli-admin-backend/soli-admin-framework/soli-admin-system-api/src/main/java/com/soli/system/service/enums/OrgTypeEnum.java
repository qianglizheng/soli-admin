package com.soli.system.service.enums;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 组织类型枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:04
 */
@Getter
@RequiredArgsConstructor
public enum OrgTypeEnum implements DbEnum<String> {

    GROUP("GROUP"),
    HEADQUARTERS("HEADQUARTERS"),
    BRANCH("BRANCH");

    private final String value;
}
