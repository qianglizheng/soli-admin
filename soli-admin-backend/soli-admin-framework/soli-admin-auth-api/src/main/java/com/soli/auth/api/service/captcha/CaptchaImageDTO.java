package com.soli.auth.api.service.captcha;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 图片验证码数据传输对象
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:37
*/
@Data
@AllArgsConstructor
public class CaptchaImageDTO implements BaseCaptchaDTO {

    /** 验证码唯一 ID */
    private String captchaUUID;

    /** base64编码的图片 */
    private String base64CaptchaImage;

}
