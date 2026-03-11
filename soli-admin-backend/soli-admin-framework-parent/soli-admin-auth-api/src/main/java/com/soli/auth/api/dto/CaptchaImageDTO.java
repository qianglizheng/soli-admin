package com.soli.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图片验证码数据传输对象
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:37
*/
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class CaptchaImageDTO extends BaseCaptchaDTO {

    /** 验证码 UUID */
    private String captchaUUID;

    /** base64编码的图片 */
    private String base64CaptchaImage;

}
