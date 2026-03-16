package com.soli.auth.web.controller.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.auth.api.dto.TokenDTO;
import com.soli.auth.api.dto.UsernamePasswordLoginDTO;
import com.soli.auth.api.service.AuthService;
import com.soli.common.api.vo.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * 认证服务控制器
 *
 * @author lizhengqiang
 * @since 2026-03-08 14:17
*/
@Tag(name = "用户认证")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "用户名密码登录")
    @PostMapping("/login-using-username")
    public ApiResponse<TokenDTO> login(@RequestBody UsernamePasswordLoginDTO userInfo) {
        TokenDTO token = authService.loginByUsernameAndPassword(userInfo);
        return ApiResponse.success(token);
    }

}
