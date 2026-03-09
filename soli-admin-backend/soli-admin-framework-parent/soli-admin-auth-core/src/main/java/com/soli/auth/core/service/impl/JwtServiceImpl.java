package com.soli.auth.core.service.impl;

import java.time.Instant;
import java.util.Date;

import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.soli.auth.api.dto.TokenDTO;
import com.soli.auth.api.service.JwtService;
import com.soli.auth.core.config.JwtProperties;

import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-09 22:24
*/
@EnableConfigurationProperties(JwtProperties.class)
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties properties;

    @Override
    public TokenDTO generateTokenDTO(Long userId) {
        String accessToken = generateAccessToken(userId);
        String refreshToken = generateRefreshToken(userId);
        return new TokenDTO(accessToken, refreshToken);
    }

    public String generateAccessToken(Long userId) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(properties.getAccessTokenExpire())))
                .sign(algorithm);
    }

    public String generateRefreshToken(Long userId) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        Instant now = Instant.now();
        return JWT.create()
                .withIssuer(properties.getIssuer())
                .withSubject(userId.toString())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plusSeconds(properties.getRefreshTokenExpire())))
                .withClaim("type", "refresh")
                .sign(algorithm);
    }

    public DecodedJWT parseToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(properties.getSecret());
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(properties.getIssuer())
                .build();
        return verifier.verify(token);
    }

    public Long getUserId(String token) {
        DecodedJWT jwt = parseToken(token);
        return Long.valueOf(jwt.getSubject());
    }

}