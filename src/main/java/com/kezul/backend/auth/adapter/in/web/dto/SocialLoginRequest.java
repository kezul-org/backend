package com.kezul.backend.auth.adapter.in.web.dto;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "소셜 로그인 요청")
public record SocialLoginRequest(

        @NotNull @Schema(description = "소셜 로그인 제공자 (KAKAO, NAVER, GOOGLE, APPLE)", example = "KAKAO") OauthProvider provider,

        @NotBlank @Schema(description = "프론트 SDK가 발급받은 인가 코드", example = "abc123xyz...") String code) {
}
