package com.kezul.backend.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kezul.backend.user.SocialUser;
import com.kezul.backend.user.application.port.out.UserPort;
import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import com.kezul.backend.user.domain.model.enums.Role;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserFinderImpl 서버 로직 테스트")
class UserFinderImplTest {

    @InjectMocks
    private UserFinderImpl userFinder;

    @Mock
    private UserPort userPort;

    @Test
    @DisplayName("findByOauthIdAndOauthProvider: 등록된 유저가 있을 때 DTO로 변환하여 반환한다.")
    void findByOauthIdAndOauthProvider_WhenUserExists() {
        // given
        String oauthId = "kakao123";
        OauthProvider provider = OauthProvider.KAKAO;

        User user = User.builder()
                .oauthId(oauthId)
                .oauthProvider(provider)
                .nickname("testUser")
                .role(Role.USER)
                .build();
        User spiedUser = spy(user);
        given(spiedUser.getId()).willReturn(1L);

        given(userPort.findByOauthIdAndOauthProvider(oauthId, provider))
                .willReturn(Optional.of(spiedUser));

        // when
        Optional<SocialUser> result = userFinder.findByOauthIdAndOauthProvider(oauthId, provider);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(1L);
        assertThat(result.get().roleName()).isEqualTo("USER");

        verify(userPort).findByOauthIdAndOauthProvider(oauthId, provider);
    }

    @Test
    @DisplayName("findByOauthIdAndOauthProvider: 등록된 유저가 없을 때 빈 Optional을 반환한다.")
    void findByOauthIdAndOauthProvider_WhenUserNotExists() {
        // given
        String oauthId = "nonExistent123";
        OauthProvider provider = OauthProvider.GOOGLE;

        given(userPort.findByOauthIdAndOauthProvider(oauthId, provider))
                .willReturn(Optional.empty());

        // when
        Optional<SocialUser> result = userFinder.findByOauthIdAndOauthProvider(oauthId, provider);

        // then
        assertThat(result).isEmpty();
        verify(userPort).findByOauthIdAndOauthProvider(oauthId, provider);
    }

    @Test
    @DisplayName("createUser: 새로운 유저 엔티티를 생성 및 DB에 저장하고 DTO로 변환하여 반환한다.")
    void createUser_Success() {
        // given
        String oauthId = "apple123";
        OauthProvider provider = OauthProvider.APPLE;

        User savedUser = User.builder()
                .oauthId(oauthId)
                .oauthProvider(provider)
                .nickname("temp")
                .role(Role.USER)
                .build();
        User spiedSavedUser = spy(savedUser);
        given(spiedSavedUser.getId()).willReturn(100L);

        given(userPort.save(any(User.class)))
                .willReturn(spiedSavedUser);

        // when
        SocialUser result = userFinder.createUser(oauthId, provider);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.roleName()).isEqualTo("USER");
        verify(userPort).save(any(User.class));
    }

    @Test
    @DisplayName("findById: User가 존재하면 SocialUser로 정상 맵핑되어 반환된다.")
    void findById_WhenExists() {
        // given
        Long userId = 999L;
        User user = User.builder()
                .oauthId("naver123")
                .oauthProvider(OauthProvider.NAVER)
                .nickname("test1")
                .role(Role.ADMIN)
                .build();
        User spiedUser = spy(user);
        given(spiedUser.getId()).willReturn(userId);

        given(userPort.findById(userId)).willReturn(Optional.of(spiedUser));

        // when
        Optional<SocialUser> result = userFinder.findById(userId);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().id()).isEqualTo(userId);
        assertThat(result.get().roleName()).isEqualTo("ADMIN");
        verify(userPort).findById(userId);
    }

    @Test
    @DisplayName("findById: User가 존재하지 않으면 빈 값을 반환한다.")
    void findById_WhenNotExists() {
        // given
        Long userId = 999L;
        given(userPort.findById(userId)).willReturn(Optional.empty());

        // when
        Optional<SocialUser> result = userFinder.findById(userId);

        // then
        assertThat(result).isEmpty();
    }
}
