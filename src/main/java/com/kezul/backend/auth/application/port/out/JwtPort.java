package com.kezul.backend.auth.application.port.out;

import java.time.Instant;

import com.kezul.backend.auth.application.port.out.dto.TokenPair;

public interface JwtPort {
    /**
     * Access Token과 Refresh Token 쌍 발급
     */
    TokenPair generateTokenPair(Long userId, String role);

    /**
     * 특정 토큰의 유효성 검증 (서명, 만료일)
     */
    boolean validateToken(String token);

    /**
     * 토큰에서 사용자 ID(Subject) 추출
     */
    Long getUserId(String token);

    /**
     * 토큰에서 권한(Role) 추출
     */
    String getRole(String token);

    /**
     * 토큰에서 남은 만료 시간 추출 (로그아웃 시 블랙리스트용 등)
     */
    Instant getExpirationTime(String token);
}
