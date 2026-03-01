package com.kezul.backend.global.event;

import java.time.Clock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 스프링 {@link ApplicationEventPublisher}를 정적(Static)으로 사용하기 위한 편의 유틸리티 클래스입니다.
 * <p>
 * 이 클래스를 사용하면 각 도메인 서비스(Service) 클래스에서 불필요하게
 * {@code ApplicationEventPublisher}를 의존성 주입(DI) 받지 않아도 간편하게 이벤트를 발행할 수 있어,
 * 생성자 주입의 복잡도를 낮추고 코드를 간결하게 유지할 수 있습니다.
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
     * 지정된 도메인 이벤트를 봉투({@link EventEnvelope})로 감싸 Spring ApplicationContext에 발행합니다.
     * 이벤트 발생 시각은 시스템에 등록된 글로벌 타임존 기준(Clock)으로 기록됩니다.
     *
     * @param type    이벤트 종류
     * @param payload 실제 비즈니스 데이터를 담고 있는 도메인 이벤트 객체
     */
    public static <T extends DomainEvent> void raise(EventType type, T payload) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        Assert.notNull(clock, "Clock 빈이 아직 등록되지 않았습니다.");
        EventEnvelope<T> envelope = EventEnvelope.wrap(type, payload, clock);
        publisher.publishEvent(envelope);
    }

    /**
     * 이미 생성된 이벤트 봉투({@link EventEnvelope})를 그대로 발행합니다. (테스트용 및 확장용)
     *
     * @param eventEnvelope 이벤트 메타데이터와 실제 페이로드 데이터가 담긴 봉투 객체
     */
    public static void raise(EventEnvelope<? extends DomainEvent> eventEnvelope) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        publisher.publishEvent(eventEnvelope);
    }
}
