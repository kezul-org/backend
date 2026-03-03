package com.kezul.backend.global.error;

/**
 * 애플리케이션 비즈니스 로직 예외의 최상위 클래스.
 *
 * <p>
 * 체크 예외(checked exception) 사용을 지양하고 모든 도메인 예외를 이
 * 런타임 예외로 통일합니다. {@link GlobalExceptionHandler}에서 이 예외를
 * 잡아 공통 에러 응답으로 변환합니다.
 *
 * <p>
 * 흐름:
 * <ol>
 * <li>서비스 레이어에서 비즈니스 규칙 위반 감지</li>
 * <li>{@code throw new AppException(ErrorCode.USER_NOT_FOUND)} 발생</li>
 * <li>{@link GlobalExceptionHandler}가 캐치 후 JSON 응답 + 구조화 로그 처리</li>
 * </ol>
 */
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * 에러 코드만으로 예외를 생성합니다.
     * 메시지는 {@link ErrorCode#getMessage()}를 기본값으로 사용합니다.
     *
     * @param errorCode
     *            발생한 비즈니스 에러의 종류
     */
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 커스텀 메시지로 예외를 생성합니다.
     * 에러 코드의 기본 메시지 외에 추가 컨텍스트를 제공할 때 사용합니다.
     *
     * @param errorCode
     *            발생한 비즈니스 에러의 종류
     * @param message
     *            상세 에러 메시지 (로그에만 기록되며 클라이언트에는 노출되지 않음)
     */
    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 에러 코드와 원인 예외로 예외를 생성합니다.
     * 외부 시스템 호출 실패 등 원인 예외를 래핑할 때 사용합니다.
     *
     * @param errorCode
     *            발생한 비즈니스 에러의 종류
     * @param cause
     *            원인이 된 예외 (스택 트레이스에 연결됨)
     */
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
