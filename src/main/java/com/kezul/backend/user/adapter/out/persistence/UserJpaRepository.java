package com.kezul.backend.user.adapter.out.persistence;

import com.kezul.backend.user.domain.model.entity.User;
import com.kezul.backend.user.domain.model.enums.OauthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User 엔티티의 데이터 접근 계층 (Spring Data JPA).
 * 
 * [JpaRepository<엔티티 타입, PK 타입>]
 * 상속만 받으면 기본적인 CRUD 메서드(save, findById, delete 등)가 자동 생성됨.
 */
public interface UserJpaRepository extends JpaRepository<User, Long> {

    /**
     * Oauth 제공자(KAKAO 등)와 해당 제공자의 고유 ID를 통해 유저를 조회합니다.
     * -> 향후 Auth 서비스에서 "이미 가입한 유저인지" 식별할 때 가장 핵심이 되는 쿼리 메서드
     *
     * @param oauthId       소셜 로그인 서버에서 발급한 고유 회원 번호
     * @param oauthProvider 소셜 로그인 제공자 enum
     * @return 일치하는 User 객체를 Optional로 반환
     */
    Optional<User> findByOauthIdAndOauthProvider(String oauthId, OauthProvider oauthProvider);
}
