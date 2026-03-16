package com.soli.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-11 22:09
*/
@Data
@Configuration
@ConfigurationProperties(prefix = "soli.auth.captcha")
public class CaptchaProperties {

    private Image image;

    private Sms sms;
    
    private Email email;

    @Data
    public static class Image {

        /** 开启登录图片验证码 */
        private boolean loginEnable = false;

        /** 登录验证码过期时间（秒） */
        private Long loginExpire;

        /** 开启注册图片验证码 */
        private boolean registerEnable = false;

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