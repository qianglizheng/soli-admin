package com.soli.auth.api.service;

/**
 * 短信服务
 *
 * @author lizhengqiang
 * @since 2026-03-11 22:20
 */
public interface SmsService {

    void send(String phone, String code);

}