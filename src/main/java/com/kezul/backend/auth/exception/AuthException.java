package com.kezul.backend.auth.exception;

import com.kezul.backend.global.exception.ErrorCode;
import com.kezul.backend.global.exception.KezulException;

/**
 * 인증/인가 과정에서 발생하는 비즈니스 예외.
 */
public class AuthException extends KezulException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
