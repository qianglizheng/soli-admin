package com.soli.auth.api.service;

import com.soli.auth.api.dto.BaseCaptchaDTO;
import com.soli.auth.api.enums.CaptchaScene;
import com.soli.auth.api.enums.CaptchaType;
import com.soli.common.api.exception.BusinessException;

/**
 * 验证码服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface CaptchaService {

    /**
     * 类型标记
     *
     * @return CaptchaType
     */
    CaptchaType type();

    /**
     * 生成验证码
     *
     * @param scene 业务场景
     * @param target 手机验证码就是手机号，邮箱验证码就是邮箱
     * @return BaseCaptchaDTO 图片验证码需要返回验证码唯一标识和 base64编码的图片
     * @throws BusinessException 异常
     */
    BaseCaptchaDTO generateCaptcha(CaptchaScene scene, String target) throws BusinessException;

    /**
     * 校验验证码
     *
     * @param scene 业务场景
     * @param captchaUUID 验证码唯一 ID
     * @param targetCaptcha 要比较的目标验证码
     */
    void validateCaptcha(CaptchaScene scene, String captchaUUID, String targetCaptcha) throws BusinessException;

}