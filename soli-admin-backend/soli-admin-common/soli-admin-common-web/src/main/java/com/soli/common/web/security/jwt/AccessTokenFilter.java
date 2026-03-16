package com.soli.common.web.security.jwt;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.soli.auth.api.service.JwtService;
import com.soli.common.api.exception.BusinessException;
import com.soli.system.service.sysmenu.SysMenuService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
* @author lizhengqiang
* @since 2026-03-07 0:04
*/
@RequiredArgsConstructor
public class AccessTokenFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final SysMenuService sysMenuService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = request.getHeader("Authorization");
        if (StringUtils.isBlank(accessToken)) {
            throw new BusinessException("accessToken 不能为空");
        }
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Long userId = null;
        try {
            userId = jwtService.getUserId(accessToken);
        } catch (JWTVerificationException e) {
            throw new BusinessException(e.getMessage());
        }

        Set<String> permsSet = sysMenuService.queryPermsByUserId(userId);
        List<GrantedAuthority> authorityList = AuthorityUtils.createAuthorityList(permsSet);

        AccessAuthentication authentication = new AccessAuthentication(authorityList);
        authentication.setUserId(userId);
        authentication.setAccessToken(accessToken);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

}
