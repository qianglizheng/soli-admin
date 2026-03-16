package com.soli.auth.api.service;

import com.soli.auth.api.dto.TokenDTO;
import com.soli.auth.api.dto.UsernamePasswordLoginDTO;
import com.soli.common.api.exception.BusinessException;

/**
 * 认证授权服务
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:45
 */
public interface AuthService {

    /**
     * 用户名 + 密码登录
     *
     * @param userInfo 用户密码传输对象
     * @return Token 对
     * @throws BusinessException 业务异常
     */
    TokenDTO loginByUsernameAndPassword(UsernamePasswordLoginDTO userInfo) throws BusinessException;
    
}