package com.soli.auth.core.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.soli.auth.api.dto.TokenDTO;
import com.soli.auth.api.dto.UsernamePasswordLoginDTO;
import com.soli.auth.api.enums.CaptchaScene;
import com.soli.auth.api.enums.CaptchaType;
import com.soli.auth.api.service.AuthService;
import com.soli.auth.api.service.CaptchaService;
import com.soli.auth.api.service.JwtService;
import com.soli.auth.core.config.CaptchaProperties;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserService;

import lombok.RequiredArgsConstructor;

/**
 * 系统用户认证服务
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:59
*/
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserService service;

    private final JwtService jwtService;

    private final List<CaptchaService> captchaServices;

    private final CaptchaProperties captchaProperties;

    @Override
    public TokenDTO loginByUsernameAndPassword(UsernamePasswordLoginDTO userInfo) throws BusinessException {
        Assert.notNull(userInfo, "用户登录信息不能为空");
        if (captchaProperties.getImage().isLoginEnable()) {
            validateCaptcha(CaptchaType.IMAGE, userInfo.getCaptchaUUID(), userInfo.getCaptchaCode());
        }
        SysUserDTO userDTO = service.getByUsername(userInfo.getUsername());
        if (Objects.isNull(userDTO) || !Objects.equals(userDTO.getPassword(), userInfo.getPassword())) {
            throw new BusinessException("登录失败：请检查用户名或者密码");
        }
        return jwtService.generateTokenDTO(userDTO.getId());
    }

    private void validateCaptcha(CaptchaType captchaType, String captchaUUID, String targetCaptcha) {
        CaptchaService captchaService = captchaServices.stream()
                .filter(service -> service.type() == captchaType)
                .findFirst()
                .orElseThrow();
        captchaService.validateCaptcha(CaptchaScene.LOGIN, captchaUUID, targetCaptcha);
    }

}
