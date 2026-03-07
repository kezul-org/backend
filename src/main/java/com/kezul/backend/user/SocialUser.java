package com.kezul.backend.user;

import com.kezul.backend.user.domain.model.entity.User;

/**
 * 모듈 간 통신(특히 auth 모듈)을 위해 User Entity 정보를 최소화하여 노출하는 DTO.
 * Entity 노출 부작용(Lazyloading, Dirty Checking)을 방지합니다.
 */
public record SocialUser(
        Long id,
        String roleName) {
    public static SocialUser from(User user) {
        return new SocialUser(user.getId(), user.getRole().name());
    }
}
