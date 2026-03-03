package com.kezul.backend.global.exception;

import com.kezul.backend.global.logging.AppLog;
import com.kezul.backend.global.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 전역 예외를 캐치하여 로깅하고 일관된 CommonResponse 포맷으로 반환하는 핸들러.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * 비즈니스 예외 처리
     */
    @ExceptionHandler(KezulException.class)
    public ResponseEntity<CommonResponse<Void>> handleKezulException(KezulException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        String traceId = MDC.get("traceId");

        String i18nMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getMessage(),
                LocaleContextHolder.getLocale());

        AppLog.businessException(
                log,
                errorCode.getCode(),
                errorCode.getMessage(),
                errorCode.getHttpStatus().value(),
                ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
    }

    /**
     * Bean Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Void>> handleValidationException(
            MethodArgumentNotValidException ex) {
        String traceId = MDC.get("traceId");
        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;

        String defaultI18nMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getMessage(),
                LocaleContextHolder.getLocale());

        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .findFirst()
                .orElse(defaultI18nMessage);

        AppLog.validationFailed(log, errorCode.getCode(), ex.getBindingResult().getErrorCount(), errorMessage);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(errorCode.getCode(), errorMessage, traceId));
    }

    /**
     * 지원하지 않는 HTTP 메서드 예외 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex) {
        String traceId = MDC.get("traceId");
        ErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;

        String i18nMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getMessage(),
                LocaleContextHolder.getLocale());

        AppLog.methodNotAllowed(log, errorCode.getCode(), ex.getMethod());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
    }

    /**
     * 존재하지 않는 URL(리소스) 요청 예외 처리
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<Void>> handleNoResourceFound(
            NoResourceFoundException ex) {
        String traceId = MDC.get("traceId");
        ErrorCode errorCode = GlobalErrorCode.RESOURCE_NOT_FOUND;

        String i18nMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getMessage(),
                LocaleContextHolder.getLocale());

        AppLog.resourceNotFound(log, errorCode.getCode(), ex.getResourcePath());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
    }

    /**
     * 최상위 예외 처리 (최후 방어선)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
        String traceId = MDC.get("traceId");
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;

        String i18nMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                null,
                errorCode.getMessage(),
                LocaleContextHolder.getLocale());

        AppLog.unhandledException(log, errorCode.getCode(), ex);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
    }
}
