package com.kezul.backend.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DateHelper 및 TimePeriod 단위 테스트.
 *
 * <p>
 * 테스트 전략:
 * <ul>
 * <li>Static 메서드 — 다양한 타임존(KST, UTC, US/Eastern)에서의 UTC 변환 검증</li>
 * <li>인스턴스 메서드 — Clock.fixed()를 주입하여 "현재 시간" 의존 로직 검증</li>
 * <li>비즈니스 컷오프 — cutoff 시간 경계값(before/after/exactly) 검증</li>
 * <li>TimePeriod — record의 동등성 및 접근자 검증</li>
 * </ul>
 */
class DateHelperTest {

    // ============================================================================================
    // 자주 사용하는 타임존 상수
    // ============================================================================================
    private static final ZoneId KST = ZoneId.of("Asia/Seoul"); // UTC+9
    private static final ZoneId UTC = ZoneId.of("UTC"); // UTC+0
    private static final ZoneId EST = ZoneId.of("America/New_York"); // UTC-5 (EST) / UTC-4 (EDT)

    // ============================================================================================
    // 1. TimePeriod Record 테스트
    // ============================================================================================

    @Nested
    @DisplayName("TimePeriod Record")
    class TimePeriodTest {

        @Test
        @DisplayName("startUtc와 endUtc 값이 정상적으로 저장되고 반환된다")
        void accessorsWork() {
            LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 1, 23, 59, 59, 999_999_999);

            TimePeriod period = new TimePeriod(start, end);

            assertThat(period.startUtc()).isEqualTo(start);
            assertThat(period.endUtc()).isEqualTo(end);
        }

        @Test
        @DisplayName("같은 시간 값을 가진 TimePeriod는 동등하다 (Record equals)")
        void equalityTest() {
            LocalDateTime start = LocalDateTime.of(2026, 3, 1, 0, 0, 0);
            LocalDateTime end = LocalDateTime.of(2026, 3, 1, 23, 59, 59, 999_999_999);

            TimePeriod period1 = new TimePeriod(start, end);
            TimePeriod period2 = new TimePeriod(start, end);

            assertThat(period1).isEqualTo(period2);
            assertThat(period1.hashCode()).isEqualTo(period2.hashCode());
        }

        @Test
        @DisplayName("시간이 다른 TimePeriod는 동등하지 않다")
        void inequalityTest() {
            TimePeriod period1 = new TimePeriod(
                    LocalDateTime.of(2026, 3, 1, 0, 0, 0),
                    LocalDateTime.of(2026, 3, 1, 23, 59, 59));
            TimePeriod period2 = new TimePeriod(
                    LocalDateTime.of(2026, 3, 2, 0, 0, 0),
                    LocalDateTime.of(2026, 3, 2, 23, 59, 59));

            assertThat(period1).isNotEqualTo(period2);
        }

