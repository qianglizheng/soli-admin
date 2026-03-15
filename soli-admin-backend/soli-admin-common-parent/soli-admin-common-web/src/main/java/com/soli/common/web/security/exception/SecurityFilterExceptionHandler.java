package com.soli.common.web.security.exception;

import java.io.IOException;
import java.io.PrintWriter;

import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.ApiResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

/**
 * 处理 springSecurity过滤器的抛出的异常
 *
 * @author lizhengqiang
 * @since 2026-03-14 11:57
*/
public class SecurityFilterExceptionHandler extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (BusinessException e) {
            ObjectMapper mapper = new ObjectMapper();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.write(mapper.writeValueAsString(ApiResponse.fail(e.getMessage())));
            writer.flush();
            writer.close();
        }
    }

}