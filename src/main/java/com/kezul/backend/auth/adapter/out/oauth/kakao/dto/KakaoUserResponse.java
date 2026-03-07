package com.kezul.backend.auth.adapter.out.oauth.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 유저 정보 API 응답 DTO.
 * GET https://kapi.kakao.com/v2/user/me 의 응답에서 필요한 필드만 매핑합니다.
 */
public record KakaoUserResponse(
        @JsonProperty("id") Long id) {
}
