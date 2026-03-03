package com.kezul.backend.global.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 감사(Audit) 로그가 필요한 메서드에 붙이는 어노테이션.
 * {@link AuditLoggingAspect}가 시작/완료/실패를 자동으로 구조화 로그로 기록합니다.
 *
 * <pre>
 * {@code
 * &#64;Audit(event = AuditEvent.PAYMENT_INITIATED)
 * public PaymentResult pay(Long userId, PaymentRequest request) { ... }
 *
 * // 개인정보가 파라미터에 있을 경우 maskArgs = true
 * &#64;Audit(event = AuditEvent.AUTH_LOGIN, maskArgs = true)
 * public LoginResult login(LoginRequest request) { ... }
 * }
 * </pre>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    AuditEvent event();

    /** 같은 이벤트 타입이어도 맥락을 더 명확히 할 때 사용. 기본값은 로그에 포함 안 됨. */
    String description() default "";

    /** true면 파라미터 실제 값 대신 타입명만 기록 (비밀번호/개인정보 보호) */
    boolean maskArgs() default false;
}
