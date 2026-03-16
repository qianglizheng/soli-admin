package com.soli.auth.core.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;


/**
 * @author lizhengqiang
 * @since 2026-03-11 20:58
*/
@Configuration
public class KaptchaConfig {

    @Bean
    public DefaultKaptcha kaptchaProducer() {

        Properties properties = new Properties();

        // 是否有边框
        properties.setProperty("kaptcha.border", "no");

        // 字体颜色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");

        // 图片宽度
        properties.setProperty("kaptcha.image.width", "120");

        // 图片高度
        properties.setProperty("kaptcha.image.height", "40");

        // 字体大小
        properties.setProperty("kaptcha.textproducer.font.size", "32");

        // 验证码长度
        properties.setProperty("kaptcha.textproducer.char.length", "4");

        // 字体
        properties.setProperty("kaptcha.textproducer.font.names", "Arial,Courier");

        Config config = new Config(properties);

        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(config);

        return defaultKaptcha;
    }

}