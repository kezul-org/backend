package com.kezul.backend.auth.domain.model.entity;

import java.time.Instant;

import com.kezul.backend.global.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "idx_refresh_tokens_user_id", columnList = "userId"),
        @Index(name = "idx_refresh_tokens_token_value", columnList = "tokenValue", unique = true)
})
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 512, unique = true)
    private String tokenValue;

    @Column(length = 255)
    private String deviceInfo;

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder
    public RefreshToken(Long userId, String tokenValue, String deviceInfo, Instant expiresAt) {
        this.userId = userId;
        this.tokenValue = tokenValue;
        this.deviceInfo = deviceInfo;
        this.expiresAt = expiresAt;
    }

    public void updateToken(String newTokenValue, Instant newExpiresAt) {
        this.tokenValue = newTokenValue;
        this.expiresAt = newExpiresAt;
    }
}
