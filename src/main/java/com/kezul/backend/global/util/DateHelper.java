package com.kezul.backend.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.DayOfWeek;

/**
 * 클라이언트 타임존(ZoneId) 기반 UTC 조회 범위(TimePeriod) 산출 유틸리티.
 */
@Component
public class DateHelper {

    private final LocalTime cutoffTime;
    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    /**
     * @param cutoffHour 기준 시간 (app.daily-cutoff-hour, 기본값 0)
     */
    public DateHelper(@Value("${app.daily-cutoff-hour:0}") int cutoffHour) {
        this.cutoffTime = LocalTime.of(cutoffHour, 0);
    }

    /* ========================================================================= */
    /* 1. 단순 기간 산출                                                           */
    /* ========================================================================= */

    /**
     * 특정 날짜의 자정 기준 시작 시간 ~ 종료 시간(UTC) 반환.
     */
    public static TimePeriod getDailyPeriod(LocalDate date, ZoneId clientZone) {
        return getPeriod(date, date, clientZone);
    }

    /**
     * 특정 주(월~일)의 시작(UTC) ~ 종료(UTC) 반환.
     * 
     * @param dateInWeek 주간 내 어느 한 날짜
     */
    public static TimePeriod getWeeklyPeriod(LocalDate dateInWeek, ZoneId clientZone) {
        LocalDate startOfWeek = dateInWeek.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = dateInWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return getPeriod(startOfWeek, endOfWeek, clientZone);
    }

    /**
     * 특정 월의 시작일 ~ 말일 (UTC) 반환.
     */
    public static TimePeriod getMonthlyPeriod(YearMonth yearMonth, ZoneId clientZone) {
        LocalDate from = yearMonth.atDay(1);
        LocalDate to = yearMonth.atEndOfMonth();
        return getPeriod(from, to, clientZone);
    }

    /**
     * 특정 시작일 ~ 종료일의 UTC 범위 반환.
     * 종료일의 가장 끝 시점(23:59:59.999...)까지 안정적으로 포함합니다.
     */
    public static TimePeriod getPeriod(LocalDate from, LocalDate to, ZoneId clientZone) {
        LocalDateTime startLocal = from.atStartOfDay();
        LocalDateTime endLocal = to.plusDays(1).atStartOfDay().minusNanos(1);

        LocalDateTime startUtc = ZonedDateTime.of(startLocal, clientZone).withZoneSameInstant(UTC_ZONE)
                .toLocalDateTime();
        LocalDateTime endUtc = ZonedDateTime.of(endLocal, clientZone).withZoneSameInstant(UTC_ZONE).toLocalDateTime();

        return new TimePeriod(startUtc, endUtc);
    }

    /**
     * "지금 현재" 클라이언트 타임존 기준으로 오늘(자정 기준) 하루 전체의 UTC 범위를 구합니다.
     */
    public static TimePeriod getTodayPeriod(ZoneId clientZone) {
        LocalDate todayInClientZone = ZonedDateTime.now(UTC_ZONE).withZoneSameInstant(clientZone).toLocalDate();
        return getDailyPeriod(todayInClientZone, clientZone);
    }

    /**
     * DB에서 꺼낸 UTC 데이터를 클라이언트 타임존 기준의 며칠(LocalDate)인지 다시 역변환합니다. (통계 그룹핑 등)
     */
    public static LocalDate toLocalDate(LocalDateTime utcDateTime, ZoneId clientZone) {
        return utcDateTime.atZone(UTC_ZONE).withZoneSameInstant(clientZone).toLocalDate();
    }

    /* ========================================================================= */
    /* 2. 비즈니스 컷오프(Cut-off) 정책 연동 메서드                                   */
    /* ========================================================================= */

    /**
     * 특정 시점(ZonedDateTime)이 Cut-off 기준으로 어떤 비즈니스 날짜에 속하는지 계산합니다.
     */
    public LocalDate getBusinessDate(ZonedDateTime dateTime, ZoneId clientZone) {
        ZonedDateTime localDateTime = dateTime.withZoneSameInstant(clientZone);
        if (localDateTime.toLocalTime().isBefore(cutoffTime)) {
            return localDateTime.toLocalDate().minusDays(1);
        } else {
            return localDateTime.toLocalDate();
        }
    }

    /**
     * "지금 현재" 클라이언트 지역의 비즈니스 하루 전체 UTC 범위 반환.
     */
    public TimePeriod getBusinessTodayPeriod(ZoneId clientZone) {
        return getBusinessDailyPeriod(ZonedDateTime.now(UTC_ZONE), clientZone);
    }

    /**
     * 특정 시점이 속한 해당 비즈니스 하루의 전체 UTC 범위를 반환합니다.
     */
    public TimePeriod getBusinessDailyPeriod(ZonedDateTime dateTime, ZoneId clientZone) {
        LocalDate businessDate = getBusinessDate(dateTime, clientZone);
        return getBusinessPeriod(businessDate, businessDate, clientZone);
    }

    /**
     * 시작일 ~ 종료일 비즈니스 기간(Cut-off 부터 익일 Cut-off 직전까지)의 UTC 범위 반환.
     */
    public TimePeriod getBusinessPeriod(LocalDate from, LocalDate to, ZoneId clientZone) {
        LocalDateTime startLocal = from.atTime(cutoffTime);
        LocalDateTime endLocal = to.plusDays(1).atTime(cutoffTime).minusNanos(1);

        LocalDateTime startUtc = ZonedDateTime.of(startLocal, clientZone).withZoneSameInstant(UTC_ZONE)
                .toLocalDateTime();
        LocalDateTime endUtc = ZonedDateTime.of(endLocal, clientZone).withZoneSameInstant(UTC_ZONE).toLocalDateTime();

        return new TimePeriod(startUtc, endUtc);
    }
}
