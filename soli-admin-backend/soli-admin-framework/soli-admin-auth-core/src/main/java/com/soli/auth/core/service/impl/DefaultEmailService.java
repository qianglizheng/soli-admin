package com.soli.auth.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.soli.auth.api.service.EmailService;

@Service
public class DefaultEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(DefaultEmailService.class);

    @Override
    public void send(String email, String code) {
        log.info("Send Email to {} code {}", email, code);
    }

}