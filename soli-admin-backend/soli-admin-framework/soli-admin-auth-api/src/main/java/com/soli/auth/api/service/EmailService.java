package com.soli.auth.api.service;

/**
 * 邮件服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface EmailService {

    /**
     * 发送邮件验证码
     *
     * @param email 邮箱
     * @param code 验证码
     */
    void send(String email, String code);

}
