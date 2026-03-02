package com.kezul.backend.global.error;

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
 * 애플리케이션 전역 예외 처리기.
 *
 * <p>
 * 모든 컨트롤러에서 던져진 예외를 이 한 곳에서 잡아 처리합니다.
 * 비즈니스 코드에서 직접 에러 로그를 남기지 않고, 이 클래스에서 일괄 로깅합니다.
 *
 * <p>
 * 처리 흐름:
 * <ol>
 * <li>예외 발생 → 핸들러 메서드 진입</li>
 * <li>SLF4J 2.0 Fluent API로 구조화 로그 기록 (traceId/spanId 자동 포함)</li>
 * <li>MDC에서 traceId를 꺼내 에러 응답에 포함</li>
 * <li>{@link CommonResponse#fail}으로 통일된 에러 응답 반환</li>
 * </ol>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        private final MessageSource messageSource;

        public GlobalExceptionHandler(MessageSource messageSource) {
                this.messageSource = messageSource;
        }

        /**
         * 비즈니스 예외 처리.
         * 서비스 레이어에서 {@link AppException}을 던지면 이 메서드가 처리합니다.
         */
        @ExceptionHandler(AppException.class)
        public ResponseEntity<CommonResponse<Void>> handleAppException(AppException ex) {
                ErrorCode errorCode = ex.getErrorCode();
                String traceId = MDC.get("traceId");

                String i18nMessage = messageSource.getMessage(
                                errorCode.getMessageKey(),
                                null,
                                errorCode.getMessage(),
                                LocaleContextHolder.getLocale());

                log.atError()
                                .addKeyValue("errorCode", errorCode.getCode())
                                .addKeyValue("errorMessage", errorCode.getMessage())
                                .addKeyValue("httpStatus", errorCode.getHttpStatus().value())
                                .setCause(ex)
                                .log("Business exception occurred");

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
        }

        /**
         * Bean Validation 실패 예외 처리.
         * {@code @Valid} 어노테이션이 붙은 요청 본문/파라미터의 유효성 검사가 실패하면 발생합니다.
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<CommonResponse<Void>> handleValidationException(
                        MethodArgumentNotValidException ex) {
                String traceId = MDC.get("traceId");
                ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

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

                log.atWarn()
                                .addKeyValue("errorCode", errorCode.getCode())
                                .addKeyValue("violations", ex.getBindingResult().getErrorCount())
                                .log("Validation failed: {}", errorMessage);

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(CommonResponse.fail(errorCode.getCode(), errorMessage, traceId));
        }

        /**
         * 지원하지 않는 HTTP 메서드 요청 예외 처리.
         */
        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<CommonResponse<Void>> handleMethodNotAllowed(
                        HttpRequestMethodNotSupportedException ex) {
                String traceId = MDC.get("traceId");
                ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;

                String i18nMessage = messageSource.getMessage(
                                errorCode.getMessageKey(),
                                null,
                                errorCode.getMessage(),
                                LocaleContextHolder.getLocale());

                log.atWarn()
                                .addKeyValue("errorCode", errorCode.getCode())
                                .addKeyValue("method", ex.getMethod())
                                .log("Method not allowed");

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
        }

        /**
         * 존재하지 않는 URL 요청 예외 처리.
         */
        @ExceptionHandler(NoResourceFoundException.class)
        public ResponseEntity<CommonResponse<Void>> handleNoResourceFound(
                        NoResourceFoundException ex) {
                String traceId = MDC.get("traceId");
                ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;

                String i18nMessage = messageSource.getMessage(
                                errorCode.getMessageKey(),
                                null,
                                errorCode.getMessage(),
                                LocaleContextHolder.getLocale());

                log.atWarn()
                                .addKeyValue("errorCode", errorCode.getCode())
                                .addKeyValue("path", ex.getResourcePath())
                                .log("Resource not found");

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
        }

        /**
         * 위의 핸들러로 처리되지 않은 모든 예외의 최후 방어선.
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<CommonResponse<Void>> handleException(Exception ex) {
                String traceId = MDC.get("traceId");
                ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;

                String i18nMessage = messageSource.getMessage(
                                errorCode.getMessageKey(),
                                null,
                                errorCode.getMessage(),
                                LocaleContextHolder.getLocale());

                log.atError()
                                .addKeyValue("errorCode", errorCode.getCode())
                                .addKeyValue("exceptionType", ex.getClass().getSimpleName())
                                .setCause(ex)
                                .log("Unexpected exception occurred");

                return ResponseEntity
                                .status(errorCode.getHttpStatus())
                                .body(CommonResponse.fail(errorCode.getCode(), i18nMessage, traceId));
        }
}
