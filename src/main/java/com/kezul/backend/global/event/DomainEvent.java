package com.kezul.backend.global.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 모든 도메인 이벤트의 최상위 마커 인터페이스.
 * 이벤트 객체는 이 인터페이스를 구현해야 합니다.
 * Spring Modulith Outbox가 JSON으로 파싱할 때 구현체 타입을 추적하기 위해 JsonTypeInfo 선언을 추가합니다.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
public interface DomainEvent {
}
