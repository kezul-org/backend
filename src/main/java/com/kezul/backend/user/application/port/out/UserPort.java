package com.kezul.backend.user.application.port.out;

import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;

import java.util.Optional;

/**
 * User 도메인의 영속성 출력 포트.
 * Application Service가 DB 접근이 필요할 때 이 인터페이스를 통해 수행하며,
 * 실제 구현은 Adapter 계층(UserPersistenceAdapter)에서 담당한다.
 */
public interface UserPort {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByOauthIdAndOauthProvider(String oauthId, OauthProvider oauthProvider);
}
