package com.soli.auth.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.soli.auth.api.service.EmailService;

/**
 * 默认邮件服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-29 18:12
 */
@Service
public class DefaultEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(DefaultEmailService.class);

    @Override
    public void send(String email, String code) {
        log.info("发送邮件验证码，邮箱：{}，验证码：{}", email, code);
    }

}
