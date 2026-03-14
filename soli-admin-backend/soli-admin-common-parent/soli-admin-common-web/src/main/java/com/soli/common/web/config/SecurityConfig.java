package com.soli.common.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import com.soli.auth.api.service.JwtService;
import com.soli.common.web.security.exception.SecurityFilterExceptionHandler;
import com.soli.common.web.security.jwt.AccessTokenFilter;
import com.soli.system.service.sysmenu.SysMenuService;

import lombok.AllArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-06 22:20
 */
@Configuration
@AllArgsConstructor
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AccessDeniedHandler accessDeniedHandler;

    private final JwtService jwtService;

    private final SysMenuService sysMenuService;

    private static final String[] SWAGGER_WHITELIST = {
            "/doc.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/.well-known/appspecific/com.chrome.devtools.json"
    };

    private static final String[] AUTH_WHITELIST = {
            "/auth/**"
    };

    /**
     * 放行 swagger
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http) {
        commonSetting(http);
        return http.securityMatcher(SWAGGER_WHITELIST).authorizeHttpRequests(authorize ->
                authorize.anyRequest().permitAll()).build();
    }

    /**
     * 放行认证接口
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    @Order(2)
    public SecurityFilterChain authFilterChain(HttpSecurity http) {
        commonSetting(http);
        return http.securityMatcher(AUTH_WHITELIST).authorizeHttpRequests(authorize ->
                authorize.anyRequest().permitAll()).build();
    }

    /**
     * 拦截所有请求，并校验 accessToken
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     */
    @Bean
    @Order(3)
    public SecurityFilterChain defaultSecurity(HttpSecurity http) {
        commonSetting(http);
        http.addFilterBefore(new AccessTokenFilter(jwtService, sysMenuService), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new SecurityFilterExceptionHandler(), SecurityContextHolderFilter.class);
        http.securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
        return http.build();
    }

    /**
     * 公共配置
     *
     * @param http HttpSecurity
     */
    private void commonSetting(HttpSecurity http) {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                );
    }

}
