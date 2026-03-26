package com.soli.auth.api.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soli.auth.api.service.auth.TokenDTO;

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
     * 生成 token 对
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return TokenDTO，token 对
     */
    TokenDTO generateTokenDTO(Long userId, Long companyId);

    /**
     * 生成 AccessToken
     *
     * @param userId 用户 ID
     * @return AccessToken
     */
    String generateAccessToken(Long userId);

    /**
     * 生成 AccessToken
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return AccessToken
     */
    String generateAccessToken(Long userId, Long companyId);

    /**
     * 生成 RefreshToken
     *
     * @param userId 用户 ID
     * @return RefreshToken
     */
    String generateRefreshToken(Long userId);

    /**
     * 生成 RefreshToken
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return RefreshToken
     */
    String generateRefreshToken(Long userId, Long companyId);

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

    /**
     * 解析 token 并且获取当前公司 ID
     *
     * @param token token 字符串
     * @return 公司 ID
     */
    Long getCompanyId(String token) throws JWTVerificationException;
}
