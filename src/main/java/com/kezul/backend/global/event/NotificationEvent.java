package com.kezul.backend.global.event;

/**
 * 도메인 이벤트 중 "사용자에게 푸시/이메일 등 알림 발송이 필요한" 이벤트임을 나타내는 마커 인터페이스.
 * 이 인터페이스를 구현한 이벤트는 알림 모듈(Notification Module)에서 수신하여 일괄 처리할 수 있습니다.
 */
public interface NotificationEvent extends DomainEvent {

    /**
     * 알림을 수신할 대상 회원의 고유 ID를 반환해야 합니다.
     *
     * @return 알림 수신 대상 UserId
     */
    Long getTargetUserId();
}
