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
import com.soli.auth.api.service.auth.TokenDTO;
import com.soli.auth.api.service.JwtService;
import com.soli.auth.core.config.JwtProperties;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-09 22:24
*/
@Service
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties properties;

    @Override
    public TokenDTO generateTokenDTO(Long userId) {
        return generateTokenDTO(userId, null);
    }

    @Override
    public TokenDTO generateTokenDTO(Long userId, Long companyId) {
        String accessToken = generateAccessToken(userId, companyId);
        String refreshToken = generateRefreshToken(userId, companyId);
        return new TokenDTO(accessToken, refreshToken);
    }

    @Override
    public String generateAccessToken(Long userId) {
        return generateAccessToken(userId, null);
    }

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

    @Override
    public String generateRefreshToken(Long userId) {
        return generateRefreshToken(userId, null);
    }

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

    @Override
    public DecodedJWT parseToken(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build();
        return verifier.verify(token);
    }

    public Long getUserId(String token) throws JWTVerificationException{
        DecodedJWT jwt = parseToken(token);
        return Long.valueOf(jwt.getSubject());
    }

    @Override
    public Long getCompanyId(String token) throws JWTVerificationException {
        DecodedJWT jwt = parseToken(token);
        return jwt.getClaim("companyId").isNull() ? null : jwt.getClaim("companyId").asLong();
    }

}
