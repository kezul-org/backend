package com.kezul.backend.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.auth.jwt")
public class JwtProperties {
    private String issuer;
    private String secretKey;
    private long accessTokenExpirationMinutes = 30;
    private long refreshTokenExpirationDays = 90;
}
