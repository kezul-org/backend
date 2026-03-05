package com.kezul.backend.auth.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kezul.backend.auth.application.port.in.SocialLoginUseCase;
import com.kezul.backend.auth.application.port.in.dto.SocialLoginCommand;
import com.kezul.backend.auth.application.port.out.JwtPort;
import com.kezul.backend.auth.application.port.out.OauthClient;
import com.kezul.backend.auth.application.port.out.RefreshTokenPort;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;
import com.kezul.backend.auth.domain.model.dto.OauthUserInfo;
import com.kezul.backend.auth.domain.model.entity.RefreshToken;
import com.kezul.backend.auth.exception.AuthErrorCode;
import com.kezul.backend.auth.exception.AuthException;
import com.kezul.backend.user.application.port.out.UserPort;
import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import com.kezul.backend.user.domain.model.enums.Role;

import lombok.extern.slf4j.Slf4j;

/**
 * 소셜 로그인 전용 서비스.
 * OAuth 인가 URL 생성, code 교환, 유저 조회/생성, JWT 발급을 담당합니다.
 */
@Slf4j
@Service
public class SocialLoginService implements SocialLoginUseCase {

    private final Map<OauthProvider, OauthClient> oauthClients;
    private final UserPort userPort;
    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    public SocialLoginService(
            List<OauthClient> oauthClientList,
            UserPort userPort,
            JwtPort jwtPort,
            RefreshTokenPort refreshTokenPort) {
        this.oauthClients = oauthClientList.stream()
                .collect(Collectors.toMap(OauthClient::getProvider, Function.identity()));
        this.userPort = userPort;
        this.jwtPort = jwtPort;
        this.refreshTokenPort = refreshTokenPort;
    }

    @Override
    @Transactional
    public TokenPair login(SocialLoginCommand command) {
        OauthClient oauthClient = getOauthClient(command.provider());
        OauthUserInfo oauthUserInfo = oauthClient.getUserInfo(command.code());
        User user = userPort.findByOauthIdAndOauthProvider(oauthUserInfo.oauthId(), oauthUserInfo.provider())
                .orElseGet(() -> createUser(oauthUserInfo));
        TokenPair tokenPair = jwtPort.generateTokenPair(user.getId(), user.getRole().name());

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.getId())
                .deviceInfo(command.deviceInfo())
                .tokenValue(tokenPair.refreshToken())
                .expiresAt(jwtPort.getExpirationTime(tokenPair.refreshToken()))
                .build();
        refreshTokenPort.save(refreshToken);

        return tokenPair;
    }

    /**
     * 신규 유저를 생성합니다.
     * 닉네임은 "유저_" + UUID 앞 8자리로 자동 생성합니다.
     */
    private User createUser(OauthUserInfo oauthUserInfo) {
        OauthProvider provider = oauthUserInfo.provider();
        String oauthId = oauthUserInfo.oauthId();
        User user = User.builder()
                .oauthProvider(provider)
                .oauthId(oauthId)
                .nickname("temp")
                .role(Role.USER)
                .build();
        return userPort.save(user);
    }

    private OauthClient getOauthClient(OauthProvider provider) {
        OauthClient oauthClient = oauthClients.get(provider);
        if (oauthClient == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }
        return oauthClient;
    }

}
