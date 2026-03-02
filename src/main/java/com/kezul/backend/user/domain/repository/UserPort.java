package com.kezul.backend.user.domain.repository;

import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;

import java.util.Optional;

public interface UserPort {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByOauthIdAndOauthProvider(String oauthId, OauthProvider oauthProvider);
}
