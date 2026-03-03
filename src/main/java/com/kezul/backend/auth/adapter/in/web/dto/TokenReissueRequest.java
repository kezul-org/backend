package com.kezul.backend.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급 요청 객체")
public record TokenReissueRequest(
        @Schema(description = "기존에 발급받은 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...") @NotBlank(message = "Refresh Token은 필수입니다.") String refreshToken,

        @Schema(description = "요청 기기 정보 (User-Agent 또는 기기 식별자)", example = "iPhone15,2 / iOS 17.1") String deviceInfo) {
}
