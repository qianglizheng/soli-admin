package com.soli.auth.api.constant;

/**
 * 认证服务常量
 *
 * @author lizhengqiang
 * @since 2026-03-09 21:48
*/
public class CaptchaConstant {

    /** 图片登录验证码键前缀 */
    public static final String CAPTCHA_IMAGE_LOGIN_PREFIX = "soli:auth:captcha:image:login";

    /** 图片注册验证码键前缀 */
    public static final String CAPTCHA_IMAGE_REGISTER_PREFIX = "soli:auth:captcha:image:register:";

    /** 短信登录验证码键前缀 */
    public static final String CAPTCHA_SMS_LOGIN_PREFIX = "soli:auth:captcha:sms:login";

    /** 短信注册验证码键前缀 */
    public static final String CAPTCHA_SMS_REGISTER_PREFIX = "soli:auth:captcha:sms:register:";

}