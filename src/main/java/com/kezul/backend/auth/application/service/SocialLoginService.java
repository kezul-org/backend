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
    // <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 5/5)
    // UserPort -> UserFinder 로 변경">
    // 전체 흐름: auth 모듈의 Service 빈을 생성할 때, user 모듈의 내부 구현체(UserPort)가 아닌 공개
    // 인터페이스(UserFinder)를 주입받도록 의존성을 교체합니다.
    // 왜? auth 모듈은 user 내부 계층인 UserPort 대신 공개된 UserFinder만 의존해야 합니다.
    // 필요한 import: com.kezul.backend.user.UserFinder
    // </editor-fold>
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
        // <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 5/5)
        // userPort 직접 조회/생성 로직 -> userFinder 사용 로직으로 변경">
        // 전체 흐름: 소셜 로그인 인증 코드로 획득한 유저 정보를 기반으로, 우리 시스템 내부의 유저를 조회하거나 신규 가입시키는 핵심 비즈니스
        // 로직입니다.
        // 왜? User 엔티티 대신 SocialUser DTO를 반환받아 사용함으로써 user 내부 구조와 결합을 끊습니다.
        // 힌트: userFinder.findByOauthIdAndProvider() 후 orElseGet(() ->
        // userFinder.createOauthUser(...)) 로 변경
        // </editor-fold>
        SocialUser user = userFinder.findByOauthIdAndOauthProvider(oauthUserInfo.oauthId(), oauthUserInfo.provider())
                .orElseGet(() -> userFinder.createUser(oauthUserInfo.oauthId(), oauthUserInfo.provider()));
        // <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 5/5)
        // user.getId(), user.getRole().name() -> SocialUser의 id, roleName 필드 사용으로 변경">
        // 전체 흐름: 토큰 생성 시에 Entity 필드가 아닌 통신용 DTO 객체의 필드를 사용하도록 변경합니다.
        // 힌트: user.getId() -> socialUser.id(), user.getRole().name() ->
        // socialUser.roleName()
        // </editor-fold>
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
