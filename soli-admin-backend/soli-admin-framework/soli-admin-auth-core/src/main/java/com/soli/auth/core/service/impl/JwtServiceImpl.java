package com.soli.auth.core.service.impl;

import java.time.Instant;
import java.util.Date;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soli.auth.api.service.JwtService;
import com.soli.auth.api.service.auth.TokenDTO;
import com.soli.auth.core.config.JwtProperties;

import lombok.RequiredArgsConstructor;

/**
 * 令牌服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-09 22:24
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties properties;

    /**
     * 生成不带公司信息的令牌对象
     *
     * @param userId 用户 ID
     * @return 令牌对象
     */
    @Override
    public TokenDTO generateTokenDTO(Long userId) {
        return generateTokenDTO(userId, null);
    }

    /**
     * 生成令牌对象
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 令牌对象
     */
    @Override
    public TokenDTO generateTokenDTO(Long userId, Long companyId) {
        String accessToken = generateAccessToken(userId, companyId);
        String refreshToken = generateRefreshToken(userId, companyId);
        return new TokenDTO(accessToken, refreshToken);
    }

    /**
     * 生成不带公司信息的访问令牌
     *
     * @param userId 用户 ID
     * @return 访问令牌
     */
    @Override
    public String generateAccessToken(Long userId) {
        return generateAccessToken(userId, null);
    }

    /**
     * 生成访问令牌
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 访问令牌
     */
    @Override
    public String generateAccessToken(Long userId, Long companyId) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        Instant now = Instant.now();
        var builder = JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(properties.getAccessTokenExpire())));
        if (companyId != null) {
            builder.withClaim("companyId", companyId);
        }
        return builder.sign(algorithm);
    }

    /**
     * 生成不带公司信息的刷新令牌
     *
     * @param userId 用户 ID
     * @return 刷新令牌
     */
    @Override
    public String generateRefreshToken(Long userId) {
        return generateRefreshToken(userId, null);
    }

    /**
     * 生成刷新令牌
     *
     * @param userId 用户 ID
     * @param companyId 公司 ID
     * @return 刷新令牌
     */
    @Override
    public String generateRefreshToken(Long userId, Long companyId) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        Instant now = Instant.now();
        var builder = JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(properties.getRefreshTokenExpire())))
                .withClaim("type", "refresh");
        if (companyId != null) {
            builder.withClaim("companyId", companyId);
        }
        return builder.sign(algorithm);
    }

    /**
     * 解析令牌
     *
     * @param token 令牌字符串
     * @return 解析后的 JWT 对象
     * @throws JWTVerificationException JWT 校验异常
     */
    @Override
    public DecodedJWT parseToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build();
        return verifier.verify(token);
    }

    /**
     * 从令牌中解析用户 ID
     *
     * @param token 令牌字符串
     * @return 用户 ID
     * @throws JWTVerificationException JWT 校验异常
     */
    @Override
    public Long getUserId(String token) throws JWTVerificationException {
        DecodedJWT jwt = parseToken(token);
        return Long.valueOf(jwt.getSubject());
    }

    /**
     * 从令牌中解析公司 ID
     *
     * @param token 令牌字符串
     * @return 公司 ID
     * @throws JWTVerificationException JWT 校验异常
     */
    @Override
    public Long getCompanyId(String token) throws JWTVerificationException {
        DecodedJWT jwt = parseToken(token);
        return jwt.getClaim("companyId").isNull() ? null : jwt.getClaim("companyId").asLong();
    }

}
