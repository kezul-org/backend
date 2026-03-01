package com.kezul.backend.global.event;

/**
 * 전역 시스템 내에서 발생하는 모든 도메인 이벤트의 식별자(종류)를 중앙 집중적으로 관리하는 열거형입니다.
 * <p>
 * 이벤트 리스너가 특정 종류의 이벤트만 필터링하거나,
 * 로그 및 디버깅 시 직관적으로 이벤트의 유형을 파악할 수 있도록 돕습니다.
 */
public enum EventType {
    USER_CREATED,
    // 필요에 따라 시스템 확장 시 추가
}
