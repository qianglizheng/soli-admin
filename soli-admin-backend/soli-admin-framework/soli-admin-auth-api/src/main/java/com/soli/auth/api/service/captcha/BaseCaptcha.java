package com.soli.auth.api.service.captcha;

import lombok.Data;

/**
 * 基础验证码类
 *
 * @author lizhengqiang
 * @since 2026-03-08 14:54
*/
@Data
public abstract class BaseCaptcha {

    /** 验证码 */
    private String captchaCode;

    /**　验证码唯一 ID */
    private String captchaUUID;

}