package com.kezul.backend.auth.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kezul.backend.auth.adapter.in.web.dto.LogoutRequest;
import com.kezul.backend.auth.adapter.in.web.dto.TokenReissueRequest;
import com.kezul.backend.auth.adapter.in.web.dto.TokenResponse;
import com.kezul.backend.auth.application.port.in.LogoutUseCase;
import com.kezul.backend.auth.application.port.in.TokenReissueUseCase;
import com.kezul.backend.auth.application.port.in.dto.TokenReissueCommand;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.global.response.CommonResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증(로그인, 로그아웃, 토큰 재발급) API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenReissueUseCase tokenReissueUseCase;
    private final LogoutUseCase logoutUseCase;

    @Operation(summary = "[Auth] 토큰 재발급 (RTR)", description = "만료된 Access Token을 갱신하기 위해 Refresh Token을 전송합니다.<br>RTR(Refresh Token Rotation) 정책에 의해 Refresh Token도 함께 새것으로 갱신됩니다.")
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
