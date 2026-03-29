package com.soli.auth.api.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soli.auth.api.service.auth.TokenDTO;

/**
 * JWT 服务
 *
 * @author lizhengqiang
 * @since 2026-03-09 22:06
 */
public interface JwtService {

    /**
     * 生成令牌对象
     *
     * @param userId 用户 ID
     * @return 令牌对象
     */
    TokenDTO generateTokenDTO(Long userId);

    /**
     * 生成令牌对象
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return 令牌对象
     */
    TokenDTO generateTokenDTO(Long userId, Long companyId);

    /**
     * 生成访问令牌
     *
     * @param userId 用户 ID
     * @return 访问令牌
     */
    String generateAccessToken(Long userId);

    /**
     * 生成访问令牌
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return 访问令牌
     */
    String generateAccessToken(Long userId, Long companyId);

    /**
     * 生成刷新令牌
     *
     * @param userId 用户 ID
     * @return 刷新令牌
     */
    String generateRefreshToken(Long userId);

    /**
     * 生成刷新令牌
     *
     * @param userId 用户 ID
     * @param companyId 当前公司 ID
     * @return 刷新令牌
     */
    String generateRefreshToken(Long userId, Long companyId);

    /**
     * 解析令牌
     *
     * @param token 令牌字符串
     * @return 解析后的 JWT 对象
     * @throws JWTVerificationException JWT 校验异常
     */
    DecodedJWT parseToken(String token) throws JWTVerificationException;

    /**
     * 解析令牌并获取用户 ID
     *
     * @param token 令牌字符串
     * @return 用户 ID
     * @throws JWTVerificationException JWT 校验异常
     */
    Long getUserId(String token) throws JWTVerificationException;

    /**
     * 解析令牌并获取公司 ID
     *
     * @param token 令牌字符串
     * @return 公司 ID
     * @throws JWTVerificationException JWT 校验异常
     */
    Long getCompanyId(String token) throws JWTVerificationException;
}
