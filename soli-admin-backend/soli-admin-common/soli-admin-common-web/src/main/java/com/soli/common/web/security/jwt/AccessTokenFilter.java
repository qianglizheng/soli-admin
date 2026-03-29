package com.soli.common.web.security.jwt;

import com.soli.common.core.security.CompanyContextHolder;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.soli.auth.api.service.JwtService;
import com.soli.common.api.exception.BusinessException;

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
        Long companyId = null;
        try {
            userId = jwtService.getUserId(accessToken);
            companyId = jwtService.getCompanyId(accessToken);
        } catch (JWTVerificationException e) {
            throw new BusinessException(e.getMessage());
        }

        List<GrantedAuthority> authorityList = List.of();
        AccessAuthentication authentication = new AccessAuthentication(authorityList);
        authentication.setUserId(userId);
        authentication.setCompanyId(companyId);
        authentication.setAccessToken(accessToken);
        authentication.setDetails(companyId);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        CompanyContextHolder.setCurrentCompanyId(companyId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            CompanyContextHolder.clear();
        }
    }

}
