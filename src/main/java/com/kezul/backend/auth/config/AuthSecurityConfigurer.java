package com.kezul.backend.auth.config;

import com.kezul.backend.global.security.configurer.DomainSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

/**
 * Auth 도메인 URL 보안 규칙.
 * 로그인, 로그아웃, 토큰 재발급 엔드포인트는 인증 없이 접근 가능합니다.
 */
@Component
public class AuthSecurityConfigurer implements DomainSecurityConfigurer {

    @Override
    public void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        authorize.requestMatchers("/api/v1/auth/**").permitAll();
    }
}
