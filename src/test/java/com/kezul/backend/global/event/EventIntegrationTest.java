package com.kezul.backend.global.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.EventListener;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@SpringBootTest
@Import(EventIntegrationTest.TestListener.class)
class EventIntegrationTest {

    @Autowired
    private TestListener testListener;

    // 테스트용 도메인 이벤트 모델
    record DummyEvent(Long id, String name) implements DomainEvent {
    }

    @Component
    public static class TestListener {
        private String syncReceivedName = null;
        private String asyncReceivedName = null;

        @EventListener
        void handleSync(EventEnvelope<DummyEvent> envelope) {
            this.syncReceivedName = envelope.payload().name();
        }

        @ApplicationModuleListener
        void handleAsync(EventEnvelope<DummyEvent> envelope) {
            this.asyncReceivedName = envelope.payload().name();
        }

        public String getSyncReceivedName() {
            return syncReceivedName;
        }

        public String getAsyncReceivedName() {
            return asyncReceivedName;
        }
    }

    @Test
    @DisplayName("봉투(EventEnvelope)로 감싼 이벤트가 동기 리스너에 성공적으로 전달된다")
    void syncEventPublishTest() {
        // given
        DummyEvent dummy = new DummyEvent(1L, "Sanghoon Sync");

        // when
        Events.raise(EventType.USER_CREATED, dummy);

        // then
        assertThat(testListener.getSyncReceivedName()).isEqualTo("Sanghoon Sync");
    }

    @Test
    @DisplayName("스프링 모듈리스 Scenario 기반 비동기 트랜잭셔널 아웃박스 이벤트 전달 검증")
    void asyncEventPublishTest(Scenario scenario) {
        // given
        DummyEvent dummy = new DummyEvent(2L, "Sanghoon Async");
        EventEnvelope<DummyEvent> envelope = EventEnvelope.wrap(EventType.USER_CREATED, dummy,
                java.time.Clock.systemUTC());

        // when & then
        // Scenario 기능을 통해 해당 이벤트가 @ApplicationModuleListener로 정상 전달/처리되었음을 완료 시점까지 대기하며
        // 검증합니다.
        scenario.publish(envelope)
                .andWaitForEventOfType(EventEnvelope.class)
                .matching(received -> "Sanghoon Async".equals(((DummyEvent) received.payload()).name()))
                .toArrive();
    }
}
