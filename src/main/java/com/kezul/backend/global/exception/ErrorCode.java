package com.kezul.backend.global.exception;

import org.springframework.http.HttpStatus;

/**
 * 애플리케이션의 모든 에러 코드가 공통으로 구현해야 하는 인터페이스.
 *
 * <p>
 * 도메인(모듈)별로 에러 코드 Enum을 분리하여 관리하기 위해 사용됩니다.
 * 예) GlobalErrorCode, AuthErrorCode, UserErrorCode 등
 */
public interface ErrorCode {

    /**
     * 클라이언트에 전달되는 고유 에러 식별자 (예: "C001", "AUTH_001")
     */
    String getCode();

    /**
     * 사람이 읽기 쉬운 에러 설명
     */
    String getMessage();

    /**
     * 응답에 사용할 HTTP 상태 코드
     */
    HttpStatus getHttpStatus();

    /**
     * 다국어(i18n) 처리를 위한 메시지 키 반환 (예: "error.C001")
     */
    default String getMessageKey() {
        return "error." + getCode();
    }
}
