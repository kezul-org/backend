package com.kezul.backend.auth.application.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.kezul.backend.user.SocialUser;
import com.kezul.backend.user.UserFinder;
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
import com.kezul.backend.user.domain.model.enums.OauthProvider;

import lombok.extern.slf4j.Slf4j;

/**
 * 소셜 로그인 전용 서비스.
 * OAuth 인가 URL 생성, code 교환, 유저 조회/생성, JWT 발급을 담당합니다.
 */
@Slf4j
@Service
public class SocialLoginService implements SocialLoginUseCase {

    private final Map<OauthProvider, OauthClient> oauthClients;
    private final UserFinder userFinder;
    private final JwtPort jwtPort;
    private final RefreshTokenPort refreshTokenPort;

    public SocialLoginService(
            List<OauthClient> oauthClientList,
            UserFinder userFinder,
            JwtPort jwtPort,
            RefreshTokenPort refreshTokenPort) {
        this.oauthClients = oauthClientList.stream()
                .collect(Collectors.toMap(OauthClient::getProvider, Function.identity()));
        this.userFinder = userFinder;
        this.jwtPort = jwtPort;
        this.refreshTokenPort = refreshTokenPort;
    }

    @Override
    @Transactional
    public TokenPair login(SocialLoginCommand command) {
        OauthClient oauthClient = getOauthClient(command.provider());
        OauthUserInfo oauthUserInfo = oauthClient.getUserInfo(command.code());

        SocialUser user = userFinder.findByOauthIdAndOauthProvider(oauthUserInfo.oauthId(), oauthUserInfo.provider())
                .orElseGet(() -> userFinder.createUser(oauthUserInfo.oauthId(), oauthUserInfo.provider()));

        TokenPair tokenPair = jwtPort.generateTokenPair(user.id(), user.roleName());

        RefreshToken refreshToken = RefreshToken.builder()
                .userId(user.id())
                .deviceInfo(command.deviceInfo())
                .tokenValue(tokenPair.refreshToken())
                .expiresAt(jwtPort.getExpirationTime(tokenPair.refreshToken()))
                .build();
        refreshTokenPort.save(refreshToken);

        return tokenPair;
    }

    private OauthClient getOauthClient(OauthProvider provider) {
        OauthClient oauthClient = oauthClients.get(provider);
        if (oauthClient == null) {
            throw new AuthException(AuthErrorCode.UNSUPPORTED_PROVIDER);
        }
        return oauthClient;
    }

}
