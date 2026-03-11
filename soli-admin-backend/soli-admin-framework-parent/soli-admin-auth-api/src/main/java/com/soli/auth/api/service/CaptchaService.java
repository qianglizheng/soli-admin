package com.soli.auth.api.service;

import java.io.IOException;

import com.soli.auth.api.dto.BaseCaptchaDTO;
import com.soli.auth.api.enums.CaptchaScene;

/**
 * 验证码服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface CaptchaService {

    /**
     * 生成验证码
     *
     * @param target 手机验证码就是手机号，邮箱验证码就是邮箱
     * @return BaseCaptchaDTO 图片验证码需要返回验证码唯一标识和 base64编码的图片
     */
    BaseCaptchaDTO generateCaptcha(CaptchaScene scene, String target) throws IOException;

}