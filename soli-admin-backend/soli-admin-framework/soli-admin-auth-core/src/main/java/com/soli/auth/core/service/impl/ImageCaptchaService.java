package com.soli.auth.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.soli.auth.api.service.captcha.CaptchaConstant;
import com.soli.auth.api.service.captcha.CaptchaImageDTO;
import com.soli.auth.api.service.captcha.CaptchaScene;
import com.soli.auth.api.service.captcha.CaptchaType;
import com.soli.auth.api.service.captcha.CaptchaService;
import com.soli.auth.core.config.CaptchaProperties;
import com.soli.common.api.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-11 22:45
*/
@Service
@RequiredArgsConstructor
public class ImageCaptchaService implements CaptchaService {

    private final DefaultKaptcha kaptchaProducer;

    private final CaptchaProperties captchaProperties;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaType type() {
        return CaptchaType.IMAGE;
    }

    @Override
    public CaptchaImageDTO generateCaptcha(CaptchaScene scene, String target) throws BusinessException {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", stream);
        } catch (IOException e) {
            throw new BusinessException("生成验证码图片失败");
        }
        String base64 = Base64.getEncoder().encodeToString(stream.toByteArray());

        String prefix = CaptchaConstant.CAPTCHA_IMAGE_LOGIN_PREFIX;
        long expire = captchaProperties.getImage().getLoginExpire();
        if (CaptchaScene.REGISTER.equals(scene)) {
            prefix = CaptchaConstant.CAPTCHA_IMAGE_REGISTER_PREFIX;
            expire = captchaProperties.getImage().getRegisterExpire();
        }

        String uuid = UUID.randomUUID().toString();
        String key = prefix + uuid;
        stringRedisTemplate.opsForValue().set(key, text, Duration.ofSeconds(expire));
        return new CaptchaImageDTO(uuid, base64);
    }

    @Override
    public void validateCaptcha(CaptchaScene scene, String captchaUUID, String targetCaptcha) throws BusinessException {
        String prefix = CaptchaConstant.CAPTCHA_IMAGE_LOGIN_PREFIX;
        if (CaptchaScene.REGISTER.equals(scene)) {
            prefix = CaptchaConstant.CAPTCHA_IMAGE_REGISTER_PREFIX;
        }
        String key = prefix + captchaUUID;
        String captcha = stringRedisTemplate.opsForValue().get(key);
        if (Objects.isNull(captcha) || !captcha.equalsIgnoreCase(targetCaptcha)) {
            throw new BusinessException("验证码错误或已过期");
        }
        stringRedisTemplate.delete(key);
    }

}