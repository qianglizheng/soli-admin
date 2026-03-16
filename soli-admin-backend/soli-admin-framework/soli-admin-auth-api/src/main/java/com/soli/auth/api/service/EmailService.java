package com.soli.auth.api.service;

public interface EmailService {

    void send(String email, String code);

}