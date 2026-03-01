package com.kezul.backend.global.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 스프링 ApplicationEventPublisher를 정적(Static)으로 사용하기 위한 유틸리티.
 * 각 도메인 서비스에서 의존성 주입 없이 간편하게 이벤트를 발행할 수 있습니다.
 */
@Component
public class Events implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Events.publisher = applicationEventPublisher;
    }

    /**
     * 이벤트를 ApplicationContext에 발행합니다.
     * TransactionalOutbox 패턴 등을 지원하기 위해 봉투(Envelope) 형태로 이벤트를 한번 감싸서 발행하는 것을 권장합니다.
     *
     * @param eventEnvelope 이벤트 봉투 객체
     */
    public static void raise(EventEnvelope<? extends DomainEvent> eventEnvelope) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        publisher.publishEvent(eventEnvelope);
    }
}
