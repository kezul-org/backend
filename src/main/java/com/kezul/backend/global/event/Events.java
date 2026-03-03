package com.kezul.backend.global.event;

import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 도메인 이벤트 정적 발행 유틸리티 클래스.
 */
@Component
public class Events {

    private static ApplicationEventPublisher publisher;
    private static Clock clock;

    public Events(ApplicationEventPublisher publisher, Clock clock) {
        Events.publisher = publisher;
        Events.clock = clock;
    }

    /**
     * 도메인 이벤트를 EventEnvelope으로 감싸 ApplicationContext에 발행합니다.
     *
     * @param type
     *            이벤트 종류
     * @param payload
     *            실제 이벤트 페이로드
     */
    public static <T extends DomainEvent> void raise(EventType type, T payload) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        Assert.notNull(clock, "Clock 빈이 아직 등록되지 않았습니다.");
        EventEnvelope<T> envelope = EventEnvelope.wrap(type, payload, clock);
        publisher.publishEvent(envelope);
    }

    /**
     * 생성된 EventEnvelope을 그대로 발행합니다. (테스트 및 내부 확장용)
     *
     * @param eventEnvelope
     *            이벤트 봉투 객체
     */
    public static void raise(EventEnvelope<? extends DomainEvent> eventEnvelope) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        publisher.publishEvent(eventEnvelope);
    }
}
