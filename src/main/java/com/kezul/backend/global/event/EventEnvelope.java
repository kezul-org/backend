package com.kezul.backend.global.event;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 도메인 이벤트의 알맹이({@link DomainEvent})에 공통 기술 메타데이터를 덧붙이는 봉투(Envelope) 객체입니다.
 * <p>
 * 이벤트 발생 시점, 고유 식별자, 이벤트 종류 등 인프라스트럭처 레벨에서 필요한 공통 정보들을
 * 실제 비즈니스 도메인 객체(Payload)와 분리하여 관리하기 위해 사용됩니다.
 * <p>
 * {@link ResolvableTypeProvider}를 구현하여, 제네릭 타입 {@code <T>}가 런타임에 소거되더라도
 * Spring ApplicationEventPublisher가 이벤트 리스너의 제네릭 타입을 정확하게 유추하여 라우팅할 수 있도록
 * 지원합니다.
 * 
 * @param eventId 이벤트의 고유 식별자 (디버깅, 멱등성 검증, 메시지 추적 용도)
 * @param time    이벤트가 실제 발생한 런타임 시각 (글로벌 서비스 시 TimeZone 동기화의 기준점)
 * @param type    이벤트의 종류를 나타내는 명시적 Enum 값
 * @param payload 실제 비즈니스 데이터를 담고 있는 도메인 이벤트 객체
 * @param <T>     {@link DomainEvent}를 구현한 실제 페이로드 클래스 타입
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
