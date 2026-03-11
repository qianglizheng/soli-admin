package com.soli.auth.core.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.soli.auth.api.constant.CaptchaConstant;
import com.soli.auth.api.dto.BaseCaptchaDTO;
import com.soli.auth.api.enums.CaptchaScene;
import com.soli.auth.api.service.CaptchaService;
import com.soli.auth.core.config.CaptchaProperties;

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

    @Override
    public BaseCaptchaDTO generateCaptcha(CaptchaScene scene, String target) throws IOException {
        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        String prefix = CaptchaConstant.CAPTCHA_IMAGE_LOGIN_PREFIX;
        long expire = captchaProperties.getSms().getLoginExpire();

        if (CaptchaScene.REGISTER.equals(scene)) {
            prefix = CaptchaConstant.CAPTCHA_IMAGE_REGISTER_PREFIX;
            expire = captchaProperties.getImage().getRegisterExpire();
        }
        String captchaUUID = prefix + UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(captchaUUID, code, Duration.ofSeconds(expire));

        SmsSender.send(target, code);
        return null;
    }
}