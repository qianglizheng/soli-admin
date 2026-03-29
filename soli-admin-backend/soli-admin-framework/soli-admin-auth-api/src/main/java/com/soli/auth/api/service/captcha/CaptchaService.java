package com.soli.auth.api.service.captcha;

import com.soli.common.api.exception.BusinessException;

/**
 * 验证码服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface CaptchaService {

    /**
     * 返回验证码类型
     *
     * @return 验证码类型
     */
    CaptchaType type();

    /**
     * 生成验证码
     *
     * @param scene 业务场景
     * @param target 目标值，短信验证码传手机号，邮箱验证码传邮箱地址
     * @return 验证码对象
     * @throws BusinessException 业务异常
     */
    BaseCaptchaDTO generateCaptcha(CaptchaScene scene, String target) throws BusinessException;

    /**
     * 校验验证码
     *
     * @param scene 业务场景
     * @param captchaUUID 验证码唯一标识
     * @param targetCaptcha 待校验的验证码
     * @throws BusinessException 业务异常
     */
    void validateCaptcha(CaptchaScene scene, String captchaUUID, String targetCaptcha) throws BusinessException;

}
