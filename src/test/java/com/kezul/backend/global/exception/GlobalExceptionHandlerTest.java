package com.kezul.backend.global.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.kezul.backend.global.response.CommonResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * GlobalExceptionHandler 각 핸들러 메서드의 응답 규격을 검증하는 단위 테스트.
 * Spring Context 없이 MessageSource만 Mock 처리하여 빠르게 실행됩니다.
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MDC.put("traceId", "test-trace-id");
    }

    @Test
    @DisplayName("KezulException 발생 시 올바른 ErrorCode와 메시지로 응답을 반환한다")
    void testHandleKezulException() {
        // given
        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
        KezulException exception = new KezulException(errorCode);
        when(messageSource.getMessage(eq(errorCode.getMessageKey()), any(), any(), any()))
                .thenReturn("잘못된 입력값입니다.");

        // when
        ResponseEntity<CommonResponse<Void>> response = globalExceptionHandler.handleKezulException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().message()).isEqualTo("잘못된 입력값입니다.");
        assertThat(response.getBody().traceId()).isEqualTo("test-trace-id");
    }

    @Test
    @DisplayName("MethodArgumentNotValidException 발생 시 첫 번째 필드 에러 메시지를 반환한다")
    void testHandleValidationException() {
        // given
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "field", "must not be blank");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        // when
        ResponseEntity<CommonResponse<Void>> response = globalExceptionHandler.handleValidationException(exception);

        // then
        ErrorCode errorCode = GlobalErrorCode.INVALID_INPUT_VALUE;
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().message()).isEqualTo("field: must not be blank");
    }

    @Test
    @DisplayName("HttpRequestMethodNotSupportedException 발생 시 405 상태코드와 응답을 반환한다")
    void testHandleMethodNotAllowed() {
        // given
        HttpRequestMethodNotSupportedException exception = new HttpRequestMethodNotSupportedException("POST");
        ErrorCode errorCode = GlobalErrorCode.METHOD_NOT_ALLOWED;
        when(messageSource.getMessage(eq(errorCode.getMessageKey()), any(), any(), any()))
                .thenReturn("지원하지 않는 HTTP 메서드입니다.");

        // when
        ResponseEntity<CommonResponse<Void>> response = globalExceptionHandler.handleMethodNotAllowed(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().message()).isEqualTo("지원하지 않는 HTTP 메서드입니다.");
    }

    @Test
    @DisplayName("NoResourceFoundException 발생 시 404 상태코드와 응답을 반환한다")
    void testHandleNoResourceFound() {
        // given
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.GET, "/not-found",
                "Resource not found");
        ErrorCode errorCode = GlobalErrorCode.RESOURCE_NOT_FOUND;
        when(messageSource.getMessage(eq(errorCode.getMessageKey()), any(), any(), any()))
                .thenReturn("요청한 리소스를 찾을 수 없습니다.");

        // when
        ResponseEntity<CommonResponse<Void>> response = globalExceptionHandler.handleNoResourceFound(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().message()).isEqualTo("요청한 리소스를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("기타 Exception 발생 시 500 상태코드와 내부 서버 에러 응답을 반환한다")
    void testHandleException() {
        // given
        Exception exception = new RuntimeException("Unexpected error");
        ErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        when(messageSource.getMessage(eq(errorCode.getMessageKey()), any(), any(), any()))
                .thenReturn("서버 내부 오류가 발생했습니다.");

        // when
        ResponseEntity<CommonResponse<Void>> response = globalExceptionHandler.handleException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo(errorCode.getCode());
        assertThat(response.getBody().message()).isEqualTo("서버 내부 오류가 발생했습니다.");
    }
}
