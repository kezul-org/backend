-- Spring Modulith 이벤트 발행 테이블
-- Spring Modulith의 Transactional Outbox 패턴이 도메인 이벤트를 추적하는 데 사용합니다.
CREATE TABLE IF NOT EXISTS event_publication (
    id                     BINARY(16)   NOT NULL,
    listener_id            VARCHAR(512) NOT NULL,
    event_type             VARCHAR(512) NOT NULL,
    serialized_event       TEXT         NOT NULL,
    publication_date       TIMESTAMP(6) NOT NULL,
    completion_date        TIMESTAMP(6) NULL,
    completion_attempts    INT          NOT NULL,
    last_resubmission_date TIMESTAMP(6) NULL,
    status                 VARCHAR(16)  NOT NULL, -- 추가됨: PUBLISHED, COMPLETED 등 상태 관리용
    PRIMARY KEY (id)
);

CREATE INDEX idx_event_publication_by_completion_date
    ON event_publication (completion_date);