        @Test
        @DisplayName("toString()은 필드 값을 포함한 문자열을 반환한다")
        void toStringContainsFields() {
            LocalDateTime start = LocalDateTime.of(2026, 1, 1, 0, 0);
            LocalDateTime end = LocalDateTime.of(2026, 1, 1, 23, 59);
            TimePeriod period = new TimePeriod(start, end);

            String str = period.toString();
            assertThat(str).contains("2026-01-01T00:00");
            assertThat(str).contains("2026-01-01T23:59");
        }
    }

    // ============================================================================================
    // 2. getDailyPeriod (Static) — 특정 날짜의 자정~끝 UTC 변환
    // ============================================================================================

    @Nested
    @DisplayName("getDailyPeriod — 단일 날짜 기간 산출")
    class GetDailyPeriodTest {

        @Test
        @DisplayName("KST 기준 2026-03-01 → UTC로 변환하면 2026-02-28 15:00:00 ~ 2026-03-01 14:59:59.999...")
        void kstToUtc() {
            // KST 2026-03-01 00:00:00 = UTC 2026-02-28 15:00:00 (KST는 UTC+9)
            TimePeriod period = DateHelper.getDailyPeriod(LocalDate.of(2026, 3, 1), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 2, 28, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("UTC 기준 날짜는 변환 없이 그대로 00:00:00 ~ 23:59:59.999...")
        void utcSameAsIs() {
            TimePeriod period = DateHelper.getDailyPeriod(LocalDate.of(2026, 3, 1), UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 23, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("EST(UTC-5) 기준 → UTC로 변환하면 +5시간 시프트")
        void estToUtc() {
            // EST 2026-03-01 00:00:00 = UTC 2026-03-01 05:00:00 (EST는 UTC-5)
            TimePeriod period = DateHelper.getDailyPeriod(LocalDate.of(2026, 3, 1), EST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 5, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 4, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("연말(12월 31일) KST → UTC 변환 시 연도 경계를 정확히 처리한다")
        void yearBoundary() {
            // KST 2025-12-31 00:00:00 = UTC 2025-12-30 15:00:00
            TimePeriod period = DateHelper.getDailyPeriod(LocalDate.of(2025, 12, 31), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2025, 12, 30, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2025, 12, 31, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("윤년 2월 29일 KST도 정상 처리된다")
        void leapYearFeb29() {
            // 2028년은 윤년. KST 2028-02-29 00:00 = UTC 2028-02-28 15:00
            TimePeriod period = DateHelper.getDailyPeriod(LocalDate.of(2028, 2, 29), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2028, 2, 28, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2028, 2, 29, 14, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 3. getWeeklyPeriod (Static) — 주간 기간 산출
    // ============================================================================================

    @Nested
    @DisplayName("getWeeklyPeriod — 주간 기간 산출")
    class GetWeeklyPeriodTest {

        @Test
        @DisplayName("주 중간(수요일) 날짜를 넣으면 해당 주 월요일~일요일 KST→UTC 범위를 반환한다")
        void midWeekWednesday() {
            // 2026-03-04 = 수요일. 해당 주: 월 3/2 ~ 일 3/8
            // KST 월 3/2 00:00 = UTC 3/1 15:00
            // KST 일 3/8 끝 = KST 3/9 00:00 - 1ns = UTC 3/8 14:59:59.999...
            TimePeriod period = DateHelper.getWeeklyPeriod(LocalDate.of(2026, 3, 4), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 8, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("월요일 자체를 넣으면 동일한 주가 반환된다")
        void mondayItself() {
            // 2026-03-02 = 월요일
            TimePeriod period = DateHelper.getWeeklyPeriod(LocalDate.of(2026, 3, 2), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 8, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("일요일 자체를 넣어도 동일한 주가 반환된다")
        void sundayItself() {
            // 2026-03-08 = 일요일
            TimePeriod period = DateHelper.getWeeklyPeriod(LocalDate.of(2026, 3, 8), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 8, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("UTC 기준 주간 범위는 타임존 시프트 없이 정확히 월~일 자정 기준")
        void utcWeek() {
            // 2026-03-04 = 수요일. 해당 주: 월 3/2 ~ 일 3/8
            TimePeriod period = DateHelper.getWeeklyPeriod(LocalDate.of(2026, 3, 4), UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 8, 23, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("월/연 경계를 걸치는 주(2025-12-29~2026-01-04)도 정상 처리된다")
        void crossYearBoundary() {
            // 2025-12-31 = 수요일. 해당 주: 월 12/29 ~ 일 1/4
            TimePeriod period = DateHelper.getWeeklyPeriod(LocalDate.of(2025, 12, 31), UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2025, 12, 29, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 1, 4, 23, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 4. getMonthlyPeriod (Static) — 월간 기간 산출
    // ============================================================================================

    @Nested
    @DisplayName("getMonthlyPeriod — 월간 기간 산출")
    class GetMonthlyPeriodTest {

        @Test
        @DisplayName("KST 2026년 3월 전체 → UTC 2월 28일 15:00 ~ 3월 31일 14:59:59.999...")
        void marchKst() {
            TimePeriod period = DateHelper.getMonthlyPeriod(YearMonth.of(2026, 3), KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 2, 28, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 31, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("UTC 2026년 2월 (비윤년) → 1일 00:00 ~ 28일 23:59:59.999...")
        void februaryNonLeapYear() {
            TimePeriod period = DateHelper.getMonthlyPeriod(YearMonth.of(2026, 2), UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 2, 1, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 2, 28, 23, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("윤년 2028년 2월 → 29일까지 포함")
        void februaryLeapYear() {
            TimePeriod period = DateHelper.getMonthlyPeriod(YearMonth.of(2028, 2), UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2028, 2, 1, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2028, 2, 29, 23, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("EST 기준 1월 → UTC 변환 시 +5시간 시프트")
        void januaryEst() {
            // EST 1/1 00:00 = UTC 1/1 05:00
            // EST 1/31 끝 = EST 2/1 00:00 - 1ns → UTC 2/1 05:00 - 1ns = UTC 2/1
            // 04:59:59.999...
            TimePeriod period = DateHelper.getMonthlyPeriod(YearMonth.of(2026, 1), EST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 1, 1, 5, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 2, 1, 4, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 5. getPeriod (Static) — 임의 기간 산출
    // ============================================================================================

    @Nested
    @DisplayName("getPeriod — 임의 기간 UTC 변환")
    class GetPeriodTest {

        @Test
        @DisplayName("from == to인 경우 하루 범위와 동일하다")
        void singleDay() {
            LocalDate date = LocalDate.of(2026, 3, 1);
            TimePeriod period = DateHelper.getPeriod(date, date, KST);
            TimePeriod daily = DateHelper.getDailyPeriod(date, KST);

            assertThat(period).isEqualTo(daily);
        }

        @Test
        @DisplayName("3일 범위(3/1~3/3) KST → UTC 변환")
        void threeDayRange() {
            // KST 3/1 00:00 = UTC 2/28 15:00
            // KST 3/3 끝 = KST 3/4 00:00 - 1ns → UTC 3/3 14:59:59.999...
            TimePeriod period = DateHelper.getPeriod(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 3),
                    KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 2, 28, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 14, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 6. getTodayPeriod (Instance, Clock 주입) — "오늘" 기간
    // ============================================================================================

    @Nested
    @DisplayName("getTodayPeriod — Clock 기반 오늘 기간 산출")
    class GetTodayPeriodTest {

        @Test
        @DisplayName("UTC 기준 2026-03-03 10:30에 KST 클라이언트의 '오늘' → KST 3/3(UTC+9이므로 19:30)")
        void utcClockKstClient() {
            // Clock 고정: UTC 2026-03-03 10:30:00
            // KST로 변환하면: 2026-03-03 19:30:00 → 오늘은 3/3
            // KST 3/3 00:00 = UTC 3/2 15:00
            // KST 3/3 끝 = UTC 3/3 14:59:59.999...
            Clock fixedClock = Clock.fixed(
                    ZonedDateTime.of(2026, 3, 3, 10, 30, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ZoneId.of("UTC"));
            DateHelper dateHelper = new DateHelper(0, fixedClock);

            TimePeriod period = dateHelper.getTodayPeriod(KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("UTC 자정 직전(23:59)에 KST 클라이언트의 '오늘' → KST 3/4(UTC+9이므로 이미 다음날)")
        void utcBeforeMidnightKstNextDay() {
            // Clock 고정: UTC 2026-03-03 23:59:00
            // KST로 변환하면: 2026-03-04 08:59:00 → 오늘은 3/4
            Clock fixedClock = Clock.fixed(
                    ZonedDateTime.of(2026, 3, 3, 23, 59, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ZoneId.of("UTC"));
            DateHelper dateHelper = new DateHelper(0, fixedClock);

            TimePeriod period = dateHelper.getTodayPeriod(KST);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 4, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("UTC 클라이언트는 Clock 시간 그대로의 날짜를 '오늘'로 사용한다")
        void utcClientSameDay() {
            Clock fixedClock = Clock.fixed(
                    ZonedDateTime.of(2026, 3, 3, 10, 0, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ZoneId.of("UTC"));
            DateHelper dateHelper = new DateHelper(0, fixedClock);

            TimePeriod period = dateHelper.getTodayPeriod(UTC);

            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 0, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 23, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 7. toLocalDate (Static) — UTC → 클라이언트 날짜 역변환
    // ============================================================================================

    @Nested
    @DisplayName("toLocalDate — UTC 시간을 클라이언트 날짜로 역변환")
    class ToLocalDateTest {

        @Test
        @DisplayName("UTC 2026-03-02 15:00 → KST 2026-03-03 00:00 → 3/3일")
        void utcToKst() {
            // UTC 3/2 15:00 + 9시간 = KST 3/3 00:00
            LocalDate result = DateHelper.toLocalDate(LocalDateTime.of(2026, 3, 2, 15, 0, 0), KST);
            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 3));
        }

        @Test
        @DisplayName("UTC 2026-03-02 14:59 → KST 2026-03-02 23:59 → 3/2일")
        void utcToKstBeforeMidnight() {
            // UTC 3/2 14:59 + 9시간 = KST 3/2 23:59
            LocalDate result = DateHelper.toLocalDate(LocalDateTime.of(2026, 3, 2, 14, 59, 0), KST);
            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 2));
        }

        @Test
        @DisplayName("UTC 시간이 그대로인 UTC 클라이언트")
        void utcToUtc() {
            LocalDate result = DateHelper.toLocalDate(LocalDateTime.of(2026, 3, 3, 23, 59, 0), UTC);
            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 3));
        }

        @Test
        @DisplayName("UTC 자정 → EST 전날(UTC-5이므로 전날 19시)")
        void utcMidnightToEst() {
            // UTC 3/3 00:00 - 5시간 = EST 3/2 19:00
            LocalDate result = DateHelper.toLocalDate(LocalDateTime.of(2026, 3, 3, 0, 0, 0), EST);
            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 2));
        }
    }

    // ============================================================================================
    // 8. getBusinessDate — 비즈니스 컷오프 기반 날짜 판정
    // ============================================================================================

    @Nested
    @DisplayName("getBusinessDate — 컷오프 기준 비즈니스 날짜 판정")
    class GetBusinessDateTest {

        @Test
        @DisplayName("cutoff=4시, KST 03:59 → 컷오프 전이므로 전날이 비즈니스 날짜 ")
        void beforeCutoff() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            // KST 2026-03-03 03:59 → cutoff(04:00) 전이므로 전날인 3/2
            ZonedDateTime kstTime = ZonedDateTime.of(2026, 3, 3, 3, 59, 0, 0, KST);
            LocalDate result = dateHelper.getBusinessDate(kstTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 2));
        }

        @Test
        @DisplayName("cutoff=4시, KST 04:00 → 컷오프 이후이므로 당일이 비즈니스 날짜")
        void exactlyCutoff() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            // KST 2026-03-03 04:00 → cutoff(04:00) 이후
            ZonedDateTime kstTime = ZonedDateTime.of(2026, 3, 3, 4, 0, 0, 0, KST);
            LocalDate result = dateHelper.getBusinessDate(kstTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 3));
        }

        @Test
        @DisplayName("cutoff=4시, KST 23:59 → 당연히 당일")
        void afterCutoffLate() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            ZonedDateTime kstTime = ZonedDateTime.of(2026, 3, 3, 23, 59, 0, 0, KST);
            LocalDate result = dateHelper.getBusinessDate(kstTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 3));
        }

        @Test
        @DisplayName("cutoff=0시(기본값), KST 00:00 → 컷오프가 자정이면 모든 시간이 당일")
        void zeroCutoff() {
            DateHelper dateHelper = new DateHelper(0, Clock.systemUTC());

            ZonedDateTime kstTime = ZonedDateTime.of(2026, 3, 3, 0, 0, 0, 0, KST);
            LocalDate result = dateHelper.getBusinessDate(kstTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 3));
        }

        @Test
        @DisplayName("UTC 시간을 KST 클라이언트 기준으로 변환 후 비즈니스 날짜를 판정한다")
        void utcInputKstClient() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            // UTC 2026-03-02 18:30 = KST 2026-03-03 03:30 → cutoff(4시) 전 → 전날 3/2
            ZonedDateTime utcTime = ZonedDateTime.of(2026, 3, 2, 18, 30, 0, 0, UTC);
            LocalDate result = dateHelper.getBusinessDate(utcTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2026, 3, 2));
        }

        @Test
        @DisplayName("연초 새벽(1/1 02:00 KST, cutoff=4) → 전년도 12/31이 비즈니스 날짜")
        void yearBoundaryCutoff() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            ZonedDateTime kstTime = ZonedDateTime.of(2026, 1, 1, 2, 0, 0, 0, KST);
            LocalDate result = dateHelper.getBusinessDate(kstTime, KST);

            assertThat(result).isEqualTo(LocalDate.of(2025, 12, 31));
        }
    }

    // ============================================================================================
    // 9. getBusinessTodayPeriod — Clock 기반 비즈니스 오늘 기간
    // ============================================================================================

    @Nested
    @DisplayName("getBusinessTodayPeriod — Clock 기반 비즈니스 오늘 기간")
    class GetBusinessTodayPeriodTest {

        @Test
        @DisplayName("cutoff=4시, Clock=KST 03:30 → 전날이 비즈니스 날짜, 전날 04:00~당일 03:59:59.999... UTC")
        void beforeCutoffClock() {
            // Clock 고정: UTC 2026-03-02 18:30 (= KST 2026-03-03 03:30 → cutoff 전 → 비즈니스 날짜는
            // 3/2)
            Clock fixedClock = Clock.fixed(
                    ZonedDateTime.of(2026, 3, 2, 18, 30, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ZoneId.of("UTC"));
            DateHelper dateHelper = new DateHelper(4, fixedClock);

            TimePeriod period = dateHelper.getBusinessTodayPeriod(KST);

            // 비즈니스 날짜 = 3/2
            // KST 3/2 04:00 = UTC 3/1 19:00
            // KST 3/3 04:00 - 1ns = UTC 3/2 18:59:59.999...
            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 1, 19, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 18, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("cutoff=4시, Clock=KST 04:01 → 당일이 비즈니스 날짜")
        void afterCutoffClock() {
            // Clock 고정: UTC 2026-03-02 19:01 (= KST 2026-03-03 04:01 → cutoff 후 → 비즈니스 날짜는
            // 3/3)
            Clock fixedClock = Clock.fixed(
                    ZonedDateTime.of(2026, 3, 2, 19, 1, 0, 0, ZoneId.of("UTC")).toInstant(),
                    ZoneId.of("UTC"));
            DateHelper dateHelper = new DateHelper(4, fixedClock);

            TimePeriod period = dateHelper.getBusinessTodayPeriod(KST);

            // 비즈니스 날짜 = 3/3
            // KST 3/3 04:00 = UTC 3/2 19:00
            // KST 3/4 04:00 - 1ns = UTC 3/3 18:59:59.999...
            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 19, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 18, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 10. getBusinessDailyPeriod — 특정 시점 기준 비즈니스 하루
    // ============================================================================================

    @Nested
    @DisplayName("getBusinessDailyPeriod — 특정 시점 기준 비즈니스 하루")
    class GetBusinessDailyPeriodTest {

        @Test
        @DisplayName("cutoff=0시(기본값), KST 15:00 → 당일 00:00~익일 00:00-1ns UTC 범위")
        void defaultCutoffMidDay() {
            DateHelper dateHelper = new DateHelper(0, Clock.systemUTC());

            ZonedDateTime kstTime = ZonedDateTime.of(2026, 3, 3, 15, 0, 0, 0, KST);
            TimePeriod period = dateHelper.getBusinessDailyPeriod(kstTime, KST);

            // 비즈니스 날짜 = 3/3, cutoff = 0시
            // KST 3/3 00:00 = UTC 3/2 15:00
            // KST 3/4 00:00 - 1ns = UTC 3/3 14:59:59.999...
            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 15, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 14, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("cutoff=6시, UTC 입력 → KST 변환 후 비즈니스 날짜 결정, 06:00~익일 05:59:59.999... UTC 범위")
        void cutoff6WithUTCInput() {
            DateHelper dateHelper = new DateHelper(6, Clock.systemUTC());

            // UTC 2026-03-02 22:00 = KST 2026-03-03 07:00 → cutoff(6시) 후 → 비즈니스 날짜 3/3
            ZonedDateTime utcTime = ZonedDateTime.of(2026, 3, 2, 22, 0, 0, 0, UTC);
            TimePeriod period = dateHelper.getBusinessDailyPeriod(utcTime, KST);

            // 비즈니스 날짜 = 3/3, cutoff = 6시
            // KST 3/3 06:00 = UTC 3/2 21:00
            // KST 3/4 06:00 - 1ns = UTC 3/3 20:59:59.999...
            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 3, 2, 21, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 20, 59, 59, 999_999_999));
        }
    }

    // ============================================================================================
    // 11. getBusinessPeriod — 비즈니스 기간 (다일)
    // ============================================================================================

    @Nested
    @DisplayName("getBusinessPeriod — 비즈니스 기간 UTC 변환")
    class GetBusinessPeriodTest {

        @Test
        @DisplayName("cutoff=4시, KST 3/1~3/3 비즈니스 기간 → 3/1 04:00 ~ 3/4 03:59:59.999... KST → UTC")
        void threeDayBusinessPeriod() {
            DateHelper dateHelper = new DateHelper(4, Clock.systemUTC());

            TimePeriod period = dateHelper.getBusinessPeriod(
                    LocalDate.of(2026, 3, 1),
                    LocalDate.of(2026, 3, 3),
                    KST);

            // KST 3/1 04:00 = UTC 2/28 19:00
            // KST 3/4 04:00 - 1ns = UTC 3/3 18:59:59.999...
            assertThat(period.startUtc()).isEqualTo(LocalDateTime.of(2026, 2, 28, 19, 0, 0));
            assertThat(period.endUtc()).isEqualTo(LocalDateTime.of(2026, 3, 3, 18, 59, 59, 999_999_999));
        }

        @Test
        @DisplayName("cutoff=0시, 단일 날짜 비즈니스 기간은 getDailyPeriod와 동일하다")
        void singleDayCutoffZero() {
            DateHelper dateHelper = new DateHelper(0, Clock.systemUTC());

            TimePeriod businessPeriod = dateHelper.getBusinessPeriod(
                    LocalDate.of(2026, 3, 3),
                    LocalDate.of(2026, 3, 3),
                    KST);
            TimePeriod dailyPeriod = DateHelper.getDailyPeriod(LocalDate.of(2026, 3, 3), KST);

            assertThat(businessPeriod).isEqualTo(dailyPeriod);
        }
    }
}
