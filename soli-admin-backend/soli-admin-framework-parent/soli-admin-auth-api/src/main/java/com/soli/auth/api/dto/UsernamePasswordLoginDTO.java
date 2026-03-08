package com.soli.auth.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lizhengqiang
 * @since 2026-03-08 14:51
*/
@Data
@EqualsAndHashCode(callSuper = true)
public class UsernamePasswordLoginDTO extends CaptchaDTO {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

}