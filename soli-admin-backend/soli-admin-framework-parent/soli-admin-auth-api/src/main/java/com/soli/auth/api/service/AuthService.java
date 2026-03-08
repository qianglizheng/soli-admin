package com.soli.auth.api.service;

import com.soli.auth.api.vo.TokenVO;

/**
 * 认证授权服务
 *
 * @author lizhengqiang
 * @since 2026-03-08 15:45
 */
public interface AuthService {

    TokenVO loginByUsernameAndPassword(String username, String password);

}
