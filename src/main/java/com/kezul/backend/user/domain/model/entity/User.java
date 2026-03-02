package com.kezul.backend.user.domain.model.entity;

import com.kezul.backend.global.entity.BaseTimeEntity;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import com.kezul.backend.user.domain.model.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자(User) 엔티티.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OauthProvider oauthProvider;

    @Column(nullable = false, length = 100)
    private String oauthId;

    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder
    public User(OauthProvider oauthProvider, String oauthId, String email,
            String nickname, String profileImageUrl, Role role) {
        this.oauthProvider = oauthProvider;
        this.oauthId = oauthId;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role != null ? role : Role.USER;
    }
}
