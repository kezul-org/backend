package com.kezul.backend.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 데이터소스 설정을 타입 안전하게 바인딩합니다.
 * application.yml의 spring.datasource.* 값과 자동 매핑됩니다.
 */
@ConfigurationProperties(prefix = "spring.datasource")
public record DatabaseProperties(
        String url,
        String username,
        String password,
        String driverClassName) {
}
