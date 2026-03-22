package com.soli.auth.web.controller.captcha;

import com.soli.auth.api.service.captcha.CaptchaScene;
import com.soli.auth.api.service.captcha.CaptchaType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取验证码请求
 *
 * @author lizhengqiang
 * @since 2026-03-11 23:46
*/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaGenerateRequest {

    /** 验证码类型 */
    private CaptchaType type;

    /** 业务场景 */
    private CaptchaScene scene;

    /** 手机号、邮箱 */
    private String target;

}