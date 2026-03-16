package com.soli.auth.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-09 22:14
*/
@Data
@ConfigurationProperties(prefix = "soli.auth.jwt")
public class JwtProperties {

    /** jwt密钥 */
    private String secret;

    /** token签发者 */
    private String issuer;

    /** access token 过期时间 (秒) */
    private long accessTokenExpire;

    /** refresh token 过期时间（秒）*/
    private long refreshTokenExpire;

}