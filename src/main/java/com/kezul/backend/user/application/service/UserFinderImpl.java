package com.kezul.backend.user.application.service;

// <editor-fold desc="[접어두기] TODO: (사용자 학습 4/5) UserFinderImpl 구현체 구현">
// 왜? UserFinder를 implements 하여 내부 엔티티 조회 및 생성을 담당합니다. UserPort를 주입받으세요.
// 힌트:
// 1) findByOauthIdAndProvider: userPort.findByOauthIdAndOauthProvider 조회 후
// SocialUser로 매핑 (Role.name() 사용)
// 2) createOauthUser: builder()로 User 엔티티 생성(role은 Role.USER) 후 userPort.save()
// 저장 결과를 SocialUser로 매핑
// </editor-fold>

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
