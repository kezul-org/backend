package com.kezul.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 전역(범용) 에러 코드 메세지 정의.
 * 도메인에 종속되지 않는 공통 에러(C0xx)를 관리합니다.
 */
@Getter
@RequiredArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    /** 요청 본문 또는 파라미터가 유효성 검사를 통과하지 못한 경우 */
    INVALID_INPUT_VALUE("C001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    /** 지원하지 않는 HTTP 메서드로 요청한 경우 */
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

    /** 요청한 리소스를 찾을 수 없는 경우 */
    RESOURCE_NOT_FOUND("C003", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /** 서버 내부에서 처리되지 않은 예외가 발생한 경우 */
    INTERNAL_SERVER_ERROR("C004", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
