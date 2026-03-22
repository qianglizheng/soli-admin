package com.soli.auth.api.service.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 携带验证码 UUID 对象
 *
 * @author lizhengqiang
 * @since 2026-03-11 23:35
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaUUIDDTO implements BaseCaptchaDTO {

    /** 验证码 UUID */
    private String captchaUUID;

}