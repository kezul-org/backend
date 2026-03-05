package com.kezul.backend.auth.domain.model.dto;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

/**
 * 소셜 로그인 제공자로부터 받아온 사용자 정보를 담는 공통 DTO.
 * 각 {@link com.kezul.backend.auth.application.port.out.OauthClient} 구현체가
 * 제공자 API 응답을 이 record로 변환하여 반환합니다.
 */
public record OauthUserInfo(
        OauthProvider provider,
        String oauthId) {
}
