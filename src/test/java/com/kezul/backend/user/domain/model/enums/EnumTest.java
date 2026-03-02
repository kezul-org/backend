package com.kezul.backend.user.domain.model.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Role과 OauthProvider Enum에 대한 간단한 단위 테스트.
 *
 * [단위 테스트 (Unit Test)]
 * Spring Context(@SpringBootTest 등)를 띄우지 않고,
 * 순수 Java 환경에서 매우 빠르고 고립된 상태로 코드 로직만 검증합니다.
 */
class EnumTest {

    @Test
    @DisplayName("Role Enum이 Spring Security에서 요구하는 'ROLE_' 접두사를 제대로 가지고 있는지 확인")
    void rolePrefixTest() {
        // given & when
        String userKey = Role.USER.getKey();
        String trainerKey = Role.TRAINER.getKey();
        String adminKey = Role.ADMIN.getKey();

        // then
        assertThat(userKey).startsWith("ROLE_").isEqualTo("ROLE_USER");
        assertThat(trainerKey).startsWith("ROLE_").isEqualTo("ROLE_TRAINER");
        assertThat(adminKey).startsWith("ROLE_").isEqualTo("ROLE_ADMIN");
    }

    @Test
    @DisplayName("생성된 Enum 값들이 존재하는지 갯수로 간단히 확인")
    void enumValuesExistTest() {
        // OauthProvider: KAKAO, NAVER, APPLE, GOOGLE (4개)
        assertThat(OauthProvider.values()).hasSize(4);

        // Role: USER, TRAINER, ADMIN (3개)
        assertThat(Role.values()).hasSize(3);
    }
}
