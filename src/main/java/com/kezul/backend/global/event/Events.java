package com.kezul.backend.global.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
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
public class Events implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        Events.publisher = applicationEventPublisher;
    }

    /**
     * 지정된 이벤트 봉투({@link EventEnvelope})를 Spring ApplicationContext에 발행합니다.
     * <p>
     * 시스템의 일관된 이벤트 추적(Tracing)과 트랜잭셔널 아웃박스(Transactional Outbox)
     * 패턴 처리를 위해, 모든 도메인 이벤트는 반드시 {@link EventEnvelope#wrap} 메서드를 통해
     * 포장된 상태로 이 메서드에 전달되어야 합니다.
     *
     * @param eventEnvelope 이벤트 메타데이터와 실제 페이로드 데이터가 담긴 봉투 객체
     */
    public static void raise(EventEnvelope<? extends DomainEvent> eventEnvelope) {
        Assert.notNull(publisher, "ApplicationEventPublisher가 아직 등록되지 않았습니다.");
        publisher.publishEvent(eventEnvelope);
    }
}
