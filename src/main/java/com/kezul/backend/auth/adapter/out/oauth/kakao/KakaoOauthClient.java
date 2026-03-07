package com.kezul.backend.auth.adapter.out.oauth.kakao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.kezul.backend.auth.adapter.out.oauth.kakao.dto.KakaoTokenResponse;
import com.kezul.backend.auth.adapter.out.oauth.kakao.dto.KakaoUserResponse;
import com.kezul.backend.auth.application.port.out.OauthClient;
import com.kezul.backend.auth.config.KakaoOauthProperties;
import com.kezul.backend.auth.domain.model.dto.OauthUserInfo;
import com.kezul.backend.user.domain.model.enums.OauthProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 카카오 OAuth 클라이언트.
 * 카카오 인가 URL 생성, Authorization Code → Token 교환, 유저 정보 조회를 담당합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOauthClient implements OauthClient {

    private static final String TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final KakaoOauthProperties properties;
    private final RestClient restClient = RestClient.create();

    @Override
    public OauthProvider getProvider() {
        return OauthProvider.KAKAO;
    }

    @Override
    public OauthUserInfo getUserInfo(String code) {
        KakaoTokenResponse tokenResponse = restClient.post()
                .uri(TOKEN_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(getMultiValueMap(code))
                .retrieve()
                .body(KakaoTokenResponse.class);

        KakaoUserResponse authorization = restClient.get()
                .uri(USER_INFO_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.accessToken())
                .retrieve()
                .body(KakaoUserResponse.class);

        return new OauthUserInfo(this.getProvider(), authorization.id().toString());
    }

    private MultiValueMap<String, String> getMultiValueMap(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.getClientId());
        body.add("client_secret", properties.getClientSecret());
        body.add("redirect_uri", properties.getRedirectUri());
        body.add("code", code);
        return body;
    }
}
