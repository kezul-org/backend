package com.kezul.backend.auth.application.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kezul.backend.auth.application.port.in.LogoutUseCase;
import com.kezul.backend.auth.application.port.in.TokenReissueUseCase;
import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.JwtPort;
import com.kezul.backend.auth.application.port.out.RefreshTokenPort;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.domain.model.entity.RefreshToken;
import com.kezul.backend.global.error.AppException;
import com.kezul.backend.global.error.ErrorCode;
import com.kezul.backend.global.logging.AppLog;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements TokenReissueUseCase, LogoutUseCase {

    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    @Override
    @Transactional
    public TokenPair reissue(TokenReissueCommand command) {
        String token = command.refreshToken();

        // 1. 서명 및 만료일 검증
        if (!jwtPort.validateToken(token)) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        // 2. DB에서 토큰 화인 (화이트리스트 방식)
        RefreshToken savedToken = refreshTokenPort.findByTokenValue(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        // 3. 토큰에서 정보 추출
        Long userId = jwtPort.getUserId(token);
        String role = jwtPort.getRole(token);

        // 4. 새로운 토큰 쌍 발급 (RTR: Refresh Token Rotation)
        TokenPair newTokens = jwtPort.generateTokenPair(userId, role);

        // 5. 만료 시간 계산 및 기존 엔티티 갱신 (더티 체킹)
        Instant newExpiresAt = jwtPort.getExpirationTime(newTokens.refreshToken());
        savedToken.updateToken(newTokens.refreshToken(), newExpiresAt);
        // refreshTokenPort.save(savedToken); // 변경 감지가 발생하지만 명시적으로 호출할 수도 있음

        AppLog.authReissued(log, userId);
        return newTokens;
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenPort.deleteByTokenValue(refreshToken);
        }
    }

    @Override
    @Transactional
    public void logoutAllDevices(Long userId) {
        refreshTokenPort.deleteByUserId(userId);
    }
}
