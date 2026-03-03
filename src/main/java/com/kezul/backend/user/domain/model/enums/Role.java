package com.kezul.backend.user.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 시스템 내 사용자 권한 열거형.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    USER("ROLE_USER", "일반 사용자"), TRAINER("ROLE_TRAINER", "트레이너"), ADMIN("ROLE_ADMIN", "시스템 관리자");

    private final String key;
    private final String title;
}
