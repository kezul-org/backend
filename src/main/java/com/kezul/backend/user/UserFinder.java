package com.kezul.backend.user;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

import java.util.Optional;

/**
 * user 모듈이 외부(auth 모듈 등)에 허가한 공식 통신 API 통로 (Provided Interface).
 * 내부 엔티티 및 포트 구현을 외부로 숨겨 모듈 결합도를 낮춥니다.
 */
public interface UserFinder {

    /**
     * OAuth 제공자와 OauthId 기반으로 가입된 사용자 조회
     *
     * @return 조회된 SocialUser DTO. 없으면 Optional.empty()
     */
    Optional<SocialUser> findByOauthIdAndOauthProvider(String oauthId, OauthProvider provider);

    /**
     * 신규 소셜 유저 생성 및 내부 저장
     *
     * @return 생성된 SocialUser DTO 반환
     */
    SocialUser createUser(String oauthId, OauthProvider provider);

    Optional<SocialUser> findById(Long userId);
}
