package com.soli.auth.api.dto;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-08 14:54
*/
@Data
public abstract class CaptchaDTO {

    /** 验证码 */
    private String captchaCode;

    /**　验证码唯一 ID */
    private String captchaUUID;

}
