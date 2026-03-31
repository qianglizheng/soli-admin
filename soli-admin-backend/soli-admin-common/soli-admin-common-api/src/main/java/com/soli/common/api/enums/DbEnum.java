package com.soli.common.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 数据库枚举接口
 *
 * @param <T> 枚举值类型
 * @author lizhengqiang
 * @since 2026-03-31 22:55
 */
public interface DbEnum<T> {

    /**
     * 获取数据库枚举值
     *
     * @return 枚举值
     */
    @JsonValue
    T getValue();
}
