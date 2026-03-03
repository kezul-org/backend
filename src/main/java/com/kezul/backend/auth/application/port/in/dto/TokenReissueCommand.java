package com.kezul.backend.auth.application.port.in.dto;

public record TokenReissueCommand(
        String refreshToken,
        String deviceInfo) {
}
