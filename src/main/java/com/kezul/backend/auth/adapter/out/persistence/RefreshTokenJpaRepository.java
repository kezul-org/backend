package com.kezul.backend.auth.adapter.out.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kezul.backend.auth.domain.model.entity.RefreshToken;

@Repository
public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenValue(String tokenValue);

    void deleteByUserId(Long userId);

    void deleteByTokenValue(String tokenValue);
}
