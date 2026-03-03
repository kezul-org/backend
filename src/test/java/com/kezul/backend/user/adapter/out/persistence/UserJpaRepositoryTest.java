package com.kezul.backend.user.adapter.out.persistence;

import com.kezul.backend.IntegrationTest;
import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserJpaRepository 및 BaseTimeEntity 통합 테스트.
 */
@IntegrationTest
class UserJpaRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    @DisplayName("User를 저장하면 BaseTimeEntity의 createdAt과 updatedAt이 자동으로 세팅된다")
    void saveUserAuditingTest() {
        // given
        User user = User.builder()
                .oauthProvider(OauthProvider.KAKAO)
                .oauthId("auditing_test_123")
                .nickname("오디팅테스터")
                .build();

        // when
        User savedUser = userJpaRepository.save(user);

        // then
        assertThat(savedUser.getId()).isNotNull(); // DB에서 발급된 PK
        assertThat(savedUser.getCreatedAt()).isNotNull(); // BaseTimeEntity 작동 확인
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isEqualTo(savedUser.getUpdatedAt()); // 최초 생성 시 같음
    }

    @Test
    @DisplayName("OauthProvider와 oauthId 복합키로 유저를 정상적으로 조회할 수 있다")
    void findByOauthIdAndOauthProviderTest() {
        // given
        User user = User.builder()
                .oauthProvider(OauthProvider.APPLE)
                .oauthId("apple_unique_123")
                .nickname("애플테스터")
                .build();
        userJpaRepository.save(user);

        // when
        Optional<User> found = userJpaRepository.findByOauthIdAndOauthProvider("apple_unique_123", OauthProvider.APPLE);
        Optional<User> notFoundProvider = userJpaRepository.findByOauthIdAndOauthProvider(
                "apple_unique_123",
                OauthProvider.KAKAO);
        Optional<User> notFoundId = userJpaRepository.findByOauthIdAndOauthProvider("wrong_id", OauthProvider.APPLE);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getNickname()).isEqualTo("애플테스터");

        assertThat(notFoundProvider).isEmpty();
        assertThat(notFoundId).isEmpty();
    }
}
