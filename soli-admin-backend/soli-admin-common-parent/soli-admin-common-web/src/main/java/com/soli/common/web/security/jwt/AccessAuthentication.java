package com.soli.common.web.security.jwt;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lizhengqiang
 * @since 2026-03-13 22:45
*/
@Getter
@Setter
public class AccessAuthentication extends AbstractAuthenticationToken {

    private String accessToken;

    private Long userId;

    public AccessAuthentication(@Nullable Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public AccessAuthentication() {
        this(null);
    }

    @Override
    public @Nullable Object getCredentials() {
        return isAuthenticated() ? null : accessToken;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return isAuthenticated() ? userId : accessToken;
    }
}