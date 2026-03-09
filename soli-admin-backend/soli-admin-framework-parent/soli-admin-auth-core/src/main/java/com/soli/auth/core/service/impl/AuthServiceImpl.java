package com.soli.auth.core.service.impl;

import java.util.Objects;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.soli.auth.api.constant.AuthConstant;
import com.soli.auth.api.dto.TokenDTO;
import com.soli.auth.api.dto.UsernamePasswordLoginDTO;
import com.soli.auth.api.service.AuthService;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.dto.SysUserDTO;
import com.soli.system.service.SysUserService;

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

    private final SysUserService sysUserService;

    private final StringRedisTemplate redisTemplate;

    @Override
    public TokenDTO loginByUsernameAndPassword(UsernamePasswordLoginDTO userInfo) throws BusinessException {
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        String captchaCode = userInfo.getCaptchaCode();
        verifyCaptcha(AuthConstant.LOGIN_CAPTCHA_PREFIX + username, captchaCode);
        SysUserDTO sysUser = sysUserService.getByUsername(username);
        if (Objects.isNull(sysUser) || !Objects.equals(sysUser.getPassword(), password)) {
            throw new BusinessException("登录失败：请检查用户名或者密码");
        }
        return null;
    }

    void verifyCaptcha(String captchaUUID, String captchaCode) throws BusinessException {
        String captcha = redisTemplate.opsForValue().get(captchaUUID);
        if (Objects.isNull(captcha) || !captcha.equalsIgnoreCase(captchaCode)) {
            throw new BusinessException("验证码错误");
        }
    }

}
