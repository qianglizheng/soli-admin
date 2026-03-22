package com.soli.auth.api.service;

/**
 * 邮件服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface EmailService {

    void send(String email, String code);

}