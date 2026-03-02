package com.kezul.backend.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kezul.backend.global.error.ErrorCode;

/**
 * 모든 API 응답의 공통 래퍼(Wrapper).
 *
 * <p>성공 응답과 실패 응답을 동일한 껍데기로 감싸 클라이언트가 일관된 포맷을 기대할 수 있도록 합니다.
 *
 * <p>성공 응답 예시:
 * <pre>{@code
 * {
 * "success": true,
 * "data": { ... }
 * }
 * }</pre>
 *
 * <p>실패 응답 예시:
 * <pre>{@code
 * {
 * "success": false,
 * "code": "U001",
 * "message": "사용자를 찾을 수 없습니다."
 * }
 * }</pre>
 *
 * @param <T> 성공 응답 시 담길 데이터의 타입
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonResponse<T>(
        @JsonProperty("success") boolean isSuccess,
        T data,
        String code,
        String message,
        String traceId // 에러 응답에서만 채워짐 — 클라이언트가 문의 시 전달하면 로그에서 즉시 추적 가능
) {

    /**
     * 데이터가 있는 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return 성공 응답
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(true, data, null, null, null);
    }

    /**
     * 데이터 없는 성공 응답을 생성합니다 (생성/삭제 작업 등).
     *
     * @return 성공 응답
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(true, null, null, null, null);
    }

    /**
     * {@link ErrorCode}를 기반으로 실패 응답을 생성합니다.
     *
     * @param errorCode 발생한 에러 코드
     * @param <T>       데이터 타입 (실패 시 null)
     * @return 실패 응답
     */
    public static <T> CommonResponse<T> fail(ErrorCode errorCode, String traceId) {
        return new CommonResponse<>(false, null, errorCode.getCode(), errorCode.getMessage(), traceId);
    }

    /**
     * 에러 코드와 커스텀 메시지로 실패 응답을 생성합니다.
     * 유효성 검사 실패 등 에러 코드의 기본 메시지 대신 구체적인 메시지를 제공할 때 사용합니다.
     *
     * @param errorCode 발생한 에러 코드
     * @param message   클라이언트에 노출할 커스텀 메시지
     * @param <T>       데이터 타입 (실패 시 null)
     * @return 실패 응답
     */
    public static <T> CommonResponse<T> fail(ErrorCode errorCode, String message, String traceId) {
        return new CommonResponse<>(false, null, errorCode.getCode(), message, traceId);
    }

    /**
     * 다국어(i18n) 처리가 완료된 코드와 메시지로 실패 응답을 생성합니다.
     *
     * @param code    발생한 에러의 코드 문자열 (예: "U001")
     * @param message 다국어 처리가 완료된 에러 메시지
     * @param traceId 추적을 위한 ID
     * @param <T>     데이터 타입 (실패 시 null)
     * @return 실패 응답
     */
    public static <T> CommonResponse<T> fail(String code, String message, String traceId) {
        return new CommonResponse<>(false, null, code, message, traceId);
    }
}
