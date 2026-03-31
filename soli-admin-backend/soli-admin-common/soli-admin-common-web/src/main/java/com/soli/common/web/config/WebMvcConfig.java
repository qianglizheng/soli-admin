package com.soli.common.web.config;

import com.soli.common.api.enums.DbEnum;
import com.soli.common.api.enums.DbEnumUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc 公共配置
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:01
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToDbEnumConverterFactory());
    }

    private static class StringToDbEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
            if (!DbEnum.class.isAssignableFrom(targetType)) {
                throw new IllegalArgumentException("Unsupported enum type: " + targetType.getName());
            }
            return source -> {
                if (source == null || source.trim().isEmpty()) {
                    return null;
                }
                return (T) DbEnumUtils.fromValue((Class) targetType, source);
            };
        }
    }
}
