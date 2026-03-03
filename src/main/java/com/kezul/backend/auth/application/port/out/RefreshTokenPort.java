package com.kezul.backend.auth.application.port.out;

import java.util.Optional;

import com.kezul.backend.auth.domain.model.entity.RefreshToken;

public interface RefreshTokenPort {
    /**
     * 리프레시 토큰 저장 및 갱신 (Spring Data JPA save 활용)
     */
    void save(RefreshToken refreshToken);

    /**
     * 토큰 값으로 리프레시 토큰 조회
     */
    Optional<RefreshToken> findByTokenValue(String tokenValue);

    /**
     * 특정 사용자의 모든 기기 리프레시 토큰 삭제 (전체 기기 로그아웃)
     */
    void deleteByUserId(Long userId);

    /**
     * 특정 토큰 단일 삭제 (현재 기기 로그아웃 또는 사용 후 폐기)
     */
    void deleteByTokenValue(String tokenValue);
}
