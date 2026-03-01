package com.kezul.backend.global.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.TimeZone;

/**
 * 글로벌 서비스를 위한 시스템 전역 타임존 설정 및 Clock 주입 구성.
 */
@Slf4j
@Configuration
public class TimeConfig {

    /**
     * JVM 전체의 기준 시간을 KST에서 UTC로 완벽히 강제 고정합니다.
     * 이 설정 덕분에 DB 입력, 로깅 등 모든 서버 내 시간 기록은 물리적 리전과 무관하게 UTC 스탠다드로 남게 됩니다.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        log.info("System TimeZone is completely forced to UTC. Server init time: {}", LocalDateTime.now());
    }

    /**
     * 서비스 로직 전반에서 의존성을 주입(Injection)받아 사용할 "현재 시간 반환"용 Clock 빈.
     * 프로덕션에서는 System UTC를 따르며, 추후 테스트 코드 작성 시 @TestConfiguration 으로
     * 과거나 미래 시간(Clock.fixed) 빈으로 갈아끼워 타임 머신 테스트를 원활하게 수행할 수 있습니다.
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
