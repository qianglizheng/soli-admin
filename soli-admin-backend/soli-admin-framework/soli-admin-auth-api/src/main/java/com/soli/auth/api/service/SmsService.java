package com.soli.auth.api.service;

public interface SmsService {

    void send(String phone, String code);

}