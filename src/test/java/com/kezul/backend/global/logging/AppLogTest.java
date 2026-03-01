package com.kezul.backend.global.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(OutputCaptureExtension.class)
class AppLogTest {

    private final Logger log = LoggerFactory.getLogger(AppLogTest.class);

    @Test
    @DisplayName("캐시 조회 결과를 규격에 맞게 DEBUG 레벨로 기록한다")
    void cacheResult(CapturedOutput output) {
        // given
        String key = "test:key:123";
        boolean hit = true;

        // when
        AppLog.cacheResult(log, key, hit);

        // Assert cacheResult
        assertThat(output.getOut())
                .contains("DEBUG")
                .contains(AppLog.CACHE_KEY + "=\"" + key + "\"")
                .contains(AppLog.CACHE_HIT + "=\"" + hit + "\"")
                .contains("Cache lookup");
    }

    @Test
    @DisplayName("외부 API 완료 로그를 규격에 맞게 INFO 레벨로 기록한다")
    void externalApi(CapturedOutput output) {
        // given
        String apiName = "NICE_API";
        int statusCode = 200;
        long durationMs = 150L;

        // when
        AppLog.externalApi(log, apiName, statusCode, durationMs);

        // then
        assertThat(output.getOut())
                .contains("INFO")
                .contains(AppLog.API + "=\"" + apiName + "\"")
                .contains(AppLog.STATUS_CODE + "=\"" + statusCode + "\"")
                .contains(AppLog.DURATION_MS + "=\"" + durationMs + "\"")
                .contains("External API call completed");
    }

    @Test
    @DisplayName("외부 API 실패 로그를 규격에 맞게 WARN 레벨로 기록하고 원인 예외를 포함한다")
    void externalApiError(CapturedOutput output) {
        // given
        String apiName = "PAYMENT_GW";
        int statusCode = 502;
        long durationMs = 300L;
        RuntimeException cause = new RuntimeException("Timeout occurred");

        // when
        AppLog.externalApiError(log, apiName, statusCode, durationMs, cause);

        // then
        assertThat(output.getOut())
                .contains("WARN")
                .contains(AppLog.API + "=\"" + apiName + "\"")
                .contains(AppLog.STATUS_CODE + "=\"" + statusCode + "\"")
                .contains(AppLog.DURATION_MS + "=\"" + durationMs + "\"")
                .contains("External API call failed")
                .contains("java.lang.RuntimeException: Timeout occurred");
    }

    @Test
    @DisplayName("스케줄러 완료 로그를 규격에 맞게 INFO 레벨로 기록한다")
    void schedulerCompleted(CapturedOutput output) {
        // given
        String jobName = "DailyReportJob";
        int processedCount = 1000;
        long durationMs = 5000L;

        // when
        AppLog.schedulerCompleted(log, jobName, processedCount, durationMs);

        // then
        assertThat(output.getOut())
                .contains("INFO")
                .contains(AppLog.JOB + "=\"" + jobName + "\"")
                .contains(AppLog.PROCESSED_COUNT + "=\"" + processedCount + "\"")
                .contains(AppLog.DURATION_MS + "=\"" + durationMs + "\"")
                .contains("Scheduler job completed");
    }
}
