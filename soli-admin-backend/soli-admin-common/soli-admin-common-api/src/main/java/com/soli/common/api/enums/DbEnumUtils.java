package com.soli.common.api.enums;

import java.util.Objects;

/**
 * 数据库枚举工具类
 *
 * @author lizhengqiang
 * @since 2026-03-31 22:56
 */
public final class DbEnumUtils {

    private DbEnumUtils() {
    }

    /**
     * 根据枚举值获取枚举对象
     *
     * @param enumType 枚举类型
     * @param value 枚举值
     * @param <E> 枚举类型
     * @return 枚举对象
     */
    public static <E extends Enum<E> & DbEnum<?>> E fromValue(Class<E> enumType, Object value) {
        if (enumType == null || value == null) {
            return null;
        }
        if (value instanceof String text && text.trim().isEmpty()) {
            return null;
        }
        E[] enumConstants = enumType.getEnumConstants();
        if (enumConstants == null) {
            return null;
        }
        for (E enumConstant : enumConstants) {
            if (matches(enumConstant.getValue(), value)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("未找到匹配的枚举值: " + enumType.getName() + " -> " + value);
    }

    private static boolean matches(Object enumValue, Object actualValue) {
        if (Objects.equals(enumValue, actualValue)) {
            return true;
        }
        if (enumValue == null || actualValue == null) {
            return false;
        }
        if (enumValue instanceof String left && actualValue instanceof String right) {
            return left.equalsIgnoreCase(right);
        }
        return String.valueOf(enumValue).equals(String.valueOf(actualValue));
    }
}
