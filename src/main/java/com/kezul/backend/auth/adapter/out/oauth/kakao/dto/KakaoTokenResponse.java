package com.kezul.backend.auth.adapter.out.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 토큰 API 응답 DTO.
 * POST https://kauth.kakao.com/oauth/token 의 응답을 매핑합니다.
 */
public record KakaoTokenResponse(
        @JsonProperty("access_token") String accessToken) {
}
