package com.soli.auth.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.soli.auth.api.constant.CaptchaConstant;
import com.soli.auth.api.dto.BaseCaptchaDTO;
import com.soli.auth.api.dto.CaptchaImageDTO;
import com.soli.auth.api.enums.CaptchaScene;
import com.soli.auth.api.enums.CaptchaType;
import com.soli.auth.api.service.CaptchaService;
import com.soli.auth.core.config.CaptchaProperties;
import com.soli.common.api.exception.BusinessException;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-11 22:45
*/
@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(CaptchaProperties.class)
public class ImageCaptchaService implements CaptchaService {

    private final DefaultKaptcha kaptchaProducer;

    private final CaptchaProperties captchaProperties;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public CaptchaType type() {
        return CaptchaType.IMAGE;
    }

    @Override
    public CaptchaImageDTO generateCaptcha(CaptchaScene scene, String target) throws IOException, BusinessException {
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", stream);
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
        return new CaptchaImageDTO(base64);
    }

}