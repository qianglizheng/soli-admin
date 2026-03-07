package com.soli.common.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;

/**
* @author lizhengqiang
* @since 2026-03-06 22:20
*/
@Configuration
@EnableWebSecurity(debug = true)
@AllArgsConstructor
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain loginSecurity(HttpSecurity http) throws Exception {
        HttpSecurity security = common(http)
                .addFilterBefore(new MyFilter("/login"), UsernamePasswordAuthenticationFilter.class);

        security
                .securityMatcher("/login/**")
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated()
                );

        return security.build();
    }

    @Bean
    @Order
    public SecurityFilterChain defaultApiFilterChain(HttpSecurity http) throws Exception {
        common(http);
        http
                .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }

    /**
     * 公共配置方法
     */
    private HttpSecurity common(HttpSecurity http) {
        return http
                .cors(AbstractHttpConfigurer::disable) // 或者自定义 Cors 配置
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );
    }

}
