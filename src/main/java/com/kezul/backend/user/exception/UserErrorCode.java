package com.kezul.backend.user.exception;

import com.kezul.backend.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 사용자(U) 도메인 전용 에러 코드.
 */
@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND), EMAIL_ALREADY_EXISTS("U002", "이미 사용 중인 이메일입니다.",
            HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
