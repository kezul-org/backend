package com.kezul.backend.user.exception;

import com.kezul.backend.global.exception.ErrorCode;
import com.kezul.backend.global.exception.KezulException;

/**
 * 사용자 도메인 규칙 위반 시 발생하는 비즈니스 예외.
 */
public class UserException extends KezulException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    public UserException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
