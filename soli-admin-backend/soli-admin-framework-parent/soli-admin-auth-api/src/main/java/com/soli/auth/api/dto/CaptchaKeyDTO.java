package com.soli.auth.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-11 23:35
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaKeyDTO implements BaseCaptchaDTO {

    /** 验证码 UUID */
    private String captchaUUID;

}