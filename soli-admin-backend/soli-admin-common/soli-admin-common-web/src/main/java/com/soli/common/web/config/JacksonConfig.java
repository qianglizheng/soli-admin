package com.soli.common.web.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.soli.common.api.enums.DbEnum;
import com.soli.common.api.enums.DbEnumUtils;
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
     * 全局 Jackson 自定义配置
     *
     * @return Jackson2ObjectMapperBuilderCustomizer
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonTrimStringCustomizer() {
        return builder -> {
            builder.deserializerByType(String.class, new StringTrimJsonDeserializer());
            builder.modules(new DbEnumJacksonModule());
        };
    }

    private static class StringTrimJsonDeserializer extends JsonDeserializer<String> {

        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            String value = parser.getValueAsString();
            return value == null ? null : value.trim();
        }
    }

    private static class DbEnumJacksonModule extends SimpleModule {

        DbEnumJacksonModule() {
            setSerializerModifier(new DbEnumSerializerModifier());
            setDeserializerModifier(new DbEnumDeserializerModifier());
        }
    }

    private static class DbEnumSerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifyEnumSerializer(SerializationConfig config,
                                                      JavaType valueType,
                                                      BeanDescription beanDesc,
                                                      JsonSerializer<?> serializer) {
            if (DbEnum.class.isAssignableFrom(beanDesc.getBeanClass())) {
                return new DbEnumJsonSerializer();
            }
            return serializer;
        }
    }

    private static class DbEnumDeserializerModifier extends BeanDeserializerModifier {

        @Override
        public JsonDeserializer<?> modifyEnumDeserializer(DeserializationConfig config,
                                                          JavaType type,
                                                          BeanDescription beanDesc,
                                                          JsonDeserializer<?> deserializer) {
            if (DbEnum.class.isAssignableFrom(beanDesc.getBeanClass())) {
                return new DbEnumJsonDeserializer(beanDesc.getBeanClass());
            }
            return deserializer;
        }
    }

    private static class DbEnumJsonSerializer extends StdSerializer<Enum<?>> {

        DbEnumJsonSerializer() {
            super((Class<Enum<?>>) (Class<?>) Enum.class);
        }

        @Override
        public void serialize(Enum<?> value, com.fasterxml.jackson.core.JsonGenerator generator, SerializerProvider provider) throws IOException {
            if (value instanceof DbEnum<?> dbEnum) {
                generator.writeObject(dbEnum.getValue());
                return;
            }
            generator.writeString(value.name());
        }
    }

    private static class DbEnumJsonDeserializer extends JsonDeserializer<Enum<?>> {

        private final Class<?> enumClass;

        DbEnumJsonDeserializer(Class<?> enumClass) {
            this.enumClass = enumClass;
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Enum<?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonToken token = parser.currentToken();
            Object value;
            if (token == JsonToken.VALUE_NULL) {
                return null;
            }
            if (token == JsonToken.VALUE_NUMBER_INT) {
                value = parser.getNumberValue();
            } else {
                value = parser.getValueAsString();
            }
            try {
                return DbEnumUtils.fromValue((Class) enumClass, value);
            } catch (IllegalArgumentException ex) {
                throw InvalidFormatException.from(parser, ex.getMessage(), value, enumClass);
            }
        }
    }

}
