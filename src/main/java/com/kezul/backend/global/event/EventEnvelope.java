package com.kezul.backend.global.event;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 도메인 이벤트 데이터 본문에 공통 메타데이터(발생 시간, 고유 ID, 종류)를 더하는 봉투(Envelope) 객체.
 * 
 * @param <T> DomainEvent 를 구현한 실제 페이로드 객체
 */
public record EventEnvelope<T extends DomainEvent>(
        String eventId, // 이벤트 고유 식별자 리퍼런스 (디버깅 / 멱등성 목적)
        LocalDateTime time, // 이벤트 발행 시간 (현재 버전에선 System default, 추후 Clock 주입)
        EventType type, // 이벤트 종류
        T payload // 본문 데이터
) implements ResolvableTypeProvider {

    @Override
    @JsonIgnore
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                getClass(), ResolvableType.forInstance(payload));
    }

    /**
     * 편의 팩토리 메서드
     * 
     * @param type    이벤트 종류
     * @param payload 실제 데이터 객체
     * @param <T>     DomainEvent
     * @return EventEnvelope 인스턴스
     */
    public static <T extends DomainEvent> EventEnvelope<T> wrap(EventType type, T payload) {
        return new EventEnvelope<>(
                UUID.randomUUID().toString(),
                LocalDateTime.now(), // [KEZ-26] 글로벌 타임존 이슈 시 Clock 기반으로 변경 예정
                type,
                payload);
    }
}
