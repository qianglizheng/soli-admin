package com.soli.auth.core.service.impl;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.soli.auth.api.constant.CaptchaConstant;
import com.soli.auth.api.dto.CaptchaUUIDDTO;
import com.soli.auth.api.enums.CaptchaScene;
import com.soli.auth.api.enums.CaptchaType;
import com.soli.auth.api.service.CaptchaService;
import com.soli.auth.core.config.CaptchaProperties;
import com.soli.auth.api.service.SmsService;
import com.soli.common.api.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-11 23:00
*/
@Service
@RequiredArgsConstructor
public class SmsCaptchaService implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    private final CaptchaProperties captchaProperties;
    
    private final SmsService smsSender;

    @Override
    public CaptchaType type() {
        return CaptchaType.SMS;
    }

    @Override
    public CaptchaUUIDDTO generateCaptcha(CaptchaScene scene, String target) {
        Assert.hasText(target, "手机号不能为空");

        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        String prefix = CaptchaConstant.CAPTCHA_SMS_LOGIN_PREFIX;
        long expire = captchaProperties.getSms().getLoginExpire();

        if (CaptchaScene.REGISTER.equals(scene)) {
            prefix = CaptchaConstant.CAPTCHA_SMS_REGISTER_PREFIX;
            expire = captchaProperties.getSms().getRegisterExpire();
        }
        String key = prefix + target;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofSeconds(expire));

        smsSender.send(target, code);
        return new CaptchaUUIDDTO(target);
    }

    @Override
    public void validateCaptcha(CaptchaScene scene, String captchaUUID, String targetCaptcha) throws BusinessException {

    }

}