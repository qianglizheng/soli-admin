package com.soli.common.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Jackson 公共配置
 *
 * @author lizhengqiang
 * @since 2026-03-31 10:30
 */
@Configuration
public class JacksonConfig {

    /**
     * 全局字符串反序列化去除前后空格
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonTrimStringCustomizer() {
        return builder -> builder.deserializerByType(String.class, new StringTrimJsonDeserializer());
    }

    private static class StringTrimJsonDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String value = parser.getValueAsString();
            return value == null ? null : value.trim();
        }
    }

}
