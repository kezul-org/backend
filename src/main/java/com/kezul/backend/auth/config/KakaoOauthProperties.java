package com.kezul.backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 카카오 OAuth 설정값.
 * application.yml의 app.auth.oauth.kakao 하위 프로퍼티를 바인딩합니다.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.auth.oauth.kakao")
public class KakaoOauthProperties {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
