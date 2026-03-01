package com.kezul.backend.global.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 모든 도메인 이벤트의 알맹이(Payload) 역할을 하는 최상위 마커 인터페이스입니다.
 * <p>
 * 실제 애플리케이션에서 발생하는 특정 사건의 데이터(예: 회원가입, 매칭 체결 등)를 담는 DTO는
 * 반드시 이 인터페이스를 구현해야 합니다. 생성된 이벤트 데이터는 {@link EventEnvelope}에
 * 포장(Wrap)되어 시스템 전체로 안전하게 발행됩니다.
 * <p>
 * Spring Modulith Outbox 기반의 트랜잭셔널 이벤트가 JSON으로 직렬화/역직렬화될 때,
 * 구체적인(Concrete) 클래스 타입을 잃어버리는 문제(Type Erasure)를 방지하기 위해
 * {@link JsonTypeInfo}를 통해 클래스 메타정보를 함께 저장하도록 강제합니다.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface DomainEvent {
}
