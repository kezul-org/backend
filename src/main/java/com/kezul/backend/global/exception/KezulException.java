package com.kezul.backend.global.exception;

/**
 * 애플리케이션 비즈니스 로직 예외의 최상위 클래스.
 * 도메인별 구체 예외(AuthException 등)가 이를 상속하며,
 * GlobalExceptionHandler에서 일실체 에러 응답으로 변환됩니다.
 */
public class KezulException extends RuntimeException {

    private final ErrorCode errorCode;

    public KezulException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public KezulException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public KezulException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
