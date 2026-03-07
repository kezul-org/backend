package com.kezul.backend.auth.application.port.out;

import com.kezul.backend.auth.domain.model.dto.OauthUserInfo;
import com.kezul.backend.user.domain.model.enums.OauthProvider;

/**
 * 소셜 로그인 제공자별 OAuth 클라이언트 인터페이스 (Output Port).
 * Application Service는 이 인터페이스만 의존하고, 실제 HTTP 호출은 Adapter가 담당합니다.
 */
public interface OauthClient {

    /** 이 클라이언트가 담당하는 소셜 제공자 식별자 반환. */
    OauthProvider getProvider();

    /**
     * Authorization Code를 받아 제공자 API로 유저 정보를 조회합니다.
     * 내부적으로 code → access_token 교환 → 유저 정보 조회 2단계를 수행합니다.
     */
    OauthUserInfo getUserInfo(String code);
}
