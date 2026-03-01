package com.kezul.backend.global.logging;

/**
 * 감사(Audit) 로그에 기록할 이벤트 종류.
 *
 * <p>
 * 문자열 대신 enum으로 관리해 오타로 인한 실수를 컴파일 타임에 방지합니다.
 * 새 이벤트 추가 시 도메인 접두사(_ 기준 앞부분)를 통일합니다.
 *
 * <p>
 * 도메인별 접두사:
 * <ul>
 * <li>USER_ — 사용자 관련</li>
 * <li>AUTH_ — 인증/인가 관련</li>
 * <li>PAYMENT_ — 결제 관련</li>
 * <li>ADMIN_ — 관리자 행위</li>
 * </ul>
 */
public enum AuditEvent {

    // ── User ─────────────────────────────────────
    USER_JOINED,
    USER_WITHDREW,
    USER_PROFILE_UPDATED,

    // ── Auth ─────────────────────────────────────
    AUTH_LOGIN,
    AUTH_LOGOUT,
    AUTH_TOKEN_REFRESHED,

    // ── Payment (추후 결제 도메인 구현 시 사용) ──
    PAYMENT_INITIATED,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED,
    PAYMENT_CANCELLED,

    // ── Admin ────────────────────────────────────
    ADMIN_USER_SUSPENDED,
    ADMIN_USER_FORCE_WITHDREW,
}
