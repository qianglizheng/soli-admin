package com.soli.auth.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.soli.auth.api.service.SmsService;

/**
 * 默认短信服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-29 18:12
 */
@Service
public class DefaultSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(DefaultSmsService.class);

    @Override
    public void send(String phone, String code) {
        log.info("发送短信验证码，手机号：{}，验证码：{}", phone, code);
    }

}
