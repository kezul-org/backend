package com.kezul.backend.auth.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kezul.backend.auth.adapter.in.web.dto.LogoutRequest;
import com.kezul.backend.auth.adapter.in.web.dto.SocialLoginRequest;
import com.kezul.backend.auth.adapter.in.web.dto.TokenReissueRequest;
import com.kezul.backend.auth.adapter.in.web.dto.TokenResponse;
import com.kezul.backend.auth.application.port.in.LogoutUseCase;
import com.kezul.backend.auth.application.port.in.SocialLoginUseCase;
import com.kezul.backend.auth.application.port.in.TokenReissueUseCase;
import com.kezul.backend.auth.application.port.in.dto.SocialLoginCommand;
import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증(소셜 로그인, 토큰 재발급, 로그아웃) API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenReissueUseCase tokenReissueUseCase;
    private final LogoutUseCase logoutUseCase;
    private final SocialLoginUseCase socialLoginUseCase;

    // =========================================================================
    // Social Login
    // =========================================================================

    @Operation(summary = "[Auth] 소셜 로그인", description = "프론트 SDK가 획득한 인가 코드(code)와 소셜 제공자(provider)를 전달하면, "
            + "백엔드가 해당 소셜 서버에 검증 후 JWT를 발급합니다.")
    @PostMapping("/social-login")
    public ResponseEntity<CommonResponse<TokenResponse>> socialLogin(
            @Valid @RequestBody SocialLoginRequest request,
            @RequestHeader(value = "User-Agent", defaultValue = "unknown") String userAgent) {
        SocialLoginCommand command = new SocialLoginCommand(request.provider(), request.code(), userAgent);
        TokenPair tokenPair = socialLoginUseCase.login(command);
        return ResponseEntity.ok(CommonResponse.success(TokenResponse.from(tokenPair)));
    }

    // =========================================================================
    // Token Management
    // =========================================================================

    @Operation(summary = "[Auth] 토큰 재발급 (RTR)", description = "만료된 Access Token을 갱신하기 위해 Refresh Token을 전송합니다.<br>"
            + "RTR(Refresh Token Rotation) 정책에 의해 Refresh Token도 함께 새것으로 갱신됩니다.")
    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<TokenResponse>> reissue(
            @Valid @RequestBody TokenReissueRequest request) {
        TokenReissueCommand command = new TokenReissueCommand(
                request.refreshToken(),
                request.deviceInfo());

        TokenPair newTokenPair = tokenReissueUseCase.reissue(command);
        return ResponseEntity.ok(CommonResponse.success(TokenResponse.from(newTokenPair)));
    }

    @Operation(summary = "[Auth] 로그아웃", description = "현재 기기에서 로그아웃합니다. DB에 저장된 해당 Refresh Token을 찾아 영구 폐기합니다.")
    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Void>> logout(
            @Valid @RequestBody LogoutRequest request) {
        logoutUseCase.logout(request.refreshToken());
        return ResponseEntity.ok(CommonResponse.success(null));
    }
}
