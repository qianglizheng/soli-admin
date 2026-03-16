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
import com.soli.auth.api.service.EmailService;
import com.soli.common.api.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailCaptchaService implements CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    private final CaptchaProperties properties;

    private final EmailService emailSender;

    @Override
    public CaptchaType type() {
        return CaptchaType.EMAIL;
    }

    @Override
    public CaptchaUUIDDTO generateCaptcha(CaptchaScene scene, String target) {
        Assert.hasText(target, "邮箱不能为空");

        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        String prefix = CaptchaConstant.CAPTCHA_EMAIL_LOGIN_PREFIX;
        long expire = properties.getEmail().getLoginExpire();
        if (CaptchaScene.REGISTER.equals(scene)) {
            prefix = CaptchaConstant.CAPTCHA_EMAIL_REGISTER_PREFIX;
            expire = properties.getEmail().getRegisterExpire();
        }
        String key = prefix + target;
        stringRedisTemplate.opsForValue().set(key, code, Duration.ofSeconds(expire));
        emailSender.send(target, code);
        return new CaptchaUUIDDTO(target);
    }

    @Override
    public void validateCaptcha(CaptchaScene scene, String captchaUUID, String targetCaptcha) throws BusinessException {

    }

}