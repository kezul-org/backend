package com.kezul.backend.auth.adapter.in.web.dto;

import com.kezul.backend.auth.application.port.out.dto.TokenPair;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "토큰 발급 응답 객체 (Access / Refresh)")
public record TokenResponse(
        @Schema(description = "새로 발급된 Access Token (수명: 30분)", example = "eyJhbGciOiJIUzI1NiJ9...") String accessToken,

        @Schema(description = "새로 발급된 Refresh Token (수명: 90일). RTR 정책에 따라 계속 갱신됩니다.", example = "eyJhbGciOiJIUzI1NiJ9...") String refreshToken) {
    public static TokenResponse from(TokenPair tokenPair) {
        return new TokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
    }
}
