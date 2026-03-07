package com.kezul.backend.user;

import com.kezul.backend.user.domain.model.entity.User;

// <editor-fold defaultstate="collapsed" desc="[접어두기] TODO: (사용자 학습 2/5)
// SocialUser DTO 레코드 생성">
// 전체 흐름: 모듈 간 통신에서 데이터가 오갈 때, 내부 구현체(User 엔티티) 노출을 막기 위해 전용 통신 객체를 만드는 통로 설계
// 단계입니다.
// 왜? auth 모듈이 user 모듈 내부의 User 엔티티를 직접 참조하지 않고 최소한의 정보만 받도록 인터페이스용 DTO를 만듭니다.
// 힌트: Long id, String roleName 필드를 가진 record 로 생성하세요.
// </editor-fold>
public record SocialUser(
        Long id,
        String roleName) {
    public static SocialUser from(User user) {
        return new SocialUser(user.getId(), user.getRole().name());
    }
}
