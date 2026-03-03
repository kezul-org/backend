package com.kezul.backend.auth.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그아웃 요청 객체")
public record LogoutRequest(
        @Schema(description = "폐기할 현재 기기의 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...") @NotBlank(message = "Refresh Token은 필수입니다.") String refreshToken) {
}
