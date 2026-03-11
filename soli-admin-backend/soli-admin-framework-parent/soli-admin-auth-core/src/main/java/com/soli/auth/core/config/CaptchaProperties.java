package com.soli.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-11 22:09
*/
@Data
@ConfigurationProperties(prefix = "soli.auth.captcha")
public class CaptchaProperties {

    private Image image;

    private Sms sms;
    
    private Email email;

    @Data
    public static class Image {

        /** 登录验证码过期时间（秒） */
        private Long loginExpire;

        /** 注册验证码过期时间（秒） */
        private Long registerExpire;

    }

    @Data
    public static class Sms {

        /** 登录验证码过期时间（秒） */
        private Long loginExpire;

        /** 注册验证码过期时间（秒） */
        private Long registerExpire;

    }
    
    @Data
    public static class Email {

        /** 登录验证码过期时间（秒） */
        private Long loginExpire;

        /** 注册验证码过期时间（秒） */
        private Long registerExpire;

    }
}