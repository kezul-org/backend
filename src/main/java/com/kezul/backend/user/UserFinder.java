package com.kezul.backend.user;

import com.kezul.backend.user.domain.model.enums.OauthProvider;

import java.util.Optional;

// <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 3/5)
// UserFinder 인터페이스 생성">
// 전체 흐름: user 모듈이 외부(auth 모듈)의 요청을 처리하기 위해 열어두는 허가된 단일 출입구(Provided Interface)를
// 정의하는 단계입니다.
// 왜? auth 모듈이 유저 정보를 조회/생성할 때 사용할 Provided Interface(공식 API)를 정의합니다.
// 힌트:
// 1) Optional<SocialUser> findByOauthIdAndProvider(String oauthId,
// OauthProvider provider);
// 2) SocialUser createOauthUser(OauthProvider provider, String oauthId, String
// nickname);
// 두 개의 메서드를 선언하세요.
// </editor-fold>
public interface UserFinder {
    Optional<SocialUser> findByOauthIdAndOauthProvider(String oauthId, OauthProvider provider);
    SocialUser createUser(String oauthId, OauthProvider provider);

    Optional<SocialUser> findById(Long userId);
}
