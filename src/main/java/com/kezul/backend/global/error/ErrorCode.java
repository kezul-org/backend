package com.kezul.backend.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 애플리케이션 공통 에러 코드 정의.
 *
 * <p>
 * 각 에러 코드는 다음 세 가지 정보를 포함합니다:
 * <ul>
 * <li>{@code code} - 클라이언트에 노출되는 고유 에러 코드 (예: "C001")</li>
 * <li>{@code message} - 사람이 읽기 쉬운 에러 설명</li>
 * <li>{@code httpStatus} - 응답에 사용할 HTTP 상태 코드</li>
 * </ul>
 *
 * <p>
 * 에러는 도메인(접두사)별로 그룹화합니다:
 * <ul>
 * <li>C : Common (공통)</li>
 * <li>U : User (사용자)</li>
 * <li>A : Auth (인증/인가)</li>
 * </ul>
 *
 * <p>
 * 새 에러 코드 추가 시, 해당 도메인 그룹에 순차적인 번호로 추가합니다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ────────────────────────────────────────────────
    // Common (C)
    // ────────────────────────────────────────────────

    /** 요청 본문 또는 파라미터가 유효성 검사를 통과하지 못한 경우 */
    INVALID_INPUT_VALUE("C001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),

    /** 지원하지 않는 HTTP 메서드로 요청한 경우 */
    METHOD_NOT_ALLOWED("C002", "지원하지 않는 HTTP 메서드입니다.", HttpStatus.METHOD_NOT_ALLOWED),

    /** 요청한 리소스를 찾을 수 없는 경우 */
    RESOURCE_NOT_FOUND("C003", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /** 서버 내부에서 처리되지 않은 예외가 발생한 경우 */
    INTERNAL_SERVER_ERROR("C004", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // ────────────────────────────────────────────────
    // Auth (A)
    // ────────────────────────────────────────────────

    /** 인증 토큰이 없거나 형식이 잘못된 경우 */
    UNAUTHORIZED("A001", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),

    /** 인증은 됐으나 해당 리소스에 대한 권한이 없는 경우 */
    FORBIDDEN("A002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    /** 액세스 토큰이 만료된 경우 */
    EXPIRED_TOKEN("A003", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    /** 토큰 서명이 유효하지 않은 경우 */
    INVALID_TOKEN("A004", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),

    // ────────────────────────────────────────────────
    // User (U)
    // ────────────────────────────────────────────────

    /** 요청한 사용자가 존재하지 않는 경우 */
    USER_NOT_FOUND("U001", "사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    /** 이미 가입된 이메일로 회원가입을 시도하는 경우 */
    EMAIL_ALREADY_EXISTS("U002", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);

    // ────────────────────────────────────────────────

    /** 클라이언트에 전달되는 에러 식별자 (예: "C001") */
    private final String code;

    /** 사람이 읽기 쉬운 에러 설명 */
    private final String message;

    /** 응답에 사용할 HTTP 상태 코드 */
    private final HttpStatus httpStatus;

    /**
     * 다국어(i18n) 처리를 위한 프로퍼티 파일 메시지 키 반환 (예: "error.C001")
     *
     * @return messages.properties 등에 정의된 에러 키
     */
    public String getMessageKey() {
        return "error." + this.code;
    }
}
