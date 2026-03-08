package com.soli.auth.web.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.auth.api.dto.UsernamePasswordLoginDTO;
import com.soli.auth.api.service.AuthService;
import com.soli.auth.api.vo.TokenVO;
import com.soli.common.api.vo.ApiResponse;

import lombok.AllArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-08 14:17
*/
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<TokenVO> login(@RequestBody UsernamePasswordLoginDTO userInfo) {
        //authService.login(userInfo);
        return ApiResponse.fail("aa");
    }
}
