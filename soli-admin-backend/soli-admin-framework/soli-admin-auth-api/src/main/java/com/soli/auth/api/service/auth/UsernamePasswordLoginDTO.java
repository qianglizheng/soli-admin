package com.soli.auth.api.service.auth;

import com.soli.auth.api.service.captcha.BaseCaptcha;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户密码登录，可扩展用户密码 + 验证码登录
 *
 * @author lizhengqiang
 * @since 2026-03-08 14:51
*/
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class UsernamePasswordLoginDTO extends BaseCaptcha {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;

}