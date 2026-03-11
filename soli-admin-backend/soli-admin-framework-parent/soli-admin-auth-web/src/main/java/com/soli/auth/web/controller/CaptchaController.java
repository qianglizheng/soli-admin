package com.soli.auth.web.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.auth.api.dto.BaseCaptchaDTO;
import com.soli.auth.api.request.CaptchaGenerateRequest;
import com.soli.auth.api.service.CaptchaService;
import com.soli.common.api.vo.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth/captcha")
@AllArgsConstructor
public class CaptchaController {

    private final List<CaptchaService> services;

    @PostMapping
    @Operation(summary = "获取验证码")
    public ApiResponse<BaseCaptchaDTO> generate(@RequestBody CaptchaGenerateRequest request)
            throws Exception {
        CaptchaService svc = services.stream().filter(s -> s.type() == request.getType()).findFirst().orElseThrow();
        BaseCaptchaDTO dto = svc.generateCaptcha(request.getScene(), request.getTarget());
        return ApiResponse.success(dto);
    }

}