package com.soli.auth.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.soli.auth.api.service.SmsService;

@Service
public class DefaultSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(DefaultSmsService.class);

    @Override
    public void send(String phone, String code) {
        log.info("Send SMS to {} code {}", phone, code);
    }

}