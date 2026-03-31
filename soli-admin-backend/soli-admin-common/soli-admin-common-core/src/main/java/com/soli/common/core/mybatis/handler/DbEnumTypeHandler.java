package com.soli.common.core.mybatis.handler;

import com.soli.common.api.enums.DbEnum;
import com.soli.common.api.enums.DbEnumUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库枚举 TypeHandler
 *
 * @param <E> 枚举类型
 * @author lizhengqiang
 * @since 2026-03-31 22:59
 */
public class DbEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;

    public DbEnumTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object value = parameter instanceof DbEnum<?> dbEnum ? dbEnum.getValue() : parameter.name();
        ps.setObject(i, value);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return toEnum(rs.getObject(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return toEnum(rs.getObject(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return toEnum(cs.getObject(columnIndex));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private E toEnum(Object value) {
        if (value == null) {
            return null;
        }
        if (DbEnum.class.isAssignableFrom(type)) {
            return (E) DbEnumUtils.fromValue((Class) type, value);
        }
        return Enum.valueOf(type, String.valueOf(value));
    }
}
