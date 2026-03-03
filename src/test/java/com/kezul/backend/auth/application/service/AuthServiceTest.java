package com.kezul.backend.auth.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.JwtPort;
import com.kezul.backend.auth.application.port.out.RefreshTokenPort;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.domain.model.entity.RefreshToken;
import com.kezul.backend.global.error.AppException;
import com.kezul.backend.global.error.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private JwtPort jwtPort;

    @Mock
    private RefreshTokenPort refreshTokenPort;

    @Test
    @DisplayName("토큰 재발급(Reissue) 성공 - RTR 적용 및 엔티티 갱신 검증")
    void reissue_Success() {
        // given
        String oldToken = "old-refresh-token";
        String newToken = "new-refresh-token";
        Long userId = 1L;
        String role = "USER";
        TokenReissueCommand command = new TokenReissueCommand(oldToken, "iPhone");

        RefreshToken savedToken = RefreshToken.builder()
                .userId(userId)
                .tokenValue(oldToken)
                .deviceInfo("iPhone")
                .expiresAt(Instant.now().plusSeconds(100))
                .build();
        TokenPair newMockTokens = new TokenPair("new-access", newToken);

        given(jwtPort.validateToken(oldToken)).willReturn(true);
        given(refreshTokenPort.findByTokenValue(oldToken)).willReturn(Optional.of(savedToken));
        given(jwtPort.getUserId(oldToken)).willReturn(userId);
        given(jwtPort.getRole(oldToken)).willReturn(role);
        given(jwtPort.generateTokenPair(userId, role)).willReturn(newMockTokens);
        given(jwtPort.getExpirationTime(newToken)).willReturn(Instant.now().plusSeconds(864000));

        // when
        TokenPair resultTokens = authService.reissue(command);

        // then
        assertThat(resultTokens.refreshToken()).isEqualTo(newToken);
        assertThat(savedToken.getTokenValue()).isEqualTo(newToken); // 엔티티 자체가 갱신되었는지 확인
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 서명이 올바르지 않거나 만료된 토큰")
    void reissue_Fail_InvalidSignatureOrExpired() {
        // given
        TokenReissueCommand command = new TokenReissueCommand("invalid-token", "iPhone");
        given(jwtPort.validateToken(anyString())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 화이트리스트(DB)에 없는 토큰")
    void reissue_Fail_TokenNotInDb() {
        // given
        TokenReissueCommand command = new TokenReissueCommand("valid-token-but-not-in-db", "iPhone");
        given(jwtPort.validateToken(anyString())).willReturn(true);
        given(refreshTokenPort.findByTokenValue(anyString())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.reissue(command))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("기기 로그아웃 성공 - 특정 토큰 삭제 호출")
    void logout_Success() {
        // given
        String tokenToLogout = "refresh-token-123";

        // when
        authService.logout(tokenToLogout);

        // then
        verify(refreshTokenPort, times(1)).deleteByTokenValue(tokenToLogout);
    }

    @Test
    @DisplayName("모든 기기 로그아웃 성공 - 해당 유저의 모든 토큰 삭제 호출")
    void logoutAllDevices_Success() {
        // given
        Long userId = 1L;

        // when
        authService.logoutAllDevices(userId);

        // then
        verify(refreshTokenPort, times(1)).deleteByUserId(userId);
    }
}
