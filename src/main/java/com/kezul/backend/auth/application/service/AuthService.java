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
import com.kezul.backend.auth.exception.AuthErrorCode;
import com.kezul.backend.auth.exception.AuthException;
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

        if (!jwtPort.validateToken(token)) {
            throw new AuthException(AuthErrorCode.INVALID_TOKEN);
        }

        RefreshToken savedToken = refreshTokenPort.findByTokenValue(token)
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_TOKEN));

        Long userId = jwtPort.getUserId(token);
        String role = jwtPort.getRole(token);

        TokenPair newTokens = jwtPort.generateTokenPair(userId, role);

        Instant newExpiresAt = jwtPort.getExpirationTime(newTokens.refreshToken());
        savedToken.updateToken(newTokens.refreshToken(), newExpiresAt);

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
