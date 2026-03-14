package com.soli.auth.api.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soli.auth.api.dto.TokenDTO;

/**
 * jwt 服务
 *
 * @author lizhengqiang
 * @since 2026-03-09 22:06
*/
public interface JwtService {

    /**
     * 生成 token 对
     *
     * @param userId 用户 ID
     * @return TokenDTO，token 对
     */
    TokenDTO generateTokenDTO(Long userId);

    /**
     * 生成 AccessToken
     *
     * @param userId 用户 ID
     * @return AccessToken
     */
    String generateAccessToken(Long userId);

    /**
     * 生成 RefreshToken
     *
     * @param userId 用户 ID
     * @return RefreshToken
     */
    String generateRefreshToken(Long userId);

    /**
     * 解析 token
     * @param token token 字符串
     * @return DecodedJWT
     */
    DecodedJWT parseToken(String token) throws JWTVerificationException;

    /**
     * 解析 token 并且获取用户 ID
     *
     * @param token token字符串
     * @return 用户 ID
     */
    Long getUserId(String token) throws JWTVerificationException;
}