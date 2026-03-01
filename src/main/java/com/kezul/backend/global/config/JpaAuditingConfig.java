package com.kezul.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Spring Data JPA의 Auditing(Entity 생성/수정일 자동 기록) 기능 활성화 및 시간 Provider 컴포넌트 설정.
 */
@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class JpaAuditingConfig {

    private final Clock clock;

    public JpaAuditingConfig(Clock clock) {
        this.clock = clock;
    }

    /**
     * 데이터 삽입(@CreatedDate) 또는 수정(@LastModifiedDate) 시,
     * 단순 LocalDateTime.now()가 아니라 스프링 컨테이너가 제공하는 Clock 빈을 사용하도록 덮어씁니다.
     * 이로 인해 타임 트래블(모의 시간) 테스트 시 데이터베이스 저장 시간 조작이 손쉬워집니다.
     */
    @Bean
    public DateTimeProvider auditingDateTimeProvider() {
        return () -> Optional.of(LocalDateTime.now(clock));
    }
}
