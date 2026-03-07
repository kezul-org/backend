package com.kezul.backend.global.security.configurer;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

/**
 * 도메인별 URL 보안 규칙을 SecurityConfig에 위임하는 인터페이스.
 * 각 도메인 모듈은 이 인터페이스를 구현하여 자신의 공개/접근 제어 경로를 선언합니다.
 */
public interface DomainSecurityConfigurer {

    void configure(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize);
}
