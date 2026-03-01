package com.kezul.backend.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 반복적인 구조화 로그를 타입 안전하게 찍는 정적 유틸리티.
 * {@link Audit} AOP로 커버되지 않는 일회성/조건부 로그에 사용합니다.
 */
@Slf4j
public final class AppLog {

    private AppLog() {
    }

    public static void cacheResult(Logger log, String key, boolean hit) {
        log.atDebug()
                .addKeyValue("cacheKey", key)
                .addKeyValue("cacheHit", hit)
                .log("Cache lookup");
    }

    public static void externalApi(Logger log, String apiName, int statusCode, long durationMs) {
        log.atInfo()
                .addKeyValue("api", apiName)
                .addKeyValue("statusCode", statusCode)
                .addKeyValue("durationMs", durationMs)
                .log("External API call completed");
    }

    /**
     * 외부 API 실패 기록. 이후 AppException으로 변환해서 던지세요.
     */
    public static void externalApiError(Logger log, String apiName,
            int statusCode, long durationMs, Throwable cause) {
        log.atWarn()
                .addKeyValue("api", apiName)
                .addKeyValue("statusCode", statusCode)
                .addKeyValue("durationMs", durationMs)
                .setCause(cause)
                .log("External API call failed");
    }

    public static void schedulerCompleted(Logger log, String jobName,
            int processedCount, long durationMs) {
        log.atInfo()
                .addKeyValue("job", jobName)
                .addKeyValue("processedCount", processedCount)
                .addKeyValue("durationMs", durationMs)
                .log("Scheduler job completed");
    }
}
