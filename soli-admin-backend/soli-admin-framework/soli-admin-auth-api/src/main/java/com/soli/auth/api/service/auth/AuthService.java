package com.soli.auth.api.service.auth;

import com.soli.common.api.exception.BusinessException;

/**
 * 认证授权服务
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:45
 */
public interface AuthService {

    /**
     * 使用用户名和密码登录
     *
     * @param userInfo 用户登录信息
     * @return 令牌对象
     * @throws BusinessException 业务异常
     */
    TokenDTO loginByUsernameAndPassword(UsernamePasswordLoginDTO userInfo) throws BusinessException;

}
