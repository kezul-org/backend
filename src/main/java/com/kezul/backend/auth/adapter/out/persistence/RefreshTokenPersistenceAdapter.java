package com.kezul.backend.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.kezul.backend.auth.application.port.out.RefreshTokenPort;
import com.kezul.backend.auth.domain.model.entity.RefreshToken;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenPersistenceAdapter implements RefreshTokenPort {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(RefreshToken refreshToken) {
        refreshTokenJpaRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByTokenValue(String tokenValue) {
        return refreshTokenJpaRepository.findByTokenValue(tokenValue);
    }

    @Override
    public void deleteByUserId(Long userId) {
        refreshTokenJpaRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteByTokenValue(String tokenValue) {
        refreshTokenJpaRepository.deleteByTokenValue(tokenValue);
    }
}
