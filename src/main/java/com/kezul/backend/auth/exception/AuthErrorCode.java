package com.kezul.backend.auth.exception;

import com.kezul.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 인증 및 인가(A) 도메인 전용 에러 코드.
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("A002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    EXPIRED_TOKEN("A003", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("A004", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    UNSUPPORTED_PROVIDER("A005", "지원하지 않는 소셜 로그인 제공자입니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
