package com.kezul.backend.auth.application.port.in;

import com.kezul.backend.auth.application.port.in.dto.SocialLoginCommand;
import com.kezul.backend.auth.application.port.out.dto.TokenPair;

/**
 * 소셜 로그인 유스케이스 (Input Port).
 * Controller가 이 인터페이스를 호출하고, SocialLoginService가 구현합니다.
 */
public interface SocialLoginUseCase {

    /** Authorization Code로 소셜 로그인을 수행하고 JWT TokenPair를 반환합니다. */
    TokenPair login(SocialLoginCommand command);
}
