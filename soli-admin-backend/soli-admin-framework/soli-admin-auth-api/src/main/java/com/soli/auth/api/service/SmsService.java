package com.soli.auth.api.service;

/**
 * 短信服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface SmsService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param code 验证码
     */
    void send(String phone, String code);

}
