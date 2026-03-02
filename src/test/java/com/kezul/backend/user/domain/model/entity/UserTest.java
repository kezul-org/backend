package com.kezul.backend.user.domain.model.entity;

import com.kezul.backend.user.domain.model.enums.OauthProvider;
import com.kezul.backend.user.domain.model.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User 엔티티의 순수 자바(로직) 단위 테스트.
 * DB 연결 없이 @Builder 로직, 기본값 세팅 등이 정상 동작하는지 확인합니다.
 */
class UserTest {

    @Test
    @DisplayName("User 빌더로 객체 생성 시 role 값이 주어지지 않으면 기본값 USER가 세팅된다")
    void roleDefaultValueTest() {
        // given & when
        User user = User.builder()
                .oauthProvider(OauthProvider.KAKAO)
                .oauthId("123456789")
                .nickname("상훈")
                // role을 명시적으로 넣지 않음
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getNickname()).isEqualTo("상훈");
        assertThat(user.getOauthProvider()).isEqualTo(OauthProvider.KAKAO);
    }

    @Test
    @DisplayName("User 빌더로 명시적 role 값을 주면 해당 값으로 세팅된다")
    void explicitRoleTest() {
        // given & when
        User user = User.builder()
                .oauthProvider(OauthProvider.APPLE)
                .oauthId("apple_id_here")
                .nickname("어드민상훈")
                .role(Role.ADMIN) // 명시적 주입
                .build();

        // then
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }
}
