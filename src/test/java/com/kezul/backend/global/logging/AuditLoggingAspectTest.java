package com.kezul.backend.global.logging;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import com.kezul.backend.IntegrationTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@IntegrationTest
@ExtendWith(OutputCaptureExtension.class)
class AuditLoggingAspectTest {

    @Autowired
    private DummyService dummyService;

    @Test
    @DisplayName("정상 실행 시 시작(INFO) 로그와 완료(INFO) 로그가 남는다")
    void logAudit_Success(CapturedOutput output) {
        // when
        String result = dummyService.normalMethod("test", 123L);

        // then
        assertThat(result).isEqualTo("success");

        // Assert start log
        assertThat(output.getOut())
                .contains("INFO")
                .contains("event=\"PAYMENT_INITIATED\"")
                .contains("class=\"DummyService\"")
                .contains("method=\"normalMethod\"")
                .contains("args=\"[test, 123]\"")
                .contains("Audit event started");

        // Assert complete log
        assertThat(output.getOut())
                .contains("INFO")
                .contains("Audit event completed")
                .contains("durationMs=\"");
    }

    @Test
    @DisplayName("예외 발생 시 에러(ERROR) 로그가 남고 예외는 다시 던져진다")
    void logAudit_Exception(CapturedOutput output) {
        // when & then
        assertThatThrownBy(() -> dummyService.exceptionMethod())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid argument");

        // Assert start log
        assertThat(output.getOut())
                .contains("INFO")
                .contains("event=\"AUTH_LOGIN\"")
                .contains("method=\"exceptionMethod\"")
                .contains("Audit event started");

        // Assert error log
        assertThat(output.getOut())
                .contains("ERROR")
                .contains("event=\"AUTH_LOGIN\"")
                .contains("durationMs=\"")
                .contains("Audit event failed")
                .contains("java.lang.IllegalArgumentException: Invalid argument");
    }

    @Test
    @DisplayName("maskArgs = true 이면 파라미터 실제 값 대신 타입명만 기록된다")
    void logAudit_MaskArgs(CapturedOutput output) {
        // when
        dummyService.maskedMethod("secret-token", "password123");

        // then
        assertThat(output.getOut())
                .contains("event=\"USER_JOINED\"")
                .contains("args=\"[String, String]\"")
                .doesNotContain("secret-token")
                .doesNotContain("password123")
                .contains("class=\"DummyService\"")
                .contains("method=\"maskedMethod\"");
    }

    @Test
    @DisplayName("description 속성이 있으면 로그에 description 키가 추가된다")
    void logAudit_WithDescription(CapturedOutput output) {
        // when
        dummyService.methodWithDescription();

        // then
        assertThat(output.getOut())
                .contains("event=\"ADMIN_USER_SUSPENDED\"")
                .contains("description=\"테스트용 설명입니다\"")
                .contains("Audit event started");
    }

    // --- Test Configuration & Beans ---

    @Configuration
    @EnableAspectJAutoProxy
    @Import({AuditLoggingAspect.class, DummyService.class})
    static class TestConfig {
        // 필요한 빈만 등록하여 가벼운 컨텍스트 사용
    }

    @Service
    static class DummyService {

        @Audit(event = AuditEvent.PAYMENT_INITIATED)
        public String normalMethod(String arg1, Long arg2) {
            return "success";
        }

        @Audit(event = AuditEvent.AUTH_LOGIN)
        public void exceptionMethod() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @Audit(event = AuditEvent.USER_JOINED, maskArgs = true)
        public void maskedMethod(String token, String password) {
            // maskArgs 테스트
        }

        @Audit(event = AuditEvent.ADMIN_USER_SUSPENDED, description = "테스트용 설명입니다")
        public void methodWithDescription() {
            // description 테스트
        }
    }
}
