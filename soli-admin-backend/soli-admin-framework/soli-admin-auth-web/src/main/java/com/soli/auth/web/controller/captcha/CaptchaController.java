package com.soli.auth.web.controller.captcha;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.auth.api.service.captcha.BaseCaptchaDTO;
import com.soli.auth.api.service.captcha.CaptchaService;
import com.soli.common.api.vo.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

@Tag(name = "验证码")
@RestController
@RequestMapping("/auth/captcha")
@AllArgsConstructor
public class CaptchaController {

    private final List<CaptchaService> captchaServices;

    @PostMapping
    @Operation(summary = "获取验证码")
    public Result<BaseCaptchaDTO> generate(@RequestBody CaptchaGenerateRequest request) {
        CaptchaService captchaService = captchaServices.stream()
                .filter(service -> service.type() == request.getType())
                .findFirst()
                .orElseThrow();
        BaseCaptchaDTO dto = captchaService.generateCaptcha(request.getScene(), request.getTarget());
        return Result.success(dto);
    }

}