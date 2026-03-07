package com.kezul.backend.global.security.filter;

import jakarta.servlet.Filter;

/**
 * 전역 SecurityFilterChain 에 등록될 인증 필터의 마커 인터페이스.
 * global 모듈이 특정 도메인(auth)의 구체적인 필터 구현체(JwtAuthenticationFilter 등)에
 * 직접 의존하지 않도록 의존성을 역전시키기 위해 사용됩니다.
 */
public interface PlatformAuthenticationFilter extends Filter {
}
