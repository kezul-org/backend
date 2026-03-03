package com.kezul.backend.auth.application.port.in;

public interface LogoutUseCase {
    /**
     * 특정 기기(디바이스)에서 로그아웃 처리 (해당 Refresh Token 삭제)
     */
    void logout(String refreshToken);

    /**
     * 사용자의 모든 기기에서 로그아웃 처리 (해당 User의 모든 Refresh Token 삭제)
     */
    void logoutAllDevices(Long userId);
}
