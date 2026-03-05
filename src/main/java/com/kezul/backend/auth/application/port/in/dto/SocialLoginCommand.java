package com.kezul.backend.auth.application.port.in.dto;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

/**
 * 소셜 로그인 요청을 Service 계층에 전달하는 Command DTO.
 */
public record SocialLoginCommand(
        OauthProvider provider,
        String code,
        String deviceInfo) {
}
