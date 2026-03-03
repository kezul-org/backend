package com.kezul.backend.global.config;

import com.kezul.backend.auth.adapter.in.filter.JwtAuthenticationFilter;
import com.kezul.backend.global.security.configurer.DomainSecurityConfigurer;
import com.kezul.backend.global.security.filter.ExceptionDelegatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import java.util.List;

/**
 * Spring Security 설정.
 * JWT 기반 Stateless 인증을 사용하며, 각 도메인의 {@link DomainSecurityConfigurer}가
 * 자신의 공개/접근 제어 경로를 선언합니다.
 */
@EnableMethodSecurity
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final ExceptionDelegatorFilter exceptionDelegatorFilter;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final List<DomainSecurityConfigurer> domainSecurityConfigurers;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> {
                            domainSecurityConfigurers.forEach(
                                    configurer -> configurer.configure(auth));
                            auth.requestMatchers(
                                    "/api/actuator/health",
                                    "/api/actuator/info",
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-resources/**")
                                    .permitAll()
                                    .anyRequest().authenticated();
                        })
                .addFilterBefore(exceptionDelegatorFilter, LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
