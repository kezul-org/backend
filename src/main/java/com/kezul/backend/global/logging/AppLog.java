package com.kezul.backend.global.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * 반복적인 구조화 로그를 타입 안전하게 찍는 정적 유틸리티.
 * {@link Audit} AOP로 커버되지 않는 일회성/조건부 로그에 사용합니다.
 */
@Slf4j
public final class AppLog {

    public static final String CACHE_KEY = "cacheKey";
    public static final String CACHE_HIT = "cacheHit";
    public static final String API = "api";
    public static final String STATUS_CODE = "statusCode";
    public static final String DURATION_MS = "durationMs";
    public static final String JOB = "job";
    public static final String PROCESSED_COUNT = "processedCount";

    private AppLog() {
    }

    public static void cacheResult(Logger log, String key, boolean hit) {
        log.atDebug()
                .addKeyValue(CACHE_KEY, key)
                .addKeyValue(CACHE_HIT, hit)
                .log("Cache lookup");
    }

    public static void externalApi(Logger log, String apiName, int statusCode, long durationMs) {
        log.atInfo()
                .addKeyValue(API, apiName)
                .addKeyValue(STATUS_CODE, statusCode)
                .addKeyValue(DURATION_MS, durationMs)
                .log("External API call completed");
    }

    /**
     * 외부 API 실패 기록. 이후 AppException으로 변환해서 던지세요.
     */
    public static void externalApiError(Logger log, String apiName,
            int statusCode, long durationMs, Throwable cause) {
        log.atWarn()
                .addKeyValue(API, apiName)
                .addKeyValue(STATUS_CODE, statusCode)
                .addKeyValue(DURATION_MS, durationMs)
                .setCause(cause)
                .log("External API call failed");
    }

    public static void schedulerCompleted(Logger log, String jobName,
            int processedCount, long durationMs) {
        log.atInfo()
                .addKeyValue(JOB, jobName)
                .addKeyValue(PROCESSED_COUNT, processedCount)
                .addKeyValue(DURATION_MS, durationMs)
                .log("Scheduler job completed");
    }
}
