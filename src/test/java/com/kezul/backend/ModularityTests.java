package com.kezul.backend;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Spring Modulith 아키텍처 검증 테스트.
 * 모듈 간 허용되지 않은 의존성이 생길 경우 이 테스트가 자동으로 실패하여 빌드를 막습니다.
 */
class ModularityTests {

    ApplicationModules modules = ApplicationModules.of(BackendApplication.class);

    @Test
    void verifiesModularStructure() {
        modules.verify();
    }
}
