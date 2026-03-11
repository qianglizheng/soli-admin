package com.soli.auth.api.dto;

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

    /** base64编码的图片 */
    private String base64CaptchaImage;

}
