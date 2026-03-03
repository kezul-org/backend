package com.kezul.backend.auth.adapter.out.jwt;

import static org.assertj.core.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.config.JwtProperties;
import com.kezul.backend.global.error.AppException;

class JwtAdapterTest {

    private JwtAdapter jwtAdapter;
    private JwtProperties jwtProperties;
    private Clock fixedClock;

    private final String SECRET_KEY = "my-32-character-ultra-secure-and-ultra-long-secret";
    private final Instant NOW = Instant.parse("2024-01-01T00:00:00Z");

    @BeforeEach
    void setUp() {
        jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("test-issuer");
        jwtProperties.setSecretKey(SECRET_KEY);
        jwtProperties.setAccessTokenExpirationMinutes(30);
        jwtProperties.setRefreshTokenExpirationDays(7);

        fixedClock = Clock.fixed(NOW, ZoneId.of("UTC"));
        jwtAdapter = new JwtAdapter(jwtProperties, fixedClock);
    }

    @Test
    @DisplayName("토큰 쌍 생성 및 기본 검증 성공")
    void generateTokenPair_And_Validate_Success() {
        // given
        Long userId = 1L;
        String role = "USER";

        // when
        TokenPair tokenPair = jwtAdapter.generateTokenPair(userId, role);

        // then
        assertThat(tokenPair).isNotNull();
        assertThat(tokenPair.accessToken()).isNotBlank();
        assertThat(tokenPair.refreshToken()).isNotBlank();

        // 생성된 토큰이 검증을 통과하는지 확인
        assertThat(jwtAdapter.validateToken(tokenPair.accessToken())).isTrue();
        assertThat(jwtAdapter.validateToken(tokenPair.refreshToken())).isTrue();
    }

    @Test
    @DisplayName("토큰 클레임(userId, role, expiration) 추출 성공")
    void extractClaims_Success() {
        // given
        Long userId = 1L;
        String role = "USER";
        TokenPair tokenPair = jwtAdapter.generateTokenPair(userId, role);
        String accessToken = tokenPair.accessToken();

        // when
        Long extractedUserId = jwtAdapter.getUserId(accessToken);
        String extractedRole = jwtAdapter.getRole(accessToken);
        Instant expirationTime = jwtAdapter.getExpirationTime(accessToken);

        // then
        assertThat(extractedUserId).isEqualTo(userId);
        assertThat(extractedRole).isEqualTo(role);

        // 엑세스 토큰은 30분 후 만료 설정됨
        Instant expectedExpiration = NOW.plusSeconds(30 * 60);
        // Date 타임 변환과정에서 밀리초 유실 가능성이 있으므로 초 단위까지만 검증
        assertThat(expirationTime.getEpochSecond()).isEqualTo(expectedExpiration.getEpochSecond());
    }

    @Test
    @DisplayName("서명이 다르거나 조작된 토큰은 validateToken 시 false 반환")
    void validateToken_Fail_InvalidSignature() {
        // given
        TokenPair tokenPair = jwtAdapter.generateTokenPair(1L, "USER");
        // 토큰의 마지막 문자를 변경하여 조작
        String invalidToken = tokenPair.accessToken() + "abc";

        // when
        boolean isValid = jwtAdapter.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 validateToken 시 false 반환")
    void validateToken_Fail_ExpiredToken() {
        // given
        Long userId = 1L;
        String role = "USER";

        // 과거 시간을 기준으로 토큰 생성 (생성 즉시 만료된 토큰)
        Instant past = NOW.minusSeconds(1000000);
        Clock pastClock = Clock.fixed(past, ZoneId.of("UTC"));
        JwtAdapter pastJwtAdapter = new JwtAdapter(jwtProperties, pastClock);
        TokenPair expiredTokenPair = pastJwtAdapter.generateTokenPair(userId, role);

        // 현재 시간을 기준으로 하는 현재 Adapter로 검증
        // when
        boolean isValid = jwtAdapter.validateToken(expiredTokenPair.accessToken());

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("유효하지 않은 토큰에서 클레임 추출 시 예외 발생")
    void extractClaims_Fail_InvalidToken() {
        // given
        String invalidToken = "obviously-invalid-token-string";

        // when & then
        assertThatThrownBy(() -> jwtAdapter.getUserId(invalidToken))
                .isInstanceOf(AppException.class);

        assertThatThrownBy(() -> jwtAdapter.getRole(invalidToken))
                .isInstanceOf(AppException.class);
    }
}
