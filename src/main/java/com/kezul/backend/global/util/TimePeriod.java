package com.kezul.backend.global.util;

import java.time.LocalDateTime;

/**
 * DB 조회 등에서 BETWEEN 쿼리에 사용할 시작 시간과 종료 시간을 담는 レコード(Record) 객체.
 * 모든 시간은 서버 스탠다드인 UTC 기준으로 저장됩니다.
 */
public record TimePeriod(
        LocalDateTime startUtc,
        LocalDateTime endUtc) {
}
