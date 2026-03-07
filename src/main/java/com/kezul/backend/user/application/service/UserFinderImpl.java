package com.kezul.backend.user.application.service;

/**
 * UserFinder 통신 규약의 구체적인 구현체.
 * User 엔티티 생명주기 및 조회 처리를 담당하며, 외부에는 노출되지 않습니다.
 */
import com.kezul.backend.user.SocialUser;
import com.kezul.backend.user.UserFinder;
import com.kezul.backend.user.application.port.out.UserPort;
import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import com.kezul.backend.user.domain.model.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFinderImpl implements UserFinder {
    private final UserPort userPort;

    @Override
    public Optional<SocialUser> findByOauthIdAndOauthProvider(String oauthId, OauthProvider provider) {
        return userPort.findByOauthIdAndOauthProvider(oauthId, provider)
                .map(SocialUser::from);
    }

    @Override
    @Transactional
    public SocialUser createUser(String oauthId, OauthProvider provider) {
        User user = User.builder()
                .role(Role.USER)
                .oauthId(oauthId)
                .oauthProvider(provider)
                .nickname("temp")
                .build();

        User savedUser = userPort.save(user);
        return SocialUser.from(savedUser);
    }

    @Override
    public Optional<SocialUser> findById(Long userId) {
        return userPort.findById(userId)
                .map(SocialUser::from);
    }
}
