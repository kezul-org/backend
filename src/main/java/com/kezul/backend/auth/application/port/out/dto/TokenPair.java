package com.kezul.backend.auth.application.port.out.dto;

import lombok.Builder;

@Builder
public record TokenPair(
        String accessToken,
        String refreshToken) {
}
